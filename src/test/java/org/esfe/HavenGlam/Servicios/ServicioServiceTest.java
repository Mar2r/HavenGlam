package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Repositorios.ServicioRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.ServicioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ServicioService servicioService;

    private Servicio servicio1;
    private Servicio servicio2;
    private Categoria categoria;
    private Estado estadoActivo;

    @BeforeEach
    void setUp() {
        estadoActivo = new Estado(1, "Activo", "General");
        categoria = new Categoria(1, "Cabello", estadoActivo);
        servicio1 = new Servicio(1, "Corte de Cabello", "Corte clásico", new BigDecimal("15.00"),
                LocalTime.of(0, 30), categoria, estadoActivo, null);
        servicio2 = new Servicio(2, "Tinte", "Tinte completo", new BigDecimal("35.00"),
                LocalTime.of(1, 30), categoria, estadoActivo, null);
    }

    @Test
    @DisplayName("Debe listar todos los servicios correctamente")
    void listar_DevuelveListaDeServicios() {
        when(servicioRepository.findAll()).thenReturn(Arrays.asList(servicio1, servicio2));

        List<Servicio> resultado = servicioService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Corte de Cabello", resultado.get(0).getNombreServicio());
        verify(servicioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un servicio por ID existente")
    void buscarPorId_CuandoExiste_DevuelveServicio() {
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicio1));

        Optional<Servicio> resultado = servicioService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Corte de Cabello", resultado.get().getNombreServicio());
        verify(servicioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el servicio por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {
        when(servicioRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Servicio> resultado = servicioService.buscarPorId(99);

        assertFalse(resultado.isPresent());
        verify(servicioRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe guardar un servicio correctamente")
    void guardar_GuardaYDevuelveServicio() {
        when(servicioRepository.save(servicio1)).thenReturn(servicio1);

        Servicio resultado = servicioService.guardar(servicio1);

        assertNotNull(resultado);
        assertEquals("Corte de Cabello", resultado.getNombreServicio());
        verify(servicioRepository, times(1)).save(servicio1);
    }

    @Test
    @DisplayName("Debe eliminar un servicio por ID")
    void eliminar_LlamaARepositorio() {
        doNothing().when(servicioRepository).deleteById(1);

        servicioService.eliminar(1);

        verify(servicioRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si el servicio existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {
        when(servicioRepository.existsById(1)).thenReturn(true);

        boolean resultado = servicioService.existePorId(1);

        assertTrue(resultado);
        verify(servicioRepository, times(1)).existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si el servicio no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {
        when(servicioRepository.existsById(99)).thenReturn(false);

        boolean resultado = servicioService.existePorId(99);

        assertFalse(resultado);
        verify(servicioRepository, times(1)).existsById(99);
    }
}