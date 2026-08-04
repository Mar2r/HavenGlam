package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Servicios.Implementaciones.EstadoService;
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
@RequestMapping("/estados")
public class EstadoController
{
 @Autowired
    private EstadoService estadoService;
 @GetMapping
    public  String listar(Model model){
     model.addAttribute("estado", estadoService.listar());
     return "estado/list";
 }
 @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model){
     model.addAttribute("estado",new Estado());
     return  "estados/form";
 }
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Estado estado = estadoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado con ID: " + id));
        model.addAttribute("estado", estado);
        return "estados/form";
    }
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("estado") Estado estado,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return "estados/form";
        }
        estadoService.guardar(estado);
        return "redirect:/estados";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        estadoService.eliminar(id);
        return "redirect:/estados";
    }
}
