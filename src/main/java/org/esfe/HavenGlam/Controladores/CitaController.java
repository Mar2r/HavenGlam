package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.IClienteService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private ICitaService citaService;

    @Autowired
    private ICitaServicioService citaServicioService;

    @Autowired
    private IServicioService servicioService;

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IEstadoService estadoService;

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

    // Vista de Historial / Mis Citas del Cliente autenticado
    @GetMapping({"/historial", "/mis-citas"})
    public String historialCitas(Model model, Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioAutenticado(authentication);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();
        Optional<Cliente> clienteOpt = clienteService.buscarPorPersona(usuario.getPersona().getIdPersona());
        if (clienteOpt.isEmpty()) {
            model.addAttribute("citas", List.of());
            model.addAttribute("totalCitas", 0);
            return "citas/historial";
        }

        Cliente cliente = clienteOpt.get();
        List<CitaServicio> todosCitaServicios = citaServicioService.listar();

        List<Cita> citasList = citaService.listar().stream()
                .filter(c -> c.getCliente() != null && cliente.getIdCliente().equals(c.getCliente().getIdCliente()))
                .sorted(Comparator.comparing(Cita::getFecha, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Cita::getHora, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        List<Map<String, Object>> misCitas = new ArrayList<>();
        for (Cita c : citasList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", c.getIdCita());
            map.put("codigo", "HG-" + (c.getFecha() != null ? c.getFecha().getYear() : "2026") + "-" + String.format("%04d", c.getIdCita()));
            map.put("fecha", c.getFecha());
            map.put("hora", c.getHora());
            map.put("horaFin", c.getHoraFin());

            String profesional = (c.getEmpleado() != null && c.getEmpleado().getPersona() != null)
                    ? c.getEmpleado().getPersona().getNombre() + " " + c.getEmpleado().getPersona().getApellido()
                    : "Especialista Haven Glam";
            map.put("profesional", profesional);

            String estadoNombre = c.getEstado() != null ? c.getEstado().getNombreEstado() : "Pendiente";
            map.put("estado", estadoNombre);
            map.put("tagClass", "status-" + estadoNombre.toLowerCase());

            boolean cancelable = "Pendiente".equalsIgnoreCase(estadoNombre) || "Confirmada".equalsIgnoreCase(estadoNombre);
            map.put("cancelable", cancelable);

            List<CitaServicio> detalle = todosCitaServicios.stream()
                    .filter(cs -> cs.getCita() != null && c.getIdCita().equals(cs.getCita().getIdCita()))
                    .collect(Collectors.toList());

            BigDecimal total = detalle.stream()
                    .map(cs -> cs.getPrecioAlMomento() != null ? cs.getPrecioAlMomento() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            map.put("total", total);
            map.put("servicios", detalle);
            map.put("observaciones", c.getObservaciones());

            misCitas.add(map);
        }

        model.addAttribute("citas", misCitas);
        model.addAttribute("totalCitas", misCitas.size());
        return "citas/historial";
    }

    // Cancelar cita por parte del cliente o administrador
    @PostMapping("/cancelar/{id}")
    public String cancelarCitaCliente(@PathVariable("id") Integer idCita,
                                      Authentication authentication,
                                      org.springframework.web.servlet.mvc.support.RedirectAttributes flash) {
        Optional<Usuario> usuarioOpt = usuarioAutenticado(authentication);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Cita> citaOpt = citaService.buscarPorId(idCita);
        if (citaOpt.isEmpty()) {
            flash.addFlashAttribute("error", "Cita no encontrada.");
            return "redirect:/citas/historial";
        }

        Cita cita = citaOpt.get();
        Usuario usuario = usuarioOpt.get();
        Optional<Cliente> clienteOpt = clienteService.buscarPorPersona(usuario.getPersona().getIdPersona());

        boolean esPropia = clienteOpt.isPresent() && cita.getCliente() != null
                && clienteOpt.get().getIdCliente().equals(cita.getCliente().getIdCliente());
        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN"));

        if (!esPropia && !esAdmin) {
            flash.addFlashAttribute("error", "No tienes permiso para cancelar esta cita.");
            return "redirect:/citas/historial";
        }

        Estado estadoCancelada = estadoService.listar().stream()
                .filter(e -> "Cancelada".equalsIgnoreCase(e.getNombreEstado()) || "Cancelado".equalsIgnoreCase(e.getNombreEstado()))
                .findFirst()
                .orElse(null);

        if (estadoCancelada != null) {
            cita.setEstado(estadoCancelada);
            citaService.guardar(cita);
            flash.addFlashAttribute("exito", "La cita ha sido cancelada correctamente y el horario ha quedado liberado.");
        } else {
            flash.addFlashAttribute("error", "No se encontró el estado de cancelación en la base de datos.");
        }

        return "redirect:/citas/historial";
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

    private static final int SLOT_MINUTOS = 30;

    // Endpoint API para consultar horarios ocupados consumido por citas.js
    @GetMapping("/api/disponibilidad")
    @ResponseBody
    public ResponseEntity<List<String>> obtenerDisponibilidad(@RequestParam(name = "fecha", required = false) String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return ResponseEntity.ok(List.of());
        }

        LocalDate dia;
        try {
            dia = LocalDate.parse(fecha);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }

        List<Cita> citasDelDia = citaService.listarActivasPorFecha(dia);

        List<String> turnosOcupados = new java.util.ArrayList<>();
        for (Cita cita : citasDelDia) {
            LocalTime inicio = cita.getHora();
            LocalTime fin = cita.getHoraFin() != null ? cita.getHoraFin() : inicio.plusMinutes(SLOT_MINUTOS);

            LocalTime cursor = LocalTime.of(inicio.getHour(), inicio.getMinute());
            while (cursor.isBefore(fin)) {
                String slot = String.format("%02d:%02d", cursor.getHour(), cursor.getMinute());
                if (!turnosOcupados.contains(slot)) {
                    turnosOcupados.add(slot);
                }
                cursor = cursor.plusMinutes(SLOT_MINUTOS);
            }
        }

        turnosOcupados.sort(String::compareTo);
        return ResponseEntity.ok(turnosOcupados);
    }

    // Endpoint API: datos del cliente autenticado (sin descriptores hardcodeados)
    @GetMapping("/api/usuario")
    @ResponseBody
    public ResponseEntity<?> obtenerUsuarioAutenticado(Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioAutenticado(authentication);
        if (usuarioOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("autenticado", false);
            return ResponseEntity.ok(error);
        }

        Usuario usuario = usuarioOpt.get();
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("autenticado", true);
        resultado.put("id", usuario.getIdUsuario());
        resultado.put("nombre", usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
        resultado.put("correo", usuario.getCorreo());
        return ResponseEntity.ok(resultado);
    }

    // Endpoint API para procesar y guardar la reserva consumido por citas.js
    @PostMapping("/api/reservar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> procesarReserva(@RequestBody Map<String, Object> payload,
                                                               Authentication authentication) {
        Map<String, Object> respuesta = new HashMap<>();

        // 1. Obtener el usuario/cliente autenticado real
        Optional<Usuario> usuarioOpt = usuarioAutenticado(authentication);
        if (usuarioOpt.isEmpty()) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "No hay un cliente autenticado en la sesión.");
            return ResponseEntity.badRequest().body(respuesta);
        }

        Optional<Cliente> clienteOpt = clienteService.buscarPorPersona(usuarioOpt.get().getPersona().getIdPersona());
        if (clienteOpt.isEmpty()) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "No se encontró un cliente asociado a tu cuenta.");
            return ResponseEntity.badRequest().body(respuesta);
        }
        Cliente cliente = clienteOpt.get();

        // 2. Obtener el profesional (empleado)
        Integer profesionalId = toInteger(payload.get("profesionalId"));
        Optional<Empleado> empleadoOpt = profesionalId != null ? empleadoService.buscarPorId(profesionalId) : Optional.empty();
        if (empleadoOpt.isEmpty()) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Debes seleccionar un profesional válido.");
            return ResponseEntity.badRequest().body(respuesta);
        }

        // 3. Fecha y hora
        String fechaStr = (String) payload.get("fecha");
        String horaStr = (String) payload.get("horaInicio");
        if (fechaStr == null || horaStr == null) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Debes seleccionar fecha y hora.");
            return ResponseEntity.badRequest().body(respuesta);
        }

        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalTime horaInicio = LocalTime.parse(horaStr);

        // Duración total calculada de forma fiable a partir de los servicios en BD
        int duracionMin = obtenerDuracionServicios(payload);
        if (duracionMin <= 0) {
            duracionMin = SLOT_MINUTOS;
        }
        LocalTime horaFin = horaInicio.plusMinutes(duracionMin);

        // 4. Estado inicial de la cita ("Pendiente")
        Estado estadoCita = obtenerEstadoPendiente();

        // 5. Observaciones / notas
        String notas = (String) payload.get("notas");
        if (notas == null || notas.isBlank()) {
            notas = "Sin observaciones";
        }

        // 6. Construir y guardar la Cita
        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setEmpleado(empleadoOpt.get());
        cita.setFecha(fecha);
        cita.setHora(horaInicio);
        cita.setHoraFin(horaFin);
        cita.setEstado(estadoCita);
        cita.setObservaciones(notas);
        cita.setFechaCreacion(LocalDateTime.now());

        Cita citaGuardada = citaService.guardar(cita);

        // 7. Guardar los servicios asociados (CitaServicio)
        List<?> serviciosIdsRaw = payload.get("serviciosIds") instanceof List ? (List<?>) payload.get("serviciosIds") : List.of();
        for (Object idRaw : serviciosIdsRaw) {
            Integer idServicio = toInteger(idRaw);
            if (idServicio == null) continue;

            Optional<Servicio> servicioOpt = servicioService.buscarPorId(idServicio);
            if (servicioOpt.isEmpty()) continue;

            CitaServicio citaServicio = new CitaServicio();
            citaServicio.setCita(citaGuardada);
            citaServicio.setServicio(servicioOpt.get());
            citaServicio.setPrecioAlMomento(servicioOpt.get().getPrecio() != null
                    ? servicioOpt.get().getPrecio()
                    : BigDecimal.ZERO);
            citaServicioService.guardar(citaServicio);
        }

        // 8. Código de referencia legible
        String codigoCita = "HG-" + fecha.getYear() + "-" + String.format("%04d", citaGuardada.getIdCita());

        respuesta.put("status", "success");
        respuesta.put("codigoCita", codigoCita);
        respuesta.put("citaId", citaGuardada.getIdCita());
        respuesta.put("mensaje", "Cita reservada y bloqueada exitosamente");

        return ResponseEntity.ok(respuesta);
    }

    // Obtiene el usuario autenticado a partir del correo (principal de Spring Security)
    private Optional<Usuario> usuarioAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }
        return usuarioService.buscarPorCorreo(authentication.getName());
    }

    private int obtenerDuracionServicios(Map<String, Object> payload) {
        List<?> serviciosIdsRaw = payload.get("serviciosIds") instanceof List ? (List<?>) payload.get("serviciosIds") : List.of();
        int total = 0;
        for (Object idRaw : serviciosIdsRaw) {
            Integer id = toInteger(idRaw);
            if (id == null) continue;
            total += servicioService.buscarPorId(id)
                    .map(s -> s.getDuracionMinutos() != null
                            ? s.getDuracionMinutos().getHour() * 60 + s.getDuracionMinutos().getMinute()
                            : 0)
                    .orElse(0);
        }
        return total;
    }

    private Estado obtenerEstadoPendiente() {
        return estadoService.listar().stream()
                .filter(e -> "Pendiente".equalsIgnoreCase(e.getNombreEstado()))
                .findFirst()
                .orElse(estadoService.listar().stream()
                        .filter(e -> ("CITA".equalsIgnoreCase(e.getTipoEstado())
                                || "Citas".equalsIgnoreCase(e.getTipoEstado()))
                                && "Pendiente".equalsIgnoreCase(e.getNombreEstado()))
                        .findFirst()
                        .orElse(null));
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String iniciales(String nombre, String apellido) {
        String i1 = (nombre != null && !nombre.isBlank()) ? nombre.trim().substring(0, 1).toUpperCase() : "";
        String i2 = (apellido != null && !apellido.isBlank()) ? apellido.trim().substring(0, 1).toUpperCase() : "";
        return i1 + i2;
    }
}