package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Rol;
import org.esfe.HavenGlam.Servicios.Implementaciones.RolService;
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
    @RequestMapping("/roles")
    public class RolController {
        @Autowired
        private RolService rolService;

        @GetMapping
        public String listar(Model model) {
            model.addAttribute("roles", rolService.listar());
            return "roles/list";
        }

        @GetMapping("/crear")
        public String mostrarFormularioCrear(Model model) {
            model.addAttribute("rol", new Rol());
            return "roles/form";
        }

        @GetMapping("/editar/{id}")
        public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
            Rol rol = rolService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
            model.addAttribute("rol", rol);
            return "roles/form";
        }

        @PostMapping("/guardar")
        public String guardar(@Valid @ModelAttribute("rol") Rol rol,
                              BindingResult result,
                              Model model) {
            if (result.hasErrors()) {
                return "roles/form";
            }
            rolService.guardar(rol);
            return "redirect:/roles";
        }

        @GetMapping("/eliminar/{id}")
        public String eliminar(@PathVariable Integer id) {
            rolService.eliminar(id);
            return "redirect:/roles";
        }
    }


