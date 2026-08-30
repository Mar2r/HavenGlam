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

    // ---------- INDEX (listado) ----------
    @GetMapping
    public String index(Model model) {
        model.addAttribute("servicios", servicioService.listar());
        return "servicios/index";
    }

    // ---------- CREATE ----------
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("servicio", new Servicio());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        return "servicios/create";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("servicio") Servicio servicio,
                        BindingResult result,
                        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listar());
            model.addAttribute("estados", estadoService.listar());
            return "servicios/create";
        }
        servicioService.guardar(servicio);
        return "redirect:/servicios";
    }

    // ---------- EDIT ----------
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
        model.addAttribute("servicio", servicio);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        return "servicios/edit";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         @Valid @ModelAttribute("servicio") Servicio servicio,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listar());
            model.addAttribute("estados", estadoService.listar());
            return "servicios/edit";
        }
        servicio.setIdServicio(id);
        servicioService.guardar(servicio);
        return "redirect:/servicios";
    }

    // ---------- DETAILS ----------
    @GetMapping("/detalles/{id}")
    public String detalles(@PathVariable Integer id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
        model.addAttribute("servicio", servicio);
        return "servicios/details";
    }

    // ---------- DELETE ----------
    @GetMapping("/eliminar/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable Integer id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
        model.addAttribute("servicio", servicio);
        return "servicios/delete";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        servicioService.eliminar(id);
        return "redirect:/servicios";
    }
}