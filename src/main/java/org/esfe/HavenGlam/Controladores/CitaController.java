package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private ICitaService citaService;

    @Autowired
    private IServicioService servicioService;

    @Autowired
    private IEmpleadoService empleadoService;

    // Vista principal del flujo de reservas de citas
    @GetMapping({"", "/"})
    public String index(Model model) {
        return "citas/index";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        return "citas/index";
    }

    @GetMapping("/reservar")
    public String reservarVista(Model model) {
        return "citas/index";
    }

    // Endpoint API: servicios activos, consumido por citas.js (Paso 1)
    @GetMapping("/api/servicios")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerServiciosDisponibles() {
        List<Map<String, Object>> resultado = servicioService.listar().stream()
                .filter(s -> s.getEstado() != null && "Activo".equalsIgnoreCase(s.getEstado().getNombreEstado()))
                .map(s -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", s.getIdServicio());
                    item.put("name", s.getNombreServicio());
                    item.put("category", s.getCategoria() != null ? s.getCategoria().getNombreCategoria() : "General");
                    item.put("price", s.getPrecio());
                    item.put("duration", s.getDuracionMinutos().getHour() * 60 + s.getDuracionMinutos().getMinute());
                    return item;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    // Endpoint API: empleados activos, consumido por citas.js (Paso 2)
    @GetMapping("/api/empleados")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerEmpleadosDisponibles() {
        List<Map<String, Object>> resultado = empleadoService.listar().stream()
                .filter(e -> e.getEstado() != null && "Activo".equalsIgnoreCase(e.getEstado().getNombreEstado()))
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", e.getIdEmpleado());
                    item.put("name", e.getPersona().getNombre() + " " + e.getPersona().getApellido());
                    item.put("role", "Especialista Haven Glam");
                    item.put("avatar", iniciales(e.getPersona().getNombre(), e.getPersona().getApellido()));
                    return item;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    // Endpoint API para consultar horarios ocupados consumido por citas.js
    @GetMapping("/api/disponibilidad")
    @ResponseBody
    public ResponseEntity<List<String>> obtenerDisponibilidad(@RequestParam(name = "fecha", required = false) String fecha) {
        return ResponseEntity.ok(List.of());
    }

    // Endpoint API para procesar y guardar la reserva consumido por citas.js
    @PostMapping("/api/reservar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> procesarReserva(@RequestBody Map<String, Object> payload) {
        Map<String, Object> respuesta = new HashMap<>();

        int numeroAleatorio = (int) (Math.random() * 9000 + 1000);
        String codigoCita = "HG-2026-" + numeroAleatorio;

        respuesta.put("status", "success");
        respuesta.put("codigoCita", codigoCita);
        respuesta.put("mensaje", "Cita reservada y bloqueada exitosamente");

        return ResponseEntity.ok(respuesta);
    }

    private String iniciales(String nombre, String apellido) {
        String i1 = (nombre != null && !nombre.isBlank()) ? nombre.trim().substring(0, 1).toUpperCase() : "";
        String i2 = (apellido != null && !apellido.isBlank()) ? apellido.trim().substring(0, 1).toUpperCase() : "";
        return i1 + i2;
    }
}