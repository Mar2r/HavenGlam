package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/estados")
public class EstadoController {

    @Autowired
    private IEstadoService estadoService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("estados", estadoService.listar());
        return "estado/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("estado", new Estado());
        return "estado/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("estado") Estado estado, BindingResult result) {
        if (result.hasErrors()) {
            return "estado/create";
        }
        estadoService.guardar(estado);
        return "redirect:/estados";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Estado estado = estadoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + id));
        model.addAttribute("estado", estado);
        return "estado/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Integer id,
                       @Valid @ModelAttribute("estado") Estado estado,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "estado/edit";
        }
        estado.setIdEstado(id);
        estadoService.guardar(estado);
        return "redirect:/estados";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Integer id, Model model) {
        Estado estado = estadoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + id));
        model.addAttribute("estado", estado);
        return "estado/details";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        Estado estado = estadoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + id));
        model.addAttribute("estado", estado);
        return "estado/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteConfirmed(@PathVariable Integer id) {
        estadoService.eliminar(id);
        return "redirect:/estados";
    }
}