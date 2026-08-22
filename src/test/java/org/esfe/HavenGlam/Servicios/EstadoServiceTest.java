package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Repositorios.EstadoRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.EstadoService;
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
public class EstadoServiceTest {

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private EstadoService estadoService;

    private Estado estado1;
    private Estado estado2;

    @BeforeEach
    void setUp() {
        estado1 = new Estado(1, "Activo", "General");
        estado2 = new Estado(2, "Inactivo", "General");
    }

    @Test
    @DisplayName("Debe listar todos los estados correctamente")
    void listar_DevuelveListaDeEstados() {
        // Arrange
        when(estadoRepository.findAll()).thenReturn(Arrays.asList(estado1, estado2));

        // Act
        List<Estado> resultado = estadoService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Activo", resultado.get(0).getNombreEstado());
        verify(estadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un estado por ID existente")
    void buscarPorId_CuandoExiste_DevuelveEstado() {
        // Arrange
        when(estadoRepository.findById(1)).thenReturn(Optional.of(estado1));

        // Act
        Optional<Estado> resultado = estadoService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Activo", resultado.get().getNombreEstado());
        verify(estadoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el estado por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {
        // Arrange
        when(estadoRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        Optional<Estado> resultado = estadoService.buscarPorId(99);

        // Assert
        assertFalse(resultado.isPresent());
        verify(estadoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe guardar un estado correctamente")
    void guardar_GuardaYDevuelveEstado() {
        // Arrange
        when(estadoRepository.save(estado1)).thenReturn(estado1);

        // Act
        Estado resultado = estadoService.guardar(estado1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Activo", resultado.getNombreEstado());
        verify(estadoRepository, times(1)).save(estado1);
    }

    @Test
    @DisplayName("Debe eliminar un estado por ID")
    void eliminar_LlamaARepositorio() {
        // Arrange
        doNothing().when(estadoRepository).deleteById(1);

        // Act
        estadoService.eliminar(1);

        // Assert
        verify(estadoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si el estado existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {
        // Arrange
        when(estadoRepository.existsById(1)).thenReturn(true);

        // Act
        boolean resultado = estadoService.existePorId(1);

        // Assert
        assertTrue(resultado);
        verify(estadoRepository, times(1)).existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si el estado no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {
        // Arrange
        when(estadoRepository.existsById(99)).thenReturn(false);

        // Act
        boolean resultado = estadoService.existePorId(99);

        // Assert
        assertFalse(resultado);
        verify(estadoRepository, times(1)).existsById(99);
    }
}
