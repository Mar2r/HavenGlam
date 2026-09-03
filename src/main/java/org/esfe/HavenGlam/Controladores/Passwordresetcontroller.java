package org.esfe.HavenGlam.Controladores;


import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.esfe.HavenGlam.Util.PasswordResetTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/recuperarClave")
public class Passwordresetcontroller {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private PasswordResetTokenUtil tokenUtil;

    @GetMapping
    public String mostrarSolicitud() {
        return "Auth/recuperarClave";
    }

    @PostMapping
    public String procesarSolicitud(@RequestParam("correo") String correo, Model model) {
        Optional<Usuario> usuario = usuarioService.buscarPorCorreo(correo);

        if (usuario.isEmpty()) {
            model.addAttribute("error", "No encontramos una cuenta con ese correo.");
            return "Auth/recuperarClave";
        }

        String token = tokenUtil.generarToken(usuario.get().getIdUsuario());
        String enlace = "/recuperarClave/reset?token=" + token;

        // TODO: cuando configuren un servidor SMTP, reemplazar estas dos líneas
        // por el envío real del correo, por ejemplo:
        //     mailService.enviarEnlaceRecuperacion(usuario.get().getCorreo(), enlaceCompleto);
        // y dejar de mostrar el enlace en pantalla.
        model.addAttribute("enlaceRecuperacion", enlace);
        model.addAttribute("mensaje", "Copia este enlace y ábrelo en el navegador para continuar (válido por 30 minutos).");

        return "Auth/recuperarClave";
    }

    @GetMapping("/reset")
    public String mostrarFormularioReset(@RequestParam("token") String token, Model model) {
        Optional<Integer> idUsuario = tokenUtil.validarToken(token);

        if (idUsuario.isEmpty()) {
            model.addAttribute("tokenInvalido", true);
            return "Auth/recuperarClaveReset";
        }

        model.addAttribute("token", token);
        return "Auth/recuperarClaveReset";
    }

    @PostMapping("/reset")
    public String procesarReset(@RequestParam("token") String token,
                                @RequestParam("nuevaContra") String nuevaContra,
                                Model model) {
        Optional<Integer> idUsuario = tokenUtil.validarToken(token);

        if (idUsuario.isEmpty()) {
            model.addAttribute("tokenInvalido", true);
            return "Auth/recuperarClaveReset";
        }

        Usuario usuario = usuarioService.buscarPorId(idUsuario.get())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // UsuarioService.guardar() cifra la contraseña automáticamente con BCrypt
        usuario.setContra(nuevaContra);
        usuarioService.guardar(usuario);

        return "redirect:/login?claveActualizada";
    }
}
