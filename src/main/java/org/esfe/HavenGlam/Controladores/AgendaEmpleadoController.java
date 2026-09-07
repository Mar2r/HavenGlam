package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AgendaEmpleadoController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private ICitaService citaService;

    @Autowired
    private ICitaServicioService citaServicioService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping("/agendaEmpleado")
    public String agenda(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        Empleado empleado = empleadoService.buscarPorPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new IllegalStateException("Este usuario no tiene un perfil de Empleado asociado"));

        LocalDate hoy = LocalDate.now();

        List<Cita> todasLasCitas = citaService.listar().stream()
                .filter(c -> c.getEmpleado() != null && empleado.getIdEmpleado().equals(c.getEmpleado().getIdEmpleado()))
                .collect(Collectors.toList());

        List<CitaServicio> todosCitaServicio = citaServicioService.listar();

        // Citas del día de hoy
        List<Cita> citasHoyList = todasLasCitas.stream()
                .filter(c -> hoy.equals(c.getFecha()))
                .sorted(Comparator.comparing(Cita::getHora))
                .collect(Collectors.toList());

        List<Map<String, Object>> agendaHoy = new ArrayList<>();
        for (Cita c : citasHoyList) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getIdCita());
            item.put("hora", c.getHora());

            String nombreCliente = c.getCliente() != null && c.getCliente().getPersona() != null
                    ? c.getCliente().getPersona().getNombre() + " " + c.getCliente().getPersona().getApellido()
                    : "Cliente";
            item.put("cliente", nombreCliente);

            String nombreEmpleado = c.getEmpleado() != null && c.getEmpleado().getPersona() != null
                    ? c.getEmpleado().getPersona().getNombre() + " " + c.getEmpleado().getPersona().getApellido()
                    : "Empleado";
            item.put("empleado", nombreEmpleado);

            item.put("servicio", nombreServicios(c, todosCitaServicio));

            String estado = c.getEstado() != null ? c.getEstado().getNombreEstado() : "Pendiente";
            item.put("estado", estado.toUpperCase());
            item.put("tagClass", "status-" + estado.toLowerCase());

            agendaHoy.add(item);
        }

        model.addAttribute("nombreUsuarioSesion", usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
        model.addAttribute("nombreEmpleado", usuario.getPersona().getNombre());
        model.addAttribute("citasHoy", agendaHoy);

        return "empleado/agendaEmpleado";
    }

    /**
     * Endpoint API para obtener todas las citas de la base de datos del empleado logueado
     */
    @GetMapping("/agendaEmpleado/api/citas")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerCitasEmpleado(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario = usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        Empleado empleado = empleadoService.buscarPorPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new IllegalStateException("Este usuario no tiene un perfil de Empleado asociado"));

        List<Cita> citas = citaService.listar().stream()
                .filter(c -> c.getEmpleado() != null && empleado.getIdEmpleado().equals(c.getEmpleado().getIdEmpleado()))
                .sorted(Comparator.comparing(Cita::getFecha).thenComparing(Cita::getHora))
                .collect(Collectors.toList());

        List<CitaServicio> todosCitaServicio = citaServicioService.listar();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Cita c : citas) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getIdCita());
            item.put("code", "HG-" + c.getFecha().getYear() + "-" + String.format("%04d", c.getIdCita()));

            String nombreCliente = "Cliente";
            String emailCliente = "Sin correo";
            String telefonoCliente = "Sin teléfono";

            if (c.getCliente() != null && c.getCliente().getPersona() != null) {
                nombreCliente = c.getCliente().getPersona().getNombre() + " " + c.getCliente().getPersona().getApellido();
                telefonoCliente = c.getCliente().getPersona().getTelefono() != null ? c.getCliente().getPersona().getTelefono() : "Sin teléfono";
            }

            // Buscar correo en Usuario si existe
            if (c.getCliente() != null && c.getCliente().getPersona() != null) {
                Integer idPersona = c.getCliente().getPersona().getIdPersona();
                Optional<Usuario> usuarioCli = usuarioService.listar().stream()
                        .filter(u -> u.getPersona() != null && idPersona.equals(u.getPersona().getIdPersona()))
                        .findFirst();
                if (usuarioCli.isPresent()) {
                    emailCliente = usuarioCli.get().getCorreo();
                }
            }

            item.put("client", nombreCliente);
            item.put("email", emailCliente);
            item.put("phone", telefonoCliente);

            String nombreEmpleado = c.getEmpleado() != null && c.getEmpleado().getPersona() != null
                    ? c.getEmpleado().getPersona().getNombre() + " " + c.getEmpleado().getPersona().getApellido()
                    : "Empleado";
            item.put("professional", nombreEmpleado);

            item.put("dateStr", c.getFecha().toString());
            item.put("timeSlot", c.getHora().toString().substring(0, 5));

            List<Map<String, Object>> serviciosLista = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (CitaServicio cs : todosCitaServicio) {
                if (cs.getCita() != null && c.getIdCita().equals(cs.getCita().getIdCita())) {
                    Map<String, Object> sMap = new HashMap<>();
                    String sNom = cs.getServicio() != null ? cs.getServicio().getNombreServicio() : "Servicio";
                    BigDecimal sPrecio = cs.getPrecioAlMomento() != null ? cs.getPrecioAlMomento() : BigDecimal.ZERO;
                    sMap.put("name", sNom);
                    sMap.put("price", sPrecio);
                    serviciosLista.add(sMap);
                    total = total.add(sPrecio);
                }
            }

            item.put("service", nombreServicios(c, todosCitaServicio));
            item.put("services", serviciosLista);
            item.put("price", total);

            String estado = c.getEstado() != null ? c.getEstado().getNombreEstado() : "Pendiente";
            item.put("status", estado.toUpperCase());
            item.put("notes", c.getObservaciones() != null ? c.getObservaciones() : "");

            resultado.add(item);
        }

        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint API para actualizar el estado de una cita en la base de datos
     * buscando ÚNICAMENTE entre los estados preexistentes en la BD.
     */
    @PostMapping("/agendaEmpleado/api/citas/{id}/estado")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarEstadoCita(@PathVariable("id") Integer idCita,
                                                                    @RequestBody Map<String, String> body,
                                                                    Principal principal) {
        Map<String, Object> resp = new HashMap<>();
        if (principal == null) {
            resp.put("status", "error");
            resp.put("mensaje", "No autenticado");
            return ResponseEntity.status(401).body(resp);
        }

        Optional<Cita> citaOpt = citaService.buscarPorId(idCita);
        if (citaOpt.isEmpty()) {
            resp.put("status", "error");
            resp.put("mensaje", "Cita no encontrada");
            return ResponseEntity.badRequest().body(resp);
        }

        String nuevoEstadoStr = body.get("estado");
        if (nuevoEstadoStr == null || nuevoEstadoStr.isBlank()) {
            resp.put("status", "error");
            resp.put("mensaje", "Estado no especificado");
            return ResponseEntity.badRequest().body(resp);
        }

        Estado estadoEncontrado = buscarEstadoExistente(nuevoEstadoStr.trim());
        if (estadoEncontrado == null) {
            resp.put("status", "error");
            resp.put("mensaje", "Estado no válido en la base de datos");
            return ResponseEntity.badRequest().body(resp);
        }

        Cita cita = citaOpt.get();
        cita.setEstado(estadoEncontrado);
        citaService.guardar(cita);

        resp.put("status", "success");
        resp.put("nuevoEstado", estadoEncontrado.getNombreEstado().toUpperCase());
        return ResponseEntity.ok(resp);
    }

    /**
     * Busca EXCLUSIVAMENTE entre los estados ya registrados en la base de datos
     * sin insertar ni crear registros adicionales.
     */
    private Estado buscarEstadoExistente(String nombreEstado) {
        String normalizado = nombreEstado.trim().toLowerCase();

        if (normalizado.startsWith("confirm")) {
            return estadoService.listar().stream()
                    .filter(e -> "Confirmada".equalsIgnoreCase(e.getNombreEstado())
                            || "Confirmado".equalsIgnoreCase(e.getNombreEstado()))
                    .findFirst()
                    .orElse(null);
        }
        if (normalizado.startsWith("complet")) {
            return estadoService.listar().stream()
                    .filter(e -> "Completada".equalsIgnoreCase(e.getNombreEstado())
                            || "Completado".equalsIgnoreCase(e.getNombreEstado()))
                    .findFirst()
                    .orElse(null);
        }
        if (normalizado.startsWith("cancel")) {
            return estadoService.listar().stream()
                    .filter(e -> "Cancelada".equalsIgnoreCase(e.getNombreEstado())
                            || "Cancelado".equalsIgnoreCase(e.getNombreEstado()))
                    .findFirst()
                    .orElse(null);
        }
        if (normalizado.startsWith("pend")) {
            return estadoService.listar().stream()
                    .filter(e -> "Pendiente".equalsIgnoreCase(e.getNombreEstado()))
                    .findFirst()
                    .orElse(null);
        }

        return estadoService.listar().stream()
                .filter(e -> e.getNombreEstado().equalsIgnoreCase(nombreEstado))
                .findFirst()
                .orElse(null);
    }

    private String nombreServicios(Cita cita, List<CitaServicio> todosDetalles) {
        List<CitaServicio> detalle = todosDetalles.stream()
                .filter(cs -> cs.getCita() != null && cita.getIdCita() != null
                        && cita.getIdCita().equals(cs.getCita().getIdCita()))
                .collect(Collectors.toList());

        if (detalle.isEmpty()) {
            return "Servicio General";
        }

        return detalle.stream()
                .map(cs -> cs.getServicio() != null ? cs.getServicio().getNombreServicio() : "Servicio")
                .collect(Collectors.joining(", "));
    }
}