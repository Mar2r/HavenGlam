package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ServicioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IServicioService servicioService;

    @Mock
    private ICategoriaService categoriaService;

    @Mock
    private IEstadoService estadoService;

    @InjectMocks
    private ServicioController servicioController;

    private Servicio servicio;
    private Categoria categoria;
    private Estado estado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(servicioController).build();
        estado = new Estado(1, "Activo", "General");
        categoria = new Categoria(1, "Cabello", estado);
        servicio = new Servicio(1, "Corte de Cabello", "Corte clásico", new BigDecimal("15.00"),
                LocalTime.of(0, 30), categoria, estado, null);
    }

    @Test
    @DisplayName("GET /servicios - Debe retornar la vista list con servicios")
    void listar_RetornaVistaList() throws Exception {
        when(servicioService.listar()).thenReturn(Arrays.asList(servicio));

        mockMvc.perform(get("/servicios"))
                .andExpect(status().isOk())
                .andExpect(view().name("servicios/list"))
                .andExpect(model().attributeExists("servicios"));

        verify(servicioService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /servicios/crear - Debe retornar la vista form con un nuevo Servicio")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        when(categoriaService.listar()).thenReturn(Arrays.asList(categoria));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));

        mockMvc.perform(get("/servicios/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("servicios/form"))
                .andExpect(model().attributeExists("servicio"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(model().attributeExists("estados"));
    }

    @Test
    @DisplayName("GET /servicios/editar/{id} - Debe retornar la vista form con el Servicio encontrado")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaForm() throws Exception {
        when(servicioService.buscarPorId(1)).thenReturn(Optional.of(servicio));
        when(categoriaService.listar()).thenReturn(Arrays.asList(categoria));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));

        mockMvc.perform(get("/servicios/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("servicios/form"))
                .andExpect(model().attributeExists("servicio"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(model().attributeExists("estados"));

        verify(servicioService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /servicios/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(servicioService.guardar(any(Servicio.class))).thenReturn(servicio);

        mockMvc.perform(post("/servicios/guardar")
                        .param("nombreServicio", "Manicure")
                        .param("precio", "10.00")
                        .param("duracionMinutos", "00:20:00")
                        .param("categoria.idCategoria", "1")
                        .param("estado.idEstado", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicios"));

        verify(servicioService, times(1)).guardar(any(Servicio.class));
    }

    @Test
    @DisplayName("GET /servicios/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(servicioService).eliminar(1);

        mockMvc.perform(get("/servicios/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicios"));

        verify(servicioService, times(1)).eliminar(1);
    }
}