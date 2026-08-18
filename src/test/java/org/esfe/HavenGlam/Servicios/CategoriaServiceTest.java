package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Repositorios.CategoriaRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria1;
    private Categoria categoria2;
    private Estado estadoActivo;

    @BeforeEach
    void setUp() {
        estadoActivo = new Estado(1, "Activo", "General");
        categoria1 = new Categoria(1, "Maquillaje", estadoActivo);
        categoria2 = new Categoria(2, "Cuidado de la Piel", estadoActivo);
    }

    @Test
    @DisplayName("Debe listar todas las categorías correctamente")
    void listar_DevuelveListaDeCategorias() {
        // Arrange
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria1, categoria2));

        // Act
        List<Categoria> resultado = categoriaService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Maquillaje", resultado.get(0).getNombreCategoria());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar una categoría por ID existente")
    void buscarPorId_CuandoExiste_DevuelveCategoria() {
        // Arrange
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria1));

        // Act
        Optional<Categoria> resultado = categoriaService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Maquillaje", resultado.get().getNombreCategoria());
        verify(categoriaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando la categoría por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {
        // Arrange
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        Optional<Categoria> resultado = categoriaService.buscarPorId(99);

        // Assert
        assertFalse(resultado.isPresent());
        verify(categoriaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe guardar una categoría correctamente")
    void guardar_GuardaYDevuelveCategoria() {
        // Arrange
        when(categoriaRepository.save(categoria1)).thenReturn(categoria1);

        // Act
        Categoria resultado = categoriaService.guardar(categoria1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Maquillaje", resultado.getNombreCategoria());
        verify(categoriaRepository, times(1)).save(categoria1);
    }

    @Test
    @DisplayName("Debe eliminar una categoría por ID")
    void eliminar_LlamaARepositorio() {
        // Arrange
        doNothing().when(categoriaRepository).deleteById(1);

        // Act
        categoriaService.eliminar(1);

        // Assert
        verify(categoriaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si la categoría existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {
        // Arrange
        when(categoriaRepository.existsById(1)).thenReturn(true);

        // Act
        boolean resultado = categoriaService.existePorId(1);

        // Assert
        assertTrue(resultado);
        verify(categoriaRepository, times(1)).existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si la categoría no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {
        // Arrange
        when(categoriaRepository.existsById(99)).thenReturn(false);

        // Act
        boolean resultado = categoriaService.existePorId(99);

        // Assert
        assertFalse(resultado);
        verify(categoriaRepository, times(1)).existsById(99);
    }
}
