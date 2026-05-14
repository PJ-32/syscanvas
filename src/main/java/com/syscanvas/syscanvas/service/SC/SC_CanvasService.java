package com.syscanvas.syscanvas.service.SC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.dto.EstadisticasCanvas;
import com.syscanvas.syscanvas.dto.SC.*;
import com.syscanvas.syscanvas.exception.ExcepcionRecursoNoEncontrado;
import com.syscanvas.syscanvas.exception.ExcepcionValidacion;
import com.syscanvas.syscanvas.mapper.SC.SC_CanvasMapper;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.helper.CalculadorEstadisticasCanvas;
import com.syscanvas.syscanvas.validator.SC.SC_CanvasValidator;

import org.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(transactionManager = "scTransactionManager")
public class SC_CanvasService {

    private static final Logger logger = LoggerFactory.getLogger(SC_CanvasService.class);

    private final SC_CanvasDAO canvasDAO;
    private final SC_EtapaDAO etapaDAO;
    private final SC_TareaDAO tareaDAO;
    private final SC_EtapaService etapaService;

    private final SC_CanvasMapper canvasMapper;
    private final SC_CanvasValidator canvasValidator;
    private final CalculadorEstadisticasCanvas calculadorEstadisticas;

    public SC_CanvasService(
            SC_CanvasDAO canvasDAO,
            SC_EtapaDAO etapaDAO,
            SC_TareaDAO tareaDAO,
            SC_EtapaService etapaService,
            SC_CanvasMapper canvasMapper,
            SC_CanvasValidator canvasValidator,
            CalculadorEstadisticasCanvas calculadorEstadisticas
    ) {
        this.canvasDAO = canvasDAO;
        this.etapaDAO = etapaDAO;
        this.tareaDAO = tareaDAO;
        this.etapaService = etapaService;
        this.canvasMapper = canvasMapper;
        this.canvasValidator = canvasValidator;
        this.calculadorEstadisticas = calculadorEstadisticas;
    }

    // ==========================================================
    // =============== CONSULTAS =================================
    // ==========================================================

    @Transactional(readOnly = true)
    public Page<SC_CanvasDTO> listarTodos(Pageable pageable) {
        Page<SC_Canvas> pageCanvas = canvasDAO.findAll(pageable);

        List<SC_CanvasDTO> canvasDTO = pageCanvas.getContent()
                .stream()
                .map(this::enriquecerConEstadisticas)
                .collect(Collectors.toList());

        return new PageImpl<>(canvasDTO, pageable, pageCanvas.getTotalElements());
    }

    /**Versión completa con etapas+ tareas */
    @Transactional(readOnly = true)
public SC_CanvasDTO buscarPorId(Long codCanvas) {

    SC_Canvas canvas = buscarCanvasOLanzarExcepcion(codCanvas);
    SC_CanvasDTO dto = enriquecerConEstadisticas(canvas);

    List<SC_Etapa> etapas = obtenerEtapasConTareas(codCanvas);
    
    etapas.sort(Comparator.comparing(SC_Etapa::getNumEtapa));

    dto.setEtapasPersonalizadas(etapas);

    return dto;
}


    public SC_Canvas buscarEntidad(Long codCanvas) {
        return canvasDAO.findById(codCanvas)
                .orElseThrow(() ->
                        new ExcepcionRecursoNoEncontrado("Canvas", "codCanvas", codCanvas));
    }

