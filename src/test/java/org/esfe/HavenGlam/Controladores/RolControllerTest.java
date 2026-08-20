package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Rol;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Servicios.Interfaces.IRolService;
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
public class RolControllerTest {
    private MockMvc mockMvc;

    @Mock
    private IRolService rolService;

    @InjectMocks
    private RolController rolController;

    private Rol rol;
    private Estado estado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rolController).build();
        estado = new Estado(1, "Activo", "General");
        rol = new Rol(1, "Administrador", estado);
    }

    @Test
    @DisplayName("GET /roles - Debe retornar la vista list con roles")
    void listar_RetornaVistaList() throws Exception {
        when(rolService.listar()).thenReturn(Arrays.asList(rol));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(view().name("roles/list"))
                .andExpect(model().attributeExists("roles"));

        verify(rolService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /roles/crear - Debe retornar la vista form con un nuevo Rol")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        mockMvc.perform(get("/roles/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("roles/form"))
                .andExpect(model().attributeExists("rol"));
    }

    @Test
    @DisplayName("GET /roles/editar/{id} - Debe retornar la vista form con el Rol encontrado")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaForm() throws Exception {
        when(rolService.buscarPorId(1)).thenReturn(Optional.of(rol));

        mockMvc.perform(get("/roles/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("roles/form"))
                .andExpect(model().attributeExists("rol"));

        verify(rolService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /roles/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(rolService.guardar(any(Rol.class))).thenReturn(rol);

        mockMvc.perform(post("/roles/guardar")
                        .param("nombreRol", "Empleado")
                        .param("estado.idEstado", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/roles"));

        verify(rolService, times(1)).guardar(any(Rol.class));
    }

    @Test
    @DisplayName("GET /roles/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(rolService).eliminar(1);

        mockMvc.perform(get("/roles/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/roles"));

        verify(rolService, times(1)).eliminar(1);
    }
}
