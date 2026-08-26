package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Repositorios.EmpleadoRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.EmpleadoService;
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
public class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private Empleado empleado1;
    private Empleado empleado2;
    private Estado estadoActivo;
    private Persona persona1;

    @BeforeEach
    void setUp() {
        estadoActivo = new Estado(1, "Activo", "General");
        persona1 = new Persona(1, "María", "Gómez", "70000002", "San Salvador", "98765432-1");
        empleado1 = new Empleado(1, persona1, estadoActivo);
        empleado2 = new Empleado(2, persona1, estadoActivo);
    }

    @Test
    @DisplayName("Debe listar todos los empleados correctamente")
    void listar_DevuelveListaDeEmpleados() {
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(empleado1, empleado2));

        List<Empleado> resultado = empleadoService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un empleado por ID existente")
    void buscarPorId_CuandoExiste_DevuelveEmpleado() {
        when(empleadoRepository.findById(1)).thenReturn(Optional.of(empleado1));

        Optional<Empleado> resultado = empleadoService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(persona1, resultado.get().getPersona());
        verify(empleadoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el empleado por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {
        when(empleadoRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Empleado> resultado = empleadoService.buscarPorId(99);

        assertFalse(resultado.isPresent());
        verify(empleadoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe buscar un empleado por ID de persona existente")
    void buscarPorPersona_CuandoExiste_DevuelveEmpleado() {
        when(empleadoRepository.findByPersona_IdPersona(1)).thenReturn(Optional.of(empleado1));

        Optional<Empleado> resultado = empleadoService.buscarPorPersona(1);

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getIdEmpleado());
        verify(empleadoRepository, times(1)).findByPersona_IdPersona(1);
    }

    @Test
    @DisplayName("Debe guardar un empleado correctamente")
    void guardar_GuardaYDevuelveEmpleado() {
        when(empleadoRepository.save(empleado1)).thenReturn(empleado1);

        Empleado resultado = empleadoService.guardar(empleado1);

        assertNotNull(resultado);
        assertEquals(estadoActivo, resultado.getEstado());
        verify(empleadoRepository, times(1)).save(empleado1);
    }

    @Test
    @DisplayName("Debe eliminar un empleado por ID")
    void eliminar_LlamaARepositorio() {
        doNothing().when(empleadoRepository).deleteById(1);

        empleadoService.eliminar(1);

        verify(empleadoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si el empleado existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {
        when(empleadoRepository.existsById(1)).thenReturn(true);

        assertTrue(empleadoService.existePorId(1));
        verify(empleadoRepository, times(1)).existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si el empleado no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {
        when(empleadoRepository.existsById(99)).thenReturn(false);

        assertFalse(empleadoService.existePorId(99));
        verify(empleadoRepository, times(1)).existsById(99);
    }

    @Test
    @DisplayName("Debe retornar true si existe un empleado asociado a la persona")
    void existePorPersona_CuandoExiste_DevuelveTrue() {
        when(empleadoRepository.existsByPersona_IdPersona(1)).thenReturn(true);

        assertTrue(empleadoService.existePorPersona(1));
        verify(empleadoRepository, times(1)).existsByPersona_IdPersona(1);
    }

    @Test
    @DisplayName("Debe retornar false si no existe un empleado asociado a la persona")
    void existePorPersona_CuandoNoExiste_DevuelveFalse() {
        when(empleadoRepository.existsByPersona_IdPersona(99)).thenReturn(false);

        assertFalse(empleadoService.existePorPersona(99));
        verify(empleadoRepository, times(1)).existsByPersona_IdPersona(99);
    }
}