package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Servicios.Interfaces.IPersonaService;
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
@RequestMapping("/personas")
public class PersonaController {

    @Autowired
    private IPersonaService personaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("personas", personaService.listar());
        return "personas/list";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("persona", new Persona());
        return "personas/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Persona persona = personaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con ID: " + id));
        model.addAttribute("persona", persona);
        return "personas/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("persona") Persona persona,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return "personas/form";
        }
        personaService.guardar(persona);
        return "redirect:/personas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        personaService.eliminar(id);
        return "redirect:/personas";
    }
}