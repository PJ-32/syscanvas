package com.syscanvas.syscanvas.dto.request;

import jakarta.validation.constraints.*;
import java.util.*;

/*
 * Request para reordenar etapas de un canvas.
 */

public record ReordenarEtapasRequest (
    @NotNull(message = "El código del canvas es obligatorio")
    Long codCanvas,
    
    @NotEmpty(message = "La lista de etapas no puede estar vacía")
    List<Long> ordenEtapas
) {
    
}