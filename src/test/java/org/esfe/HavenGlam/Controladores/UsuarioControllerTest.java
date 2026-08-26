package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Modelos.Rol;
import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IPersonaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IRolService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUsuarioService usuarioService;

    @Mock
    private IRolService rolService;

    @Mock
    private IEstadoService estadoService;

    @Mock
    private IPersonaService personaService;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuario;
    private Estado estado;
    private Rol rol;
    private Persona persona;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        estado = new Estado(1, "Activo", "General");
        rol = new Rol(1, "Cliente", estado);
        persona = new Persona(1, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");
        usuario = new Usuario(1, "juan@haven.com", "hashSeguro", rol, estado, persona);
    }

    @Test
    @DisplayName("GET /usuarios - Debe retornar la vista list con usuarios")
    void listar_RetornaVistaList() throws Exception {
        when(usuarioService.listar()).thenReturn(Arrays.asList(usuario));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/list"))
                .andExpect(model().attributeExists("usuarios"));

        verify(usuarioService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /usuarios/crear - Debe retornar la vista form con un nuevo Usuario y los combos")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        when(rolService.listar()).thenReturn(Arrays.asList(rol));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));
        when(personaService.listar()).thenReturn(Arrays.asList(persona));

        mockMvc.perform(get("/usuarios/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attributeExists("estados"))
                .andExpect(model().attributeExists("personas"));
    }

    @Test
    @DisplayName("GET /usuarios/editar/{id} - Debe retornar la vista form con el Usuario encontrado y los combos")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaForm() throws Exception {
        when(usuarioService.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(rolService.listar()).thenReturn(Arrays.asList(rol));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));
        when(personaService.listar()).thenReturn(Arrays.asList(persona));

        mockMvc.perform(get("/usuarios/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attributeExists("estados"))
                .andExpect(model().attributeExists("personas"));

        verify(usuarioService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /usuarios/guardar - Con correo disponible debe guardar y redireccionar")
    void guardar_ConCorreoDisponible_Redirecciona() throws Exception {
        when(usuarioService.buscarPorCorreo("nuevo@haven.com")).thenReturn(Optional.empty());
        when(usuarioService.guardar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios/guardar")
                        .param("correo", "nuevo@haven.com")
                        .param("contra", "123456")
                        .param("rol.idRol", "1")
                        .param("estado.idEstado", "1")
                        .param("persona.idPersona", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"));

        verify(usuarioService, times(1)).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("POST /usuarios/guardar - Con correo en uso por otro usuario debe retornar form con error")
    void guardar_ConCorreoEnUsoPorOtroUsuario_RetornaFormConError() throws Exception {
        Usuario otroUsuario = new Usuario(2, "juan@haven.com", "otroHash", rol, estado, persona);
        when(usuarioService.buscarPorCorreo("juan@haven.com")).thenReturn(Optional.of(otroUsuario));
        when(rolService.listar()).thenReturn(Arrays.asList(rol));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));
        when(personaService.listar()).thenReturn(Arrays.asList(persona));

        mockMvc.perform(post("/usuarios/guardar")
                        .param("idUsuario", "1")
                        .param("correo", "juan@haven.com")
                        .param("contra", "123456")
                        .param("rol.idRol", "1")
                        .param("estado.idEstado", "1")
                        .param("persona.idPersona", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attributeExists("errorCorreo"));

        verify(usuarioService, never()).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("GET /usuarios/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(usuarioService).eliminar(1);

        mockMvc.perform(get("/usuarios/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"));

        verify(usuarioService, times(1)).eliminar(1);
    }
}