    @Transactional(readOnly = true)
    public Page<SC_CanvasDTO> buscarPorProyecto(Long codProyecto, Pageable pageable) {
        List<SC_Canvas> canvas = canvasDAO.findByCodPyto(codProyecto);
        return paginarYMapear(canvas, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SC_CanvasDTO> buscarPorPersona(Long codPersona, Pageable pageable) {
        List<SC_Canvas> canvas = canvasDAO.findByCodPersona(codPersona);
        return paginarYMapear(canvas, pageable);
    }

    /** Obtiene etapas con sus tareas */
    @Transactional(readOnly = true)
public List<SC_Etapa> obtenerEtapasConTareas(Long codCanvas) {

    List<SC_Etapa> etapas = etapaDAO.findByCanvas_CodCanvas(codCanvas);

    for (SC_Etapa etapa : etapas) {
        List<SC_Tarea> tareas = tareaDAO.findByEtapa_CodEtapa(etapa.getCodEtapa());
        etapa.setTareas(tareas);
    }

    return etapas;
}

    // ==========================================================
    // =============== CRUD =====================================
    // ==========================================================

    public SC_CanvasDTO guardar(SC_CanvasDTO dto) {

        canvasValidator.validarCanvas(dto);
        canvasValidator.validarNombreUnico(dto.getNomCanvas(), null);

        SC_Canvas canvas = canvasMapper.toEntity(dto);
        LocalDateTime now = LocalDateTime.now();
        canvas.setFecCreacion(now);
        canvas.setFecModificacion(now);
        canvas.setEditable(true);

        SC_Canvas guardado = canvasDAO.save(canvas);

        String tipo = guardado.getTipoCanvas().getTipCanvas();
        if (!"F".equals(tipo)) {
            // Para plantillas, seguimos usando la fábrica (es lógica interna segura)
            List<SC_Etapa> etapasBase = SC_EtapaPlantillaFactory.crearEtapasPorTipo(guardado);
            etapaDAO.saveAll(etapasBase);
        } else {
            // Para Canvas LIBRE, usamos el SERVICIO para validar la entrada del usuario
            if (dto.getEtapasPersonalizadas() != null) {
                for (SC_Etapa etapaOriginal : dto.getEtapasPersonalizadas()) {
                    SC_Etapa nuevaEtapa = new SC_Etapa();
                    nuevaEtapa.setNomEtapa(etapaOriginal.getNomEtapa());
                    nuevaEtapa.setDesEtapa(etapaOriginal.getDesEtapa());
                    nuevaEtapa.setNumEtapa(etapaOriginal.getNumEtapa());
                    nuevaEtapa.setCanvas(guardado);
                    nuevaEtapa.setFecCreacion(now);
                    nuevaEtapa.setFecModificacion(now);
                    
                    // 🔥 LLAMADA AL SERVICIO: Aquí se ejecutan tus validaciones de SC_EtapaService
                    etapaService.guardar(nuevaEtapa); 
                }
            }
        }

        return enriquecerConEstadisticas(guardado);
    }

    public SC_CanvasDTO actualizar(Long codCanvas, SC_CanvasDTO dto) {

        SC_Canvas existente = buscarCanvasOLanzarExcepcion(codCanvas);

        canvasValidator.validarCanvas(dto);
        canvasValidator.validarNombreUnico(dto.getNomCanvas(), codCanvas);

        actualizarCampos(existente, dto);
        existente.setFecModificacion(LocalDateTime.now());

        SC_Canvas actualizado = canvasDAO.save(existente);

        return enriquecerConEstadisticas(actualizado);
    }

    // ==========================================================
    // =============== EXPORTAR / IMPORTAR ======================
    // ==========================================================

    public String exportarCanvas(Long id) throws IOException {

        SC_Canvas canvas = buscarCanvasOLanzarExcepcion(id);
        SC_CanvasDTO dto = canvasMapper.toDTO(canvas);

        List<SC_Etapa> etapas = obtenerEtapasConTareas(id);
        dto.setEtapasPersonalizadas(etapas);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
    }

    public SC_CanvasDTO importarCanvas(MultipartFile file, Long usuarioActualId) throws IOException {

    String json = new String(file.getBytes(), StandardCharsets.UTF_8);

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Leer JSON como DTO original
    SC_CanvasDTO dtoImportado = mapper.readValue(json, SC_CanvasDTO.class);

    // ==========================================
    // CREAR NUEVO CANVAS
    // ==========================================
    SC_Canvas nuevoCanvas = new SC_Canvas();
    nuevoCanvas.setNomCanvas(dtoImportado.getNomCanvas());
    nuevoCanvas.setDesCanvas(dtoImportado.getDesCanvas());
    
    // Tipo de canvas desde el DTO
    SC_Tipo_Canvas tipo = new SC_Tipo_Canvas();
    tipo.setTipCanvas(dtoImportado.getTipoCanvas().getTipCanvas());
    tipo.setDesTipCanvas(dtoImportado.getTipoCanvas().getDesTipCanvas());
    tipo.setVigente(1);
    nuevoCanvas.setTipoCanvas(tipo);

    nuevoCanvas.setEditable(true);

    // 🔥 CODPERSONA OBLIGATORIO
    nuevoCanvas.setCodPersona(usuarioActualId);

    // Si tu estructura requiere codPyto:
    if (dtoImportado.getCodPyto() != null) {
        nuevoCanvas.setCodPyto(dtoImportado.getCodPyto());
    }

    // Estado por defecto: Activo (1)
    SC_Estado estado = new SC_Estado();
    estado.setCodEstado(1);
    nuevoCanvas.setEstado(estado);

    nuevoCanvas.setFecCreacion(LocalDateTime.now());
    nuevoCanvas.setFecModificacion(LocalDateTime.now());

    // Guardamos el canvas
    SC_Canvas canvasGuardado = canvasDAO.save(nuevoCanvas);

    // ==========================================
    // IMPORTAR ETAPAS
    // ==========================================
    if (dtoImportado.getEtapasPersonalizadas() != null) {

        for (SC_Etapa etapaOriginal : dtoImportado.getEtapasPersonalizadas()) {

            SC_Etapa nuevaEtapa = new SC_Etapa();
            nuevaEtapa.setNomEtapa(etapaOriginal.getNomEtapa());
            nuevaEtapa.setDesEtapa(etapaOriginal.getDesEtapa());
            nuevaEtapa.setNumEtapa(etapaOriginal.getNumEtapa());
            nuevaEtapa.setVigente(1);
            nuevaEtapa.setCanvas(canvasGuardado);

            nuevaEtapa.setFecCreacion(LocalDateTime.now());
            nuevaEtapa.setFecModificacion(LocalDateTime.now());

            SC_Etapa etapaGuardada = etapaDAO.save(nuevaEtapa);

            // ==========================================
            // IMPORTAR TAREAS
            // ==========================================
            if (etapaOriginal.getTareas() != null) {
                for (SC_Tarea original : etapaOriginal.getTareas()) {

                    SC_Tarea nuevaTarea = new SC_Tarea();
                    nuevaTarea.setNomTarea(original.getNomTarea());
                    nuevaTarea.setDesTarea(original.getDesTarea());
                    nuevaTarea.setNumTarea(original.getNumTarea());
                    nuevaTarea.setEstado("PENDIENTE");
                    nuevaTarea.setVigente(1L);

                    nuevaTarea.setEtapa(etapaGuardada);

                    // 🔥 NECESARIO: Tarea también requiere codPersona
                    nuevaTarea.setCodPersona(usuarioActualId);

                    nuevaTarea.setFecCreacion(LocalDateTime.now());
                    nuevaTarea.setFecModificacion(LocalDateTime.now());

                    tareaDAO.save(nuevaTarea);
                }
            }
        }
    }

    // Devolver DTO final
    SC_CanvasDTO finalDTO = canvasMapper.toDTO(canvasGuardado);
    return finalDTO;
}

    // ==========================================================
    // =============== ELIMINAR =================================
    // ==========================================================

    @Transactional
    public void eliminar(Long codCanvas) {

    // 1️⃣ Buscar el canvas
    SC_Canvas canvas = buscarCanvasOLanzarExcepcion(codCanvas);

    // 2️⃣ Obtener las etapas del canvas
    List<SC_Etapa> etapas = etapaDAO.findByCanvas_CodCanvas(codCanvas);

    // 3️⃣ Eliminar tareas de cada etapa
    for (SC_Etapa etapa : etapas) {
        List<SC_Tarea> tareas = tareaDAO.findByEtapa_CodEtapa(etapa.getCodEtapa());
        tareaDAO.deleteAll(tareas);
    }

    // 4️⃣ Eliminar las etapas
    etapaDAO.deleteAll(etapas);

    // 5️⃣ Ahora sí → eliminar el canvas
    canvasDAO.delete(canvas);
}

    public SC_CanvasDTO cambiarEstadoEditable(Long codCanvas, Boolean editable) {

    SC_Canvas canvas = buscarCanvasOLanzarExcepcion(codCanvas);

    // Si no viene valor ⇒ toggle automático
    if (editable == null) {
        editable = !canvas.getEditable();
    }

    canvas.setEditable(editable);
    canvas.setFecModificacion(LocalDateTime.now());

    SC_Canvas actualizado = canvasDAO.save(canvas);
    return enriquecerConEstadisticas(actualizado);
}

    // ==========================================================
    // =============== PRIVADOS =================================
    // ==========================================================

    private SC_Canvas buscarCanvasOLanzarExcepcion(Long codCanvas) {
        return canvasDAO.findById(codCanvas)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                        "Canvas", "codCanvas", codCanvas));
    }

