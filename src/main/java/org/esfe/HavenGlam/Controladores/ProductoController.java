package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Producto;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IProductoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private IProductoService productoService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private IEstadoService estadoService;

    @Autowired
    private IUploadService uploadService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos/index";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("estados", estadoService.listarPorTipo("General"));
        return "productos/create";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("producto") Producto producto,
                        BindingResult result,
                        @RequestParam(value = "file", required = false) MultipartFile file,
                        Model model) throws IOException {

        if (!result.hasErrors() && existeNombreDuplicado(producto)) {
            result.rejectValue("nombreProducto", "duplicado", "Ya existe un producto con este nombre");
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarActivas());
            model.addAttribute("estados", estadoService.listarPorTipo("General"));
            return "productos/create";
        }
        if (file != null && !file.isEmpty()) {
            String url = uploadService.uploadFile(file);
            producto.setImagenUrl(url);
        }
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("estados", estadoService.listarPorTipo("General"));
        return "productos/edit";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         @Valid @ModelAttribute("producto") Producto producto,
                         BindingResult result,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         Model model) throws IOException {

        producto.setIdProducto(id);

        if (!result.hasErrors() && existeNombreDuplicado(producto)) {
            result.rejectValue("nombreProducto", "duplicado", "Ya existe otro producto con este nombre");
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarActivas());
            model.addAttribute("estados", estadoService.listarPorTipo("General"));
            return "productos/edit";
        }

        if (file != null && !file.isEmpty()) {
            String url = uploadService.uploadFile(file);
            producto.setImagenUrl(url);
        } else {
            Producto existente = productoService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
            producto.setImagenUrl(existente.getImagenUrl());
        }

        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @GetMapping("/detalles/{id}")
    public String detalles(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        model.addAttribute("producto", producto);
        return "productos/details";
    }

    @GetMapping("/eliminar/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        model.addAttribute("producto", producto);
        return "productos/delete";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }

    private boolean existeNombreDuplicado(Producto producto) {
        return productoService.listar().stream()
                .anyMatch(p -> p.getNombreProducto().equalsIgnoreCase(producto.getNombreProducto())
                        && (producto.getIdProducto() == null || !p.getIdProducto().equals(producto.getIdProducto())));
    }
}