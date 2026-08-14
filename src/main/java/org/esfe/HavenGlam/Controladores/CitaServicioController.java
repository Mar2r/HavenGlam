package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/citaservicios")
public class CitaServicioController {
    @Autowired
    private ICitaServicioService citaServicioService;
    @Autowired
    private ICitaService citaService;
    @Autowired
    private IServicioService servicioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citaServicios", citaServicioService.listar());
        return "citaservicios/list";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("citaServicio", new CitaServicio());
        model.addAttribute("citas", citaService.listar());
        model.addAttribute("servicios", servicioService.listar());
        return "citaservicios/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        CitaServicio citaServicio = citaServicioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("CitaServicio no encontrado con ID: " + id));
        model.addAttribute("citaServicio", citaServicio);
        model.addAttribute("citas", citaService.listar());
        model.addAttribute("servicios", servicioService.listar());
        return "citaservicios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("citaServicio") CitaServicio citaServicio,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("citas", citaService.listar());
            model.addAttribute("servicios", servicioService.listar());
            return "citaservicios/form";
        }
        citaServicioService.guardar(citaServicio);
        return "redirect:/citaservicios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        citaServicioService.eliminar(id);
        return "redirect:/citaservicios";
    }
}