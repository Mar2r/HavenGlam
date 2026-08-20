package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Repositorios.CitaServicioRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.CitaServicioService;
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
public class CitaServicioServiceTest {

    @Mock
    private CitaServicioRepository citaServicioRepository;

    @InjectMocks
    private CitaServicioService citaServicioService;

    private CitaServicio citaServicio1;
    private CitaServicio citaServicio2;
    private Cita cita;
    private Servicio servicio;

    @BeforeEach
    void setUp() {

        // Objetos simulados para las relaciones
        // No necesitamos guardarlos en la base de datos.
        cita = new Cita();
        cita.setIdCita(1);

        servicio = new Servicio();
        servicio.setIdServicio(1);
        servicio.setNombreServicio("Corte de cabello");

        citaServicio1 = new CitaServicio(
                1,
                cita,
                servicio,
                new BigDecimal("25.00")
        );

        citaServicio2 = new CitaServicio(
                2,
                cita,
                servicio,
                new BigDecimal("35.00")
        );
    }

    @Test
    @DisplayName("Debe listar todas las citas-servicios correctamente")
    void listar_DevuelveListaDeCitaServicios() {

        // Arrange
        when(citaServicioRepository.findAll())
                .thenReturn(Arrays.asList(
                        citaServicio1,
                        citaServicio2
                ));

        // Act
        List<CitaServicio> resultado =
                citaServicioService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(
                new BigDecimal("25.00"),
                resultado.get(0).getPrecioAlMomento()
        );

        verify(citaServicioRepository, times(1))
                .findAll();
    }

    @Test
    @DisplayName("Debe buscar una cita-servicio por ID existente")
    void buscarPorId_CuandoExiste_DevuelveCitaServicio() {

        // Arrange
        when(citaServicioRepository.findById(1))
                .thenReturn(Optional.of(citaServicio1));

        // Act
        Optional<CitaServicio> resultado =
                citaServicioService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(
                new BigDecimal("25.00"),
                resultado.get().getPrecioAlMomento()
        );

        verify(citaServicioRepository, times(1))
                .findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando la cita-servicio no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {

        // Arrange
        when(citaServicioRepository.findById(99))
                .thenReturn(Optional.empty());

        // Act
        Optional<CitaServicio> resultado =
                citaServicioService.buscarPorId(99);

        // Assert
        assertFalse(resultado.isPresent());

        verify(citaServicioRepository, times(1))
                .findById(99);
    }

    @Test
    @DisplayName("Debe guardar una cita-servicio correctamente")
    void guardar_GuardaYDevuelveCitaServicio() {

        // Arrange
        when(citaServicioRepository.save(citaServicio1))
                .thenReturn(citaServicio1);

        // Act
        CitaServicio resultado =
                citaServicioService.guardar(citaServicio1);

        // Assert
        assertNotNull(resultado);
        assertEquals(
                new BigDecimal("25.00"),
                resultado.getPrecioAlMomento()
        );

        verify(citaServicioRepository, times(1))
                .save(citaServicio1);
    }

    @Test
    @DisplayName("Debe eliminar una cita-servicio por ID")
    void eliminar_LlamaARepositorio() {

        // Arrange
        doNothing()
                .when(citaServicioRepository)
                .deleteById(1);

        // Act
        citaServicioService.eliminar(1);

        // Assert
        verify(citaServicioRepository, times(1))
                .deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si la cita-servicio existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {

        // Arrange
        when(citaServicioRepository.existsById(1))
                .thenReturn(true);

        // Act
        boolean resultado =
                citaServicioService.existePorId(1);

        // Assert
        assertTrue(resultado);

        verify(citaServicioRepository, times(1))
                .existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si la cita-servicio no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {

        // Arrange
        when(citaServicioRepository.existsById(99))
                .thenReturn(false);

        // Act
        boolean resultado =
                citaServicioService.existePorId(99);

        // Assert
        assertFalse(resultado);

        verify(citaServicioRepository, times(1))
                .existsById(99);
    }
}