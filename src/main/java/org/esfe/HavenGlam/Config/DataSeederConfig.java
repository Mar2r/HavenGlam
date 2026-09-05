package org.esfe.HavenGlam.Config;

import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Modelos.Rol;
import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Repositorios.EstadoRepository;
import org.esfe.HavenGlam.Repositorios.PersonaRepository;
import org.esfe.HavenGlam.Repositorios.RolRepository;
import org.esfe.HavenGlam.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataSeederConfig {

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner sembrarAdmin() {
        return args -> {

            if (usuarioRepository.existsByCorreo("admin@havenglam.com")) {
                return; // ya existe, no hacer nada
            }

            // 1. Estado Activo (buscar o crear)
            Estado estadoActivo = estadoRepository.findByTipoEstado("General")
                    .stream()
                    .filter(e -> e.getNombreEstado().equalsIgnoreCase("Activo"))
                    .findFirst()
                    .orElseGet(() -> estadoRepository.save(new Estado(null, "Activo", "General")));

            // 2. Rol Administrador (buscar o crear)
            Rol rolAdmin = rolRepository.findAll().stream()
                    .filter(r -> r.getNombreRol().equalsIgnoreCase("Administrador"))
                    .findFirst()
                    .orElseGet(() -> rolRepository.save(new Rol(null, "Administrador", estadoActivo)));

            // 3. Persona
            Persona persona = new Persona();
            persona.setNombre("Admin");
            persona.setApellido("HavenGlam");
            persona.setTelefono("00000000");
            persona.setDireccion("San Salvador");
            persona.setDui(null);
            persona = personaRepository.save(persona);

            // 4. Usuario con contraseña CIFRADA
            Usuario admin = new Usuario();
            admin.setPersona(persona);
            admin.setCorreo("admin@havenglam.com");
            admin.setContra(passwordEncoder.encode("Admin123"));
            admin.setRol(rolAdmin);
            admin.setEstado(estadoActivo);
            usuarioRepository.save(admin);

            System.out.println(">>> Usuario admin creado: admin@havenglam.com / Admin123");
        };
    }
}