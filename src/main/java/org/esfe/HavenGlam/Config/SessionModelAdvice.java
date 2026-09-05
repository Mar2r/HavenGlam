package org.esfe.HavenGlam.Config;

import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SessionModelAdvice {

    @Autowired
    private IUsuarioService usuarioService;

    @ModelAttribute("nombreUsuarioSesion")
    public String nombreUsuarioSesion(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        return usuarioService.buscarPorCorreo(authentication.getName())
                .map(u -> u.getPersona().getNombre())
                .orElse(null);
    }
}