package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Producto;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IProductoService;
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
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IProductoService productoService;

    @Mock
    private ICategoriaService categoriaService;

    @Mock
    private IEstadoService estadoService;

    @InjectMocks
    private ProductoController productoController;

    private Producto producto;
    private Categoria categoria;
    private Estado estado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        estado = new Estado(1, "Activo", "General");
        categoria = new Categoria(1, "Cuidado de la Piel", estado);
        producto = new Producto(1, "Crema Hidratante", "Crema facial", new BigDecimal("18.50"),
                20, categoria, estado, null);
    }

    @Test
    @DisplayName("GET /productos - Debe retornar la vista list con productos")
    void listar_RetornaVistaList() throws Exception {
        when(productoService.listar()).thenReturn(Arrays.asList(producto));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/list"))
                .andExpect(model().attributeExists("productos"));

        verify(productoService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /productos/crear - Debe retornar la vista form con un nuevo Producto")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        when(categoriaService.listar()).thenReturn(Arrays.asList(categoria));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));

        mockMvc.perform(get("/productos/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/form"))
                .andExpect(model().attributeExists("producto"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(model().attributeExists("estados"));
    }

    @Test
    @DisplayName("GET /productos/editar/{id} - Debe retornar la vista form con el Producto encontrado")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaForm() throws Exception {
        when(productoService.buscarPorId(1)).thenReturn(Optional.of(producto));
        when(categoriaService.listar()).thenReturn(Arrays.asList(categoria));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));

        mockMvc.perform(get("/productos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/form"))
                .andExpect(model().attributeExists("producto"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(model().attributeExists("estados"));

        verify(productoService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /productos/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(productoService.guardar(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/productos/guardar")
                        .param("nombreProducto", "Shampoo")
                        .param("precio", "12.00")
                        .param("stock", "30")
                        .param("categoria.idCategoria", "1")
                        .param("estado.idEstado", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService, times(1)).guardar(any(Producto.class));
    }

    @Test
    @DisplayName("GET /productos/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(productoService).eliminar(1);

        mockMvc.perform(get("/productos/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService, times(1)).eliminar(1);
    }
}