package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
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
public class CategoriaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    private Categoria categoria;
    private Estado estado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController).build();
        estado = new Estado(1, "Activo", "General");
        categoria = new Categoria(1, "Cabello", estado);
    }

    @Test
    @DisplayName("GET /categorias - Debe retornar la vista list con categorías")
    void listar_RetornaVistaList() throws Exception {
        when(categoriaService.listar()).thenReturn(Arrays.asList(categoria));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/list"))
                .andExpect(model().attributeExists("categorias"));

        verify(categoriaService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /categorias/crear - Debe retornar la vista form con una nueva Categoria")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        mockMvc.perform(get("/categorias/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/form"))
                .andExpect(model().attributeExists("categoria"));
    }

    @Test
    @DisplayName("GET /categorias/editar/{id} - Debe retornar la vista form con la Categoria encontrada")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaForm() throws Exception {
        when(categoriaService.buscarPorId(1)).thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/categorias/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/form"))
                .andExpect(model().attributeExists("categoria"));

        verify(categoriaService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /categorias/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(categoriaService.guardar(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/categorias/guardar")
                .param("nombreCategoria", "Facial")
                .param("estado.idEstado", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias"));

        verify(categoriaService, times(1)).guardar(any(Categoria.class));
    }

    @Test
    @DisplayName("GET /categorias/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(categoriaService).eliminar(1);

        mockMvc.perform(get("/categorias/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias"));

        verify(categoriaService, times(1)).eliminar(1);
    }
}
