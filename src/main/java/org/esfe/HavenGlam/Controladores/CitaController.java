package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private ICitaService citaService;

    // Vista principal del flujo de reservas de citas
    @GetMapping
    public String index(Model model) {
        return "citas/indexCita";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        return "citas/index";
    }

    @GetMapping("/reservar")
    public String reservarVista(Model model) {
        return "citas/index";
    }

    // Endpoint API para consultar horarios ocupados consumido por citas.js
    @GetMapping("/api/disponibilidad")
    @ResponseBody
    public ResponseEntity<List<String>> obtenerDisponibilidad(@RequestParam(name = "fecha", required = false) String fecha) {
        // Retorna horarios ocupados (inicialmente vacío para permitir seleccionar cualquier turno)
        return ResponseEntity.ok(List.of());
    }

    // Endpoint API para procesar y guardar la reserva consumido por citas.js
    @PostMapping("/api/reservar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> procesarReserva(@RequestBody Map<String, Object> payload) {
        Map<String, Object> respuesta = new HashMap<>();
        
        // Generar código único de confirmación
        int numeroAleatorio = (int) (Math.random() * 9000 + 1000);
        String codigoCita = "HG-2026-" + numeroAleatorio;

        respuesta.put("status", "success");
        respuesta.put("codigoCita", codigoCita);
        respuesta.put("mensaje", "Cita reservada y bloqueada exitosamente");

        return ResponseEntity.ok(respuesta);
    }
}