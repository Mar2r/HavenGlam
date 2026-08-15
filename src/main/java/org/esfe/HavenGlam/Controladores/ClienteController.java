package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.RegistroClienteForm;
import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Servicios.Interfaces.IClienteService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IRegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IRegistroService registroService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes/list";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("registroCliente", new RegistroClienteForm());
        return "clientes/form";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("registroCliente") RegistroClienteForm form,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            return "clientes/form";
        }
        try {
            registroService.registrarCliente(form);
        } catch (IllegalStateException ex) {
            model.addAttribute("errorRegistro", ex.getMessage());
            return "clientes/form";
        }
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        model.addAttribute("cliente", cliente);
        model.addAttribute("estados", estadoService.listar());
        return "clientes/editar";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("estados", estadoService.listar());
            return "clientes/editar";
        }
        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        clienteService.eliminar(id);
        return "redirect:/clientes";
    }
}