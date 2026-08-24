package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Producto;
import org.esfe.HavenGlam.Repositorios.ProductoRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto1;
    private Producto producto2;
    private Categoria categoria;
    private Estado estadoActivo;

    @BeforeEach
    void setUp() {
        estadoActivo = new Estado(1, "Activo", "General");
        categoria = new Categoria(1, "Cuidado de la Piel", estadoActivo);
        producto1 = new Producto(1, "Crema Hidratante", "Crema facial", new BigDecimal("18.50"),
                20, categoria, estadoActivo, null);
        producto2 = new Producto(2, "Protector Solar", "Protector SPF 50", new BigDecimal("22.00"),
                15, categoria, estadoActivo, null);
    }

    @Test
    @DisplayName("Debe listar todos los productos correctamente")
    void listar_DevuelveListaDeProductos() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto1, producto2));

        List<Producto> resultado = productoService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Crema Hidratante", resultado.get(0).getNombreProducto());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un producto por ID existente")
    void buscarPorId_CuandoExiste_DevuelveProducto() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto1));

        Optional<Producto> resultado = productoService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Crema Hidratante", resultado.get().getNombreProducto());
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el producto por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Producto> resultado = productoService.buscarPorId(99);

        assertFalse(resultado.isPresent());
        verify(productoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe guardar un producto correctamente")
    void guardar_GuardaYDevuelveProducto() {
        when(productoRepository.save(producto1)).thenReturn(producto1);

        Producto resultado = productoService.guardar(producto1);

        assertNotNull(resultado);
        assertEquals("Crema Hidratante", resultado.getNombreProducto());
        verify(productoRepository, times(1)).save(producto1);
    }

    @Test
    @DisplayName("Debe eliminar un producto por ID")
    void eliminar_LlamaARepositorio() {
        doNothing().when(productoRepository).deleteById(1);

        productoService.eliminar(1);

        verify(productoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si el producto existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {
        when(productoRepository.existsById(1)).thenReturn(true);

        boolean resultado = productoService.existePorId(1);

        assertTrue(resultado);
        verify(productoRepository, times(1)).existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si el producto no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {
        when(productoRepository.existsById(99)).thenReturn(false);

        boolean resultado = productoService.existePorId(99);

        assertFalse(resultado);
        verify(productoRepository, times(1)).existsById(99);
    }
}