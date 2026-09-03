package org.esfe.HavenGlam.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class Logincontroller {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "Auth/login";
    }
}
