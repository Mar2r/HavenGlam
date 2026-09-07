package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private IServicioService servicioService;

    @GetMapping
    public String index(Model model) {

        List<Servicio> conImagen = servicioService.listar().stream()
                .filter(s -> s.getImagenUrl() != null && !s.getImagenUrl().isBlank())
                .collect(Collectors.toList());

        Servicio heroServicio = conImagen.isEmpty() ? null : conImagen.get(0);

        List<Servicio> serviciosDestacados = conImagen.stream()
                .limit(4)
                .collect(Collectors.toList());

        model.addAttribute("heroServicio", heroServicio);
        model.addAttribute("serviciosDestacados", serviciosDestacados);

        return "index";
    }
}