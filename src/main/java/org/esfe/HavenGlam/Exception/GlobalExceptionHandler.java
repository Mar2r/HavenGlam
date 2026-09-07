package org.esfe.HavenGlam.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Captura excepciones de negocio lanzadas dentro de los controllers
 * y las muestra usando las mismas plantillas visuales que los errores
 * HTTP de framework (400/404/500), para mantener consistencia visual.
 *
 * Ejemplos que cubre:
 * - AgendaEmpleadoController lanzando IllegalStateException cuando
 *   el Usuario logueado no tiene un perfil de Empleado asociado.
 * - Cualquier ...Controller.orElseThrow(() -> new IllegalArgumentException(
 *   "X no encontrado con ID: " + id)) que no fue capturado localmente.
 * - Cualquier excepción no controlada que llegue hasta el controller
 *   (fallback genérico a error 500).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String manejarIllegalState(IllegalStateException ex, Model model) {
        model.addAttribute("titulo", "No se pudo completar la acción");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("titulo", "No encontrado");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String manejarErrorGeneral(Exception ex, Model model) {
        ex.printStackTrace(); // para ver la causa real en consola
        model.addAttribute("titulo", "Algo salió mal");
        model.addAttribute("mensaje", "Ocurrió un error inesperado. Ya estamos al tanto.");
        return "error/500";
    }
}