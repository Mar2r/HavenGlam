package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Repositorios.CitaRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.CitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private CitaService citaService;

    private Cita cita1;
    private Cita cita2;
    private Cliente cliente;
    private Empleado empleado;
    private Estado estadoActivo;

    @BeforeEach
    void setUp() {

        estadoActivo = new Estado(1, "Activo", "General");

        // Como estamos haciendo pruebas unitarias con Mockito,
        // no necesitamos guardar Cliente ni Empleado en la base de datos.
        cliente = new Cliente();
        cliente.setIdCliente(1);

        empleado = new Empleado();
        empleado.setIdEmpleado(1);

        cita1 = new Cita(
                1,
                cliente,
                empleado,
                LocalDate.of(2026, 8, 20),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                estadoActivo,
                "Cita de prueba 1",
                LocalDateTime.now()
        );

        cita2 = new Cita(
                2,
                cliente,
                empleado,
                LocalDate.of(2026, 8, 21),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                estadoActivo,
                "Cita de prueba 2",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Debe listar todas las citas correctamente")
    void listar_DevuelveListaDeCitas() {

        // Arrange
        when(citaRepository.findAll())
                .thenReturn(Arrays.asList(cita1, cita2));

        // Act
        List<Cita> resultado = citaService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(
                "Cita de prueba 1",
                resultado.get(0).getObservaciones()
        );

        verify(citaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar una cita por ID existente")
    void buscarPorId_CuandoExiste_DevuelveCita() {

        // Arrange
        when(citaRepository.findById(1))
                .thenReturn(Optional.of(cita1));

        // Act
        Optional<Cita> resultado =
                citaService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(
                "Cita de prueba 1",
                resultado.get().getObservaciones()
        );

        verify(citaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando la cita no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {

        // Arrange
        when(citaRepository.findById(99))
                .thenReturn(Optional.empty());

        // Act
        Optional<Cita> resultado =
                citaService.buscarPorId(99);

        // Assert
        assertFalse(resultado.isPresent());

        verify(citaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe guardar una cita correctamente")
    void guardar_GuardaYDevuelveCita() {

        // Arrange
        when(citaRepository.save(cita1))
                .thenReturn(cita1);

        // Act
        Cita resultado =
                citaService.guardar(cita1);

        // Assert
        assertNotNull(resultado);
        assertEquals(
                "Cita de prueba 1",
                resultado.getObservaciones()
        );

        verify(citaRepository, times(1))
                .save(cita1);
    }

    @Test
    @DisplayName("Debe eliminar una cita por ID")
    void eliminar_LlamaARepositorio() {

        // Arrange
        doNothing()
                .when(citaRepository)
                .deleteById(1);

        // Act
        citaService.eliminar(1);

        // Assert
        verify(citaRepository, times(1))
                .deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si la cita existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {

        // Arrange
        when(citaRepository.existsById(1))
                .thenReturn(true);

        // Act
        boolean resultado =
                citaService.existePorId(1);

        // Assert
        assertTrue(resultado);

        verify(citaRepository, times(1))
                .existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si la cita no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {

        // Arrange
        when(citaRepository.existsById(99))
                .thenReturn(false);

        // Act
        boolean resultado =
                citaService.existePorId(99);

        // Assert
        assertFalse(resultado);

        verify(citaRepository, times(1))
                .existsById(99);
    }
}