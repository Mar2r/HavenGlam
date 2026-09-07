package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Servicios.Interfaces.IProductoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CatalogoClienteController {

    @Autowired
    private IServicioService servicioService;

    @Autowired
    private IProductoService productoService;

    @GetMapping("/catalogo/servicios")
    public String inicioServicio(Model model) {
        model.addAttribute("servicios", servicioService.listar());
        return "servicios/inicioServicio";
    }

    @GetMapping("/catalogo/productos")
    public String inicioProducto(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos/inicioProducto";
    }
}