package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AgendaEmpleadoController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private ICitaService citaService;

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

        model.addAttribute("nombreEmpleado", usuario.getPersona().getNombre());
        model.addAttribute("citasHoy", citasHoy);

        return "empleado/agendaEmpleado";
    }
}