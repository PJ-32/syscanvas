--- PROCEDIMIENTO CRUDTRAMO
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.CRUDTRAMO (
    CodPyto_i IN NUMERIC ,
    codRutaPy_i IN NUMERIC,
    codTramoPy_i IN NUMERIC,
    NroVersion_i IN NUMERIC,
    CodTramo_i IN VARCHAR2,
    fechaRegistro_i IN DATE,
    denominacionTramo_i IN VARCHAR2, 
    denominacionCortoTramo_i IN VARCHAR2,
    NroKmsTramo_i IN NUMERIC,
    ZonaGPS_i  IN VARCHAR2,
    progInicio_i IN VARCHAR2,
    progFinal_i IN VARCHAR2,
    latitudInicioTramo_i IN VARCHAR2,
    latitudFinalTramo_i IN VARCHAR2,
    longitudInicioTramo_i IN VARCHAR2,
    longitudFinalTramo_i IN VARCHAR2,
    altitudInicioTramo_i IN VARCHAR2,
    altitudFinalTramo_i IN VARCHAR2,
    observacionTramo_i IN VARCHAR2,
    vigencia_i IN VARCHAR2,
    ACCION IN VARCHAR2
    ) IS
BEGIN
    CASE ACCION
        WHEN 'INSERTAR' THEN 
        INSERT INTO T_TRAMO (CodPyto,codRutaPy,codTramoPy,NroVersion,CodTramo,fechaRegistro,
            denominacionTramo, denominacionCortoTramo,NroKmsTramo,ZonaGPS,progInicio,progFin,
            latitudInicioTramo,latitudFinalTramo,longitudInicioTramo,longitudFinalTramo,
            altitudInicioTramo,altitudFinalTramo,observacionTramo,vigencia) 
        VALUES (CodPyto_i,codRutaPy_i,codTramoPy_i,nroversion_i,CodTramo_i,fechaRegistro_i,
            denominacionTramo_i, denominacionCortoTramo_i,NroKmsTramo_i,ZonaGPS_i,progInicio_i,progFinal_i,
            latitudInicioTramo_i,latitudFinalTramo_i,longitudInicioTramo_i,longitudFinalTramo_i,
            altitudInicioTramo_i,altitudFinalTramo_i,observacionTramo_i,vigencia_i); 
        WHEN 'MODIFICAR' THEN 
        UPDATE t_tramo SET codtramo = CodTramo_i,  fechaRegistro = fechaRegistro_i, denominacionTramo = denominacionTramo_i,
            denominacionCortoTramo = denominacionCortoTramo_i, nrokmstramo=nrokmstramo_i, zonagps=zonagps_i, progInicio= progInicio_i,
            progFin=progFinal_i, latitudInicioTramo = latitudInicioTramo_i, latitudFinalTramo = latitudFinalTramo_i, longitudInicioTramo = longitudInicioTramo_i,
            longitudFinalTramo = longitudFinalTramo_i, altitudInicioTramo = altitudInicioTramo_i, altitudFinalTramo = altitudFinalTramo_i, observaciontramo = observaciontramo_i,
            vigencia = vigencia_i WHERE codpyto = codpyto_i AND codrutapy = codrutapy_i AND codtramopy = codtramopy_i AND nroversion = nroversion_i;
        WHEN 'ELIMINAR' THEN
        DELETE FROM T_Tramo WHERE codpyto = codpyto_i AND codrutapy = codrutapy_i AND codtramopy = codtramopy_i AND nroversion = nroversion_i;
    END CASE ;
END;
/

--- PROCEDIMIENTO DELETERUTA
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.DELETERUTA (
    CodPyto_i IN NUMERIC, codRutaPy_i IN NUMERIC, NroVersion_i IN NUMERIC ) IS
BEGIN
    DELETE FROM T_RUTA WHERE CodPyto=CodPyto_i AND codRutaPy=codRutaPy_i AND NroVersion = NroVersion_i;
END;
/

--- PROCEDIMIENTO INSERTSUBTRAMO
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.INSERTSUBTRAMO (
    CodPyto_i IN NUMERIC ,
    codRutaPy_i IN NUMERIC,
    codTramoPy_i IN NUMERIC,
    codSubtramoPy_i IN NUMERIC,
    NroVersion_i IN NUMERIC,
    CodSubTramo_i IN VARCHAR2,
    fechaRegistro_i IN DATE,
    denominacionSubtramo_i IN VARCHAR2, 
    denominacionCortoSubtramo_i IN VARCHAR2,
    NroKmssubTramo_i IN NUMERIC,
    ZonaGPS_i  IN VARCHAR2,
    progInicio_i IN VARCHAR2,
    progFinal_i IN VARCHAR2,
    latitudInicioSubtramo_i IN VARCHAR2,
    latitudFinalSubtramo_i IN VARCHAR2,
    longitudInicioSubtramo_i IN VARCHAR2,
    longitudFinalSubtramo_i IN VARCHAR2,
    altitudInicioSubtramo_i IN VARCHAR2,
    altitudFinalSubtramo_i IN VARCHAR2,
    observacionsubTramo_i IN VARCHAR2,
    vigencia_i IN VARCHAR2 
    ) IS
BEGIN
    INSERT INTO T_SUBTRAMO (CodPyto,codRutaPy,codTramoPy,codSubTramoPy,NroVersion,CodSubTramo,
        fechaRegistro,denominacionSubtramo, denominacionCortoSubtramo,NroKmsSubTramo,ZonaGPS,progInicio,
        progFin,latitudInicioSubtramo,latitudFinalSubtramo,longitudInicioSubtramo,longitudFinalSubtramo,
        altitudInicioSubtramo,altitudFinalSubtramo,observacionSubtramo,vigencia) 
    VALUES (CodPyto_i,codRutaPy_i,codTramoPy_i,codSubTramoPy_i,2,CodSubTramo_i, fechaRegistro_i,denominacionSubtramo_i,
        denominacionCortoSubtramo_i,NroKmsSubTramo_i,ZonaGPS_i,progInicio_i, progFinal_i,latitudInicioSubtramo_i,
        latitudFinalSubtramo_i,longitudInicioSubtramo_i,longitudFinalSubtramo_i, altitudInicioSubtramo_i,
        altitudFinalSubtramo_i,observacionSubtramo_i,vigencia_i);
END;
/

--- PROCEDIMIENTO INSERTTRAMO
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.INSERTTRAMO ( 
    CodPyto_i IN NUMERIC ,
    codRutaPy_i IN NUMERIC,
    codTramoPy_i IN NUMERIC,
    NroVersion_i IN NUMERIC,
    CodTramo_i IN VARCHAR2,
    fechaRegistro_i IN DATE,
    denominacionTramo_i IN VARCHAR2, 
    denominacionCortoTramo_i IN VARCHAR2,
    NroKmsTramo_i IN NUMERIC,
    ZonaGPS_i  IN VARCHAR2,
    progInicio_i IN VARCHAR2,
    progFinal_i IN VARCHAR2,
    latitudInicioTramo_i IN VARCHAR2,
    latitudFinalTramo_i IN VARCHAR2,
    longitudInicioTramo_i IN VARCHAR2,
    longitudFinalTramo_i IN VARCHAR2,
    altitudInicioTramo_i IN VARCHAR2,
    altitudFinalTramo_i IN VARCHAR2,
    observacionTramo_i IN VARCHAR2,
    vigencia_i IN VARCHAR2 
    ) IS
BEGIN
    INSERT INTO T_TRAMO (CodPyto,codRutaPy,codTramoPy,NroVersion,CodTramo,fechaRegistro,
        denominacionTramo, denominacionCortoTramo,NroKmsTramo,ZonaGPS,progInicio,progFin,
        latitudInicioTramo,latitudFinalTramo,longitudInicioTramo,longitudFinalTramo,
        altitudInicioTramo,altitudFinalTramo,observacionTramo,vigencia) 
    VALUES (CodPyto_i,codRutaPy_i,codTramoPy_i,nroversion_i,CodTramo_i,fechaRegistro_i,
        denominacionTramo_i, denominacionCortoTramo_i,NroKmsTramo_i,ZonaGPS_i,progInicio_i,progFinal_i,
        latitudInicioTramo_i,latitudFinalTramo_i,longitudInicioTramo_i,longitudFinalTramo_i,
        altitudInicioTramo_i,altitudFinalTramo_i,observacionTramo_i,vigencia_i); 
END;
/

--- PROCEDIMIENTO RUTAVER2
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.RUTAVER2 (
    codigo_proyecto IN number
    ) IS
BEGIN
    INSERT INTO T_RUTA (CodPyto,codRutaPy,NroVersion,CodRuta,fechaRegistro,
        denominacionRuta, denominacionCortoRuta,NroKms,ZonaGPS,progInicio,progFinal,
        latitudInicioRuta,latitudFinalRuta,longitudInicioRuta,longitudFinalRuta,
        altitudInicioRuta,altitudFinalRuta,observacionRuta,vigencia,ElaboradoPor) 
    SELECT 
        tr.CodPyto,tr.codRutaPy,2,tr.CodRuta,tr.fechaRegistro,
        tr.denominacionRuta, tr.denominacionCortoRuta,tr.NroKms,tr.ZonaGPS,tr.progInicio,
        tr.progFinal,tr.latitudInicioRuta,tr.latitudFinalRuta,tr.longitudInicioRuta,
        tr.longitudFinalRuta,tr.altitudInicioRuta,tr.altitudFinalRuta,tr.observacionRuta,
        tr.vigencia,tr.ElaboradoPor 
    FROM 
        T_RUTA tr WHERE tr.CodPyto = codigo_proyecto AND tr.codrutapy NOT
        IN (select codrutapy from t_ruta WHERE nroversion = 2 AND CodPyto = codigo_proyecto);

    INSERT INTO T_TRAMO (CodPyto,codRutaPy,codTramoPy,NroVersion,CodTramo,fechaRegistro,
        denominacionTramo, denominacionCortoTramo,NroKmsTramo,ZonaGPS,progInicio,progFin,
        latitudInicioTramo,latitudFinalTramo,longitudInicioTramo,longitudFinalTramo,
        altitudInicioTramo,altitudFinalTramo,observacionTramo,vigencia) 
    SELECT 
        tt.CodPyto,tt.codRutaPy,tt.codTramoPy,2,tt.CodTramo,tt.fechaRegistro,
        tt.denominacionTramo, tt.denominacionCortoTramo,tt.NroKmsTramo,tt.ZonaGPS,tt.progInicio,
        tt.progFin, tt.latitudInicioTramo,tt.latitudFinalTramo,tt.longitudInicioTramo,tt.longitudFinalTramo,
        tt.altitudInicioTramo,tt.altitudFinalTramo,tt.observacionTramo,tt.vigencia
    FROM 
        T_TRAMO tt WHERE tt.CodPyto = codigo_proyecto AND tt.codtramopy NOT
        IN (select codtramopy from t_tramo WHERE nroversion = 2 AND CodPyto = codigo_proyecto);

    INSERT INTO T_SUBTRAMO (CodPyto,codRutaPy,codTramoPy,codSubTramoPy,NroVersion,CodSubTramo,
        fechaRegistro,denominacionSubtramo, denominacionCortoSubtramo,NroKmsSubTramo,ZonaGPS,progInicio,
        progFin,latitudInicioSubtramo,latitudFinalSubtramo,longitudInicioSubtramo,longitudFinalSubtramo,
        altitudInicioSubtramo,altitudFinalSubtramo,observacionSubtramo,vigencia) 
    SELECT 
        ts.CodPyto,ts.codRutaPy,ts.codTramoPy,ts.codSubTramoPy,2,ts.CodSubTramo,
        ts.fechaRegistro,ts.denominacionSubtramo, ts.denominacionCortoSubtramo,ts.NroKmsSubTramo,ts.ZonaGPS,
        ts.progInicio, ts.progFin,ts.latitudInicioSubtramo,ts.latitudFinalSubtramo,ts.longitudInicioSubtramo,
        ts.longitudFinalSubtramo,ts.altitudInicioSubtramo,ts.altitudFinalSubtramo,ts.observacionSubtramo,ts.vigencia
    FROM T_SUBTRAMO ts WHERE ts.CodPyto = codigo_proyecto AND ts.codsubtramopy NOT
    IN (select codsubtramopy from t_subtramo WHERE nroversion = 2 AND CodPyto = codigo_proyecto);
END;
/

--- PROCEDIMIENTO SP_ACTUALIZAR_EXPERIENCIA
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.SP_ACTUALIZAR_EXPERIENCIA (
    T_CORREXP IN NUMBER,
    T_CODPERS IN NUMBER,
    T_LUGARTRABAJO IN VARCHAR2,
    T_DESTRABAJO IN VARCHAR2,
    T_CONTRATO IN VARCHAR2,
    T_FECINI IN DATE,
    T_FECFIN IN DATE,
    T_NROMESES IN NUMBER,
    T_NRODIAS IN NUMBER,
    T_MOTIVORETIRO IN VARCHAR2,
    T_codprof IN number,
    T_VIGENTE  IN VARCHAR2
    ) IS
BEGIN
    UPDATE T_EXPERIENCIA 
    SET codprof=T_codprof, LUGARTRABAJO=T_LUGARTRABAJO,DESTRABAJO=T_DESTRABAJO,
        CONTRATO=T_CONTRATO,FECINI=T_FECINI,FECFIN=T_FECFIN,NROMESES=T_NROMESES,
        NRODIAS=T_NRODIAS,MOTIVORETIRO=T_MOTIVORETIRO,VIGENTE=T_VIGENTE WHERE CORREXP=T_CORREXP AND CODPERS=T_CODPERS;
END ;
/

--- PROCEDIMIENTO SP_AGREGAR_EXPERIENCIA
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.SP_AGREGAR_EXPERIENCIA (
    T_CORREXP IN NUMBER,
    T_CODPERS IN NUMBER,
    T_LUGARTRABAJO IN  VARCHAR2,
    T_DESTRABAJO IN VARCHAR2,
    T_CONTRATO IN VARCHAR2,
    T_FECINI IN DATE,
    T_FECFIN IN DATE,
    T_NROMESES IN NUMBER,
    T_NRODIAS IN NUMBER,
    T_MOTIVORETIRO IN VARCHAR2,
    T_codprof IN NUMBER,
    T_VIGENTE IN VARCHAR2
    ) IS
BEGIN
    INSERT INTO T_EXPERIENCIA (CORREXP,CODPERS,LUGARTRABAJO,DESTRABAJO,CONTRATO,FECINI,
        FECFIN,NROMESES,NRODIAS,MOTIVORETIRO,codprof,VIGENTE)
    VALUES(T_CORREXP,T_CODPERS,T_LUGARTRABAJO,T_DESTRABAJO,T_CONTRATO,T_FECINI,T_FECFIN,
        T_NROMESES,T_NRODIAS,T_MOTIVORETIRO,T_codprof,T_VIGENTE);
END;
/

--- PROCEDIMIENTO SP_AGREGAR_PROVINCIA
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.SP_AGREGAR_PROVINCIA (
    T_CODDPTO IN VARCHAR2,
    T_CODPROV IN VARCHAR2,
    T_DESPROV IN  VARCHAR2,
    T_VIGENTE IN VARCHAR2
    ) IS
BEGIN
    INSERT INTO T_PROVINCIA (CODDPTO, CODPROV, DESPROV, VIGENTE)
    VALUES(T_CODDPTO, T_CODPROV, T_DESPROV, T_VIGENTE);
END;
/

--- PROCEDIMIENTO SP_CRUD_PYTOPERS
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.SP_CRUD_PYTOPERS (
    T_CODPYTO IN NUMBER,
    T_CORREMPL IN NUMBER,
    T_CODPERS IN NUMBER,
    T_CODCARGO IN NUMBER,
    T_EMAIL  IN VARCHAR2,
    T_CELULAR IN VARCHAR2,
    T_FLGPYEMPIE IN VARCHAR2,
    T_FECINI IN DATE ,
    T_FECFIN IN DATE ,
    T_FLGEMPLJEF IN VARCHAR2,
    T_COSTO IN NUMBER,
    T_DESQTRAB IN VARCHAR2,
    T_OBSERVAC IN VARCHAR2,
    T_VIGENTE IN VARCHAR2,
    ACCION IN VARCHAR2
    ) IS
BEGIN
    CASE ACCION
        WHEN 'INSERTAR' THEN
            INSERT INTO T_PYTOPERS(CODPYTO,CORREMPL,CODPERS,CODCARGO,EMAIL,CELULAR,FLGPYEMPIE,
                FECINI,FECFIN,FLGEMPLJEF,COSTO,DESQTRAB,OBSERVAC,VIGENTE) 
            VALUES (T_CODPYTO,T_CORREMPL,T_CODPERS,T_CODCARGO,T_EMAIL,T_CELULAR,T_FLGPYEMPIE,
                T_FECINI,T_FECFIN,T_FLGEMPLJEF,T_COSTO,T_DESQTRAB,T_OBSERVAC,T_VIGENTE);
        WHEN 'MODIFICAR' THEN
            UPDATE T_PYTOPERS 
            SET CODPYTO=T_CODPYTO,CORREMPL=T_CORREMPL,CODPERS=T_CODPERS,CODCARGO=T_CODCARGO,
                EMAIL=T_EMAIL,CELULAR=T_CELULAR,FLGPYEMPIE=T_FLGPYEMPIE,FECINI=T_FECINI,FECFIN=T_FECFIN,
                FLGEMPLJEF=T_FLGEMPLJEF,COSTO=T_COSTO,DESQTRAB=T_DESQTRAB,OBSERVAC=T_OBSERVAC,VIGENTE=T_VIGENTE
            WHERE CORREMPL=T_CORREMPL AND CODPERS=T_CODPERS;
        WHEN 'ELIMINAR' THEN
            DELETE FROM T_PYTOPERS WHERE CORREMPL=T_CORREMPL AND CODPYTO=T_CODPYTO ;
    END CASE;
END;
/

--- PROCEDIMIENTO SP_ELIMINAR_EXPERIENCIA
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.SP_ELIMINAR_EXPERIENCIA (
    T_CORREXP IN NUMBER,
    T_CODPERS IN  NUMBER
    ) IS
BEGIN
    DELETE FROM T_Experiencia WHERE CORREXP=T_CORREXP AND CODPERS=T_CODPERS;
END;
/

--- PROCEDIMIENTO UPDATERUTA
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.UPDATERUTA 
    (CodPyto_i IN NUMERIC ,
    codRutaPy_i IN NUMERIC,
    NroVersion_i IN NUMERIC,
    CodRuta_i IN VARCHAR2,
    fechaRegistro_i IN DATE,
    denominacionRuta_i IN VARCHAR2, 
    denominacionCortoRuta_i IN VARCHAR2,
    NroKms_i IN NUMERIC,
    ZonaGPS_i  IN VARCHAR2,
    progInicio_i IN VARCHAR2,
    progFinal_i IN VARCHAR2,
    latitudInicioRuta_i IN VARCHAR2,
    latitudFinalRuta_i IN VARCHAR2,
    longitudInicioRuta_i IN VARCHAR2,
    longitudFinalRuta_i IN VARCHAR2,
    altitudInicioRuta_i IN VARCHAR2,
    altitudFinalRuta_i IN VARCHAR2,
    observacionRuta_i IN VARCHAR2,
    vigencia_i IN VARCHAR2,
    elaboradopor_i IN NUMERIC
    ) IS
BEGIN
    UPDATE t_ruta 
    SET codruta = CodRuta_i,  fechaRegistro = fechaRegistro_i, denominacionRuta = denominacionRuta_i,
        denominacionCortoRuta = denominacionCortoRuta_i, nrokms=nrokms_i, zonagps=zonagps_i, 
        progInicio= progInicio_i, progFinal=progFinal_i, latitudInicioRuta = latitudInicioRuta_i,
        latitudFinalRuta = latitudFinalRuta_i, longitudInicioRuta = longitudInicioRuta_i,
        longitudFinalRuta = longitudFinalRuta_i, altitudInicioRuta = altitudInicioRuta_i, 
        altitudFinalRuta = altitudFinalRuta_i, observacionruta = observacionRuta_i, vigencia = vigencia_i, elaboradopor = elaboradopor_i
    WHERE codpyto = codpyto_i AND codrutapy = codrutapy_i AND nroversion = nroversion_i;
END;
/

--- PROCEDIMIENTO UPDATESUBTRAMO
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.UPDATESUBTRAMO (
    CodPyto_i IN NUMERIC ,
    codRutaPy_i IN NUMERIC,
    codTramoPy_i IN NUMERIC,
    codSubtramoPy_i IN NUMERIC,
    NroVersion_i IN NUMERIC,
    CodSubTramo_i IN VARCHAR2,
    fechaRegistro_i IN DATE,
    denominacionSubtramo_i IN VARCHAR2, 
    denominacionCortoSubtramo_i IN VARCHAR2,
    NroKmssubTramo_i IN NUMERIC,
    ZonaGPS_i  IN VARCHAR2,
    progInicio_i IN VARCHAR2,
    progFinal_i IN VARCHAR2,
    latitudInicioSubtramo_i IN VARCHAR2,
    latitudFinalSubtramo_i IN VARCHAR2,
    longitudInicioSubtramo_i IN VARCHAR2,
    longitudFinalSubtramo_i IN VARCHAR2,
    altitudInicioSubtramo_i IN VARCHAR2,
    altitudFinalSubtramo_i IN VARCHAR2,
    observacionsubTramo_i IN VARCHAR2,
    vigencia_i IN VARCHAR2 
    ) IS
BEGIN
    UPDATE t_subtramo 
    SET codsubtramo = CodsubTramo_i,  fechaRegistro = fechaRegistro_i, denominacionSubtramo = denominacionSubtramo_i,
        denominacionCortoSubtramo = denominacionCortoSubtramo_i, nrokmssubtramo=nrokmssubtramo_i, zonagps=zonagps_i, 
        progInicio= progInicio_i, progFin=progFinal_i, latitudInicioSubtramo = latitudInicioSubtramo_i, 
        latitudFinalSubtramo = latitudFinalSubtramo_i, longitudInicioSubtramo = longitudInicioSubtramo_i,
        longitudFinalSubtramo = longitudFinalSubtramo_i, altitudInicioSubtramo = altitudInicioSubtramo_i, altitudFinalSubtramo = altitudFinalSubtramo_i, 
        observacionsubtramo = observacionsubtramo_i, vigencia = vigencia_i
    WHERE codpyto = codpyto_i AND codrutapy = codrutapy_i AND codtramopy = codtramopy_i AND codsubtramopy = codsubtramopy_i AND nroversion = nroversion_i;
END;
/

--- PROCEDIMIENTO UPDATETRAMO
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.UPDATETRAMO ( 
    CodPyto_i IN NUMERIC ,
    codRutaPy_i IN NUMERIC,
    codTramoPy_i IN NUMERIC,
    NroVersion_i IN NUMERIC,
    CodTramo_i IN VARCHAR2,
    fechaRegistro_i IN DATE,
    denominacionTramo_i IN VARCHAR2, 
    denominacionCortoTramo_i IN VARCHAR2,
    NroKmsTramo_i IN NUMERIC,
    ZonaGPS_i  IN VARCHAR2,
    progInicio_i IN VARCHAR2,
    progFinal_i IN VARCHAR2,
    latitudInicioTramo_i IN VARCHAR2,
    latitudFinalTramo_i IN VARCHAR2,
    longitudInicioTramo_i IN VARCHAR2,
    longitudFinalTramo_i IN VARCHAR2,
    altitudInicioTramo_i IN VARCHAR2,
    altitudFinalTramo_i IN VARCHAR2,
    observacionTramo_i IN VARCHAR2,
    vigencia_i IN VARCHAR2 
    ) IS
BEGIN
    UPDATE t_tramo 
    SET codtramo = CodTramo_i,  fechaRegistro = fechaRegistro_i, denominacionTramo = denominacionTramo_i,
        denominacionCortoTramo = denominacionCortoTramo_i, nrokmstramo=nrokmstramo_i, zonagps=zonagps_i, 
        progInicio= progInicio_i, progFin=progFinal_i, latitudInicioTramo = latitudInicioTramo_i, 
        latitudFinalTramo = latitudFinalTramo_i, longitudInicioTramo = longitudInicioTramo_i, longitudFinalTramo = longitudFinalTramo_i, 
        altitudInicioTramo = altitudInicioTramo_i, altitudFinalTramo = altitudFinalTramo_i, observaciontramo = observaciontramo_i, vigencia = vigencia_i
    WHERE codpyto = codpyto_i AND codrutapy = codrutapy_i AND codtramopy = codtramopy_i AND nroversion = nroversion_i;
END;
/

--- PROCEDIMIENTO UP_PRES_ELIMINARPARTIDA
SET DEFINE off;
CREATE OR REPLACE NONEDITIONABLE PROCEDURE SYSCANVAS.UP_PRES_ELIMINARPARTIDA (
    IN_INgegr IN VARCHAR,
    IN_codpartidai IN INT,
    
    out_codrpta OUT INT,
    out_msgrpta OUT VARCHAR
    )AS
    cant_presdet NUMBER;
    cant_partidas NUMBER;
    var_codpyto NUMBER;
    var_nro VARCHAR(20);
    var_numeros VARCHAR(255);

    CURSOR curpresdet (cr_INgegr VARCHAR, cr_codpartidai NUMBER) IS
        SELECT codpyto, nroversion
        FROM t_presINgreso_det
        WHERE INgegr = cr_INgegr
        AND codpartidai=cr_codpartidai;
BEGIN
    /* SE ELIMINAN TODOS LOS DETALLES */
    SELECT COUNT(1)
    INTO cant_presdet
    FROM t_presINgreso_det
    WHERE INgegr=IN_INgegr
        AND codpartidai=IN_codpartidai;

    var_numeros := '';

    IF cant_presdet !=0 THEN
        OPEN curpresdet(IN_INgegr, IN_codpartidai);
        LOOP
            FETCH curpresdet INTO var_codpyto, var_nro;
            var_numeros := var_numeros || var_nro || ',';
            EXIT WHEN curpresdet%NOTFOUND;
        END LOOP;
        CLOSE curpresdet;

        DELETE FROM t_presINgreso_det 
        WHERE INgegr = IN_INgegr
            AND codpartidai=IN_codpartidai;

		/* SE ELIMINAN TODOS LOS PRESUPUESTOS */
		WHILE INSTR(var_numeros, ',')>0 LOOP
			var_nro := SUBSTR(var_numeros, 1, INSTR(var_numeros, ','));

			out_msgrpta := out_msgrpta || ' - ' || var_nro;
			/*DELETE FROM t_presINgreso 
			WHERE codpyto = var_codpyto
				AND nroversion=var_nro;*/

			var_numeros := SUBSTR(var_numeros, INSTR(var_numeros, ',')+1);
		END LOOP;
    END IF;

    /* SE ELIMINA LA PARTIDA */
    SELECT COUNT(1) 
    INTO cant_partidas
    FROM t_partidaINg
    WHERE INgegr=IN_INgegr
        AND codpartidai=IN_codpartidai;

    IF cant_partidas !=0 THEN
        DELETE FROM t_partidaINg 
        WHERE INgegr = IN_INgegr
            AND codpartidai=IN_codpartidai;
    END IF;

    out_codrpta := 1;
    out_msgrpta := 'ELIMINADO CORRECTAMENTE';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        out_codrpta := -1;
        out_msgrpta := 'ERROR AL ELIMINAR';
    WHEN OTHERS THEN
        out_codrpta := SQLCODE;
        out_msgrpta := SQLERRM ;
END;
/