    private SC_CanvasDTO enriquecerConEstadisticas(SC_Canvas canvas) {
        SC_CanvasDTO dto = canvasMapper.toDTO(canvas);

        EstadisticasCanvas stats = calculadorEstadisticas.calcular(canvas);
        dto.setTotalTareas(stats.totalTareas());
        dto.setTareasCompletadas(stats.tareasCompletadas());
        dto.setPorcentajeProgreso(stats.porcentajeProgreso());
        dto.setTotalComentarios(stats.totalComentarios());

        return dto;
    }

    private Page<SC_CanvasDTO> paginarYMapear(List<SC_Canvas> canvas, Pageable pageable) {

        int inicio = (int) pageable.getOffset();
        int fin = Math.min(inicio + pageable.getPageSize(), canvas.size());

        List<SC_CanvasDTO> canvasDTO = canvas.subList(inicio, fin).stream()
                .map(this::enriquecerConEstadisticas)
                .collect(Collectors.toList());

        return new PageImpl<>(canvasDTO, pageable, canvas.size());
    }

    private void actualizarCampos(SC_Canvas canvas, SC_CanvasDTO dto) {

        if (dto.getNomCanvas() != null) canvas.setNomCanvas(dto.getNomCanvas());
        if (dto.getDesCanvas() != null) canvas.setDesCanvas(dto.getDesCanvas());
        if (dto.getEditable() != null) canvas.setEditable(dto.getEditable());
        if (dto.getCodPyto() != null) canvas.setCodPyto(dto.getCodPyto());
        if (dto.getCodPersona() != null) canvas.setCodPersona(dto.getCodPersona());
    }
}
