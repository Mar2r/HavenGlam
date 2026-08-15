package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IPersonaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IRolService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRolService rolService;

    @Autowired
    private IEstadoService estadoService;

    @Autowired
    private IPersonaService personaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/list";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuario", new Usuario());
        cargarCombos(model);
        return "usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        model.addAttribute("usuario", usuario);
        cargarCombos(model);
        return "usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuario") Usuario usuario,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            cargarCombos(model);
            return "usuarios/form";
        }

        Optional<Usuario> existente = usuarioService.buscarPorCorreo(usuario.getCorreo());
        boolean correoEnUso = existente.isPresent()
                && !existente.get().getIdUsuario().equals(usuario.getIdUsuario());
        if (correoEnUso) {
            model.addAttribute("errorCorreo", "Ese correo ya está registrado por otro usuario");
            cargarCombos(model);
            return "usuarios/form";
        }

        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

    private void cargarCombos(Model model) {
        model.addAttribute("roles", rolService.listar());
        model.addAttribute("estados", estadoService.listar());
        model.addAttribute("personas", personaService.listar());
    }
}