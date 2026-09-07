package org.esfe.HavenGlam.Controladores;

import jakarta.validation.Valid;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private IServicioService servicioService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private IEstadoService estadoService;

    @Autowired
    private IUploadService uploadService;

    @ModelAttribute
    public void agregarAtributosComunes(Model model) {
        model.addAttribute("activePage", "servicios");
        model.addAttribute("nombreAdmin", "Administrador");
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Servicios");
        model.addAttribute("pageSubtitle", "Catálogo y administración de servicios de belleza");
        model.addAttribute("servicios", servicioService.listar());
        return "servicios/index";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("pageTitle", "Nuevo Servicio");
        model.addAttribute("pageSubtitle", "Registrar un nuevo tratamiento en el catálogo");
        model.addAttribute("servicio", new Servicio());
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("estados", estadoService.listarPorTipo("General"));
        return "servicios/create";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("servicio") Servicio servicio,
                        BindingResult result,
                        @RequestParam(value = "file", required = false) MultipartFile file,
                        Model model) throws IOException {

        if (!result.hasErrors() && existeNombreDuplicado(servicio)) {
            result.rejectValue("nombreServicio", "duplicado", "Ya existe un servicio con este nombre");
        }

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Nuevo Servicio");
            model.addAttribute("pageSubtitle", "Registrar un nuevo tratamiento en el catálogo");
            model.addAttribute("categorias", categoriaService.listarActivas());
            model.addAttribute("estados", estadoService.listarPorTipo("General"));
            return "servicios/create";
        }
        if (file != null && !file.isEmpty()) {
            String url = uploadService.uploadFile(file);
            servicio.setImagenUrl(url);
        }
        servicioService.guardar(servicio);
        return "redirect:/servicios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
        model.addAttribute("pageTitle", "Editar Servicio");
        model.addAttribute("pageSubtitle", "Modificar información del servicio seleccionado");
        model.addAttribute("servicio", servicio);
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("estados", estadoService.listarPorTipo("General"));
        return "servicios/edit";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         @Valid @ModelAttribute("servicio") Servicio servicio,
                         BindingResult result,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         Model model) throws IOException {

        servicio.setIdServicio(id);

        if (!result.hasErrors() && existeNombreDuplicado(servicio)) {
            result.rejectValue("nombreServicio", "duplicado", "Ya existe otro servicio con este nombre");
        }

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Servicio");
            model.addAttribute("pageSubtitle", "Modificar información del servicio seleccionado");
            model.addAttribute("categorias", categoriaService.listarActivas());
            model.addAttribute("estados", estadoService.listarPorTipo("General"));
            return "servicios/edit";
        }

        if (file != null && !file.isEmpty()) {
            String url = uploadService.uploadFile(file);
            servicio.setImagenUrl(url);
        } else {
            Servicio existente = servicioService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
            servicio.setImagenUrl(existente.getImagenUrl());
        }

        servicioService.guardar(servicio);
        return "redirect:/servicios";
    }

    @GetMapping("/detalles/{id}")
    public String detalles(@PathVariable Integer id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
        model.addAttribute("pageTitle", "Detalle del Servicio");
        model.addAttribute("pageSubtitle", "Información detallada del servicio");
        model.addAttribute("servicio", servicio);
        return "servicios/details";
    }

    // En ServicioController (o un controller nuevo, p. ej. CatalogoClienteController)
    @GetMapping("/catalogo/servicios")
    public String inicioServicio(Model model) {
        model.addAttribute("servicios", servicioService.listar());
        return "servicios/inicioServicio";
    }
    @GetMapping("/eliminar/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable Integer id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
        model.addAttribute("pageTitle", "Eliminar Servicio");
        model.addAttribute("pageSubtitle", "Confirmación para dar de baja un servicio");
        model.addAttribute("servicio", servicio);
        return "servicios/delete";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        servicioService.eliminar(id);
        return "redirect:/servicios";
    }

    private boolean existeNombreDuplicado(Servicio servicio) {
        return servicioService.listar().stream()
                .anyMatch(s -> s.getNombreServicio().equalsIgnoreCase(servicio.getNombreServicio())
                        && (servicio.getIdServicio() == null || !s.getIdServicio().equals(servicio.getIdServicio())));
    }
}