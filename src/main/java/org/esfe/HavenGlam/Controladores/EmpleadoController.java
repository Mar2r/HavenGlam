package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.RegistroEmpleadoForm;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IRegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private IRegistroService registroService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empleados", empleadoService.listar());
        return "empleados/list";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("registroEmpleado", new RegistroEmpleadoForm());
        return "empleados/form";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("registroEmpleado") RegistroEmpleadoForm form,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            return "empleados/form";
        }
        try {
            registroService.registrarEmpleado(form);
        } catch (IllegalStateException ex) {
            model.addAttribute("errorRegistro", ex.getMessage());
            return "empleados/form";
        }
        return "redirect:/empleados";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Empleado empleado = empleadoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID: " + id));
        model.addAttribute("empleado", empleado);
        model.addAttribute("estados", estadoService.listar());
        return "empleados/editar";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("empleado") Empleado empleado,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("estados", estadoService.listar());
            return "empleados/editar";
        }
        empleadoService.guardar(empleado);
        return "redirect:/empleados";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        empleadoService.eliminar(id);
        return "redirect:/empleados";
    }
}