package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/agendaEmpleado")
    public String agenda(Model model, Principal principal) {

        Usuario usuario = usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        Empleado empleado = empleadoService.buscarPorPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new IllegalStateException("Este usuario no tiene un perfil de Empleado asociado"));

        LocalDate hoy = LocalDate.now();

        List<Cita> citasHoy = citaService.listar().stream()
                .filter(c -> c.getEmpleado() != null && empleado.getIdEmpleado().equals(c.getEmpleado().getIdEmpleado()))
                .filter(c -> hoy.equals(c.getFecha()))
                .sorted(Comparator.comparing(Cita::getHora))
                .collect(Collectors.toList());

        List<Map<String, Object>> agenda = new ArrayList<>();
        for (Cita c : citasHoy) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("hora", c.getHora());

            String nombreCliente = c.getCliente() != null && c.getCliente().getPersona() != null
                    ? c.getCliente().getPersona().getNombre() + " " + c.getCliente().getPersona().getApellido()
                    : "Cliente";
            item.put("cliente", nombreCliente);

            String nombreEmpleado = c.getEmpleado() != null && c.getEmpleado().getPersona() != null
                    ? c.getEmpleado().getPersona().getNombre() + " " + c.getEmpleado().getPersona().getApellido()
                    : "Empleado";
            item.put("empleado", nombreEmpleado);

            item.put("servicio", nombreServicios(c));

            String estado = c.getEstado() != null ? c.getEstado().getNombreEstado() : "Pendiente";
            item.put("estado", estado.toUpperCase());
            item.put("tagClass", "tag-" + estado.toLowerCase());
            item.put("esAhora", false);

            agenda.add(item);
        }

        model.addAttribute("nombreEmpleado", usuario.getPersona().getNombre());
        model.addAttribute("citasHoy", agenda);
        model.addAttribute("citasHoyCount", agenda.size());

        return "empleado/agendaEmpleado";
    }

    private String nombreServicios(Cita cita) {
        List<CitaServicio> detalle = citaServicioService.listar().stream()
                .filter(cs -> cs.getCita() != null && cita.getIdCita() != null
                        && cita.getIdCita().equals(cs.getCita().getIdCita()))
                .collect(Collectors.toList());

        if (detalle.isEmpty()) {
            return "Servicio";
        }

        return detalle.stream()
                .map(cs -> cs.getServicio() != null ? cs.getServicio().getNombreServicio() : "Servicio")
                .collect(Collectors.joining(", "));
    }
}