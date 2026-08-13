package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/servicios")
public class ServicioController {
    @Autowired
    private IServicioService servicioService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicios", servicioService.listar());
        return "servicios/list";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("servicio", new Servicio());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        return "servicios/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
        model.addAttribute("servicio", servicio);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        return "servicios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("servicio") Servicio servicio,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listar());
            model.addAttribute("estados", estadoService.listar());
            return "servicios/form";
        }
        servicioService.guardar(servicio);
        return "redirect:/servicios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        servicioService.eliminar(id);
        return "redirect:/servicios";
    }
}