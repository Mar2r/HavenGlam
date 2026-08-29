package org.esfe.HavenGlam.Controladores;


import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String index(){

        return "index";
    }
}
