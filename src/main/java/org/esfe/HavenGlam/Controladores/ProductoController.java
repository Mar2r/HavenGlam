package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Producto;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    @Autowired
    private IProductoService productoService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping
    public String Index(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos/index";
    }

    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        return "productos/create";
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        return "productos/edit";
    }

    @PostMapping("/create")
    public String guardar(@Valid @ModelAttribute("producto") Producto producto,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listar());
            model.addAttribute("estados", estadoService.listar());
            return "productos/index";
        }
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }
}