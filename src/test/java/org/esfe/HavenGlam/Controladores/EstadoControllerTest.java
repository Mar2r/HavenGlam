package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
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
public class EstadoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IEstadoService estadoService;

    @InjectMocks
    private EstadoController estadoController;

    private Estado estado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(estadoController).build();
        estado = new Estado(1, "Activo", "General");
    }

    @Test
    @DisplayName("GET /estados - Debe retornar la vista list con estados")
    void listar_RetornaVistaList() throws Exception {
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));

        mockMvc.perform(get("/estados"))
                .andExpect(status().isOk())
                .andExpect(view().name("estados/list"))
                .andExpect(model().attributeExists("estados"));

        verify(estadoService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /estados/crear - Debe retornar la vista form con un nuevo Estado")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        mockMvc.perform(get("/estados/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("estados/form"))
                .andExpect(model().attributeExists("estado"));
    }

    @Test
    @DisplayName("GET /estados/editar/{id} - Debe retornar la vista form con el Estado encontrado")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaForm() throws Exception {
        when(estadoService.buscarPorId(1)).thenReturn(Optional.of(estado));

        mockMvc.perform(get("/estados/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("estados/form"))
                .andExpect(model().attributeExists("estado"));

        verify(estadoService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /estados/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(estadoService.guardar(any(Estado.class))).thenReturn(estado);

        mockMvc.perform(post("/estados/guardar")
                        .param("nombreEstado", "Inactivo")
                        .param("tipoEstado", "General"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estados"));

        verify(estadoService, times(1)).guardar(any(Estado.class));
    }

    @Test
    @DisplayName("GET /estados/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(estadoService).eliminar(1);

        mockMvc.perform(get("/estados/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estados"));

        verify(estadoService, times(1)).eliminar(1);
    }
}