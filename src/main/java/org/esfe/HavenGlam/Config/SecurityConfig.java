package org.esfe.HavenGlam.Config;

import org.esfe.HavenGlam.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return correo -> usuarioRepository.findByCorreo(correo)
                .map(usuario -> User.builder()
                        .username(usuario.getCorreo())
                        .password(usuario.getContra())
                        .roles(usuario.getRol().getNombreRol().toUpperCase())
                        .build())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + correo
                        )
                );
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService());

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {

            String redirectUrl = "/";

            for (GrantedAuthority authority : authentication.getAuthorities()) {

                String rol = authority.getAuthority();

                if (rol.equalsIgnoreCase("ROLE_ADMINISTRADOR")
                        || rol.equalsIgnoreCase("ROLE_ADMIN")) {

                    redirectUrl = "/admin/dashboard";
                    break;

                } else if (rol.equalsIgnoreCase("ROLE_EMPLEADO")) {

                    redirectUrl = "/agendaEmpleado";
                    break;

                } else if (rol.equalsIgnoreCase("ROLE_CLIENTE")) {

                    redirectUrl = "/";
                    break;
                }
            }

            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/",
                                "/login",
                                "/servicios",
                                "/productos",
                                "/citas/**",
                                "/clientes/crear",
                                "/clientes/registrar",
                                "/recuperarClave",
                                "/recuperarClave/**"
                        ).permitAll()
                        .requestMatchers(
                                "/admin/**",
                                "/empleados/**",
                                "/usuarios/**",
                                "/roles/**",
                                "/estados/**",
                                "/categorias/**",
                                "/servicios/**",
                                "/productos/**",
                                "/citaservicios/**"
                        ).hasRole("ADMINISTRADOR")
                        .requestMatchers(
                                "/agendaEmpleado/**"
                        ).hasRole("EMPLEADO")
                        .requestMatchers(
                                "/cliente/**"
                        ).hasRole("CLIENTE")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("correo")
                        .passwordParameter("contra")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}