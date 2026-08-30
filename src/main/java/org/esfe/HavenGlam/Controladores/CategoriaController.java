package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("categorias", categoriaService.listar());
        return "categoria/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("estados", estadoService.listarPorTipo("General"));
        return "categoria/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("categoria") Categoria categoria,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("estados", estadoService.listarPorTipo("General"));
            return "categoria/create";
        }
        categoriaService.guardar(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        model.addAttribute("categoria", categoria);
        model.addAttribute("estados", estadoService.listarPorTipo("General"));
        return "categoria/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Integer id,
                       @Valid @ModelAttribute("categoria") Categoria categoria,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("estados", estadoService.listarPorTipo("General"));
            return "categoria/edit";
        }
        categoria.setIdCategoria(id);
        categoriaService.guardar(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Integer id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        model.addAttribute("categoria", categoria);
        return "categoria/details";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        model.addAttribute("categoria", categoria);
        return "categoria/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteConfirmed(@PathVariable Integer id) {
        categoriaService.eliminar(id);
        return "redirect:/categorias";
    }
}