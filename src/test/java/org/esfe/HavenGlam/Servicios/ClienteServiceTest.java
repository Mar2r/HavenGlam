package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Repositorios.ClienteRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.ClienteService;
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
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente1;
    private Cliente cliente2;
    private Estado estadoActivo;
    private Persona persona1;

    @BeforeEach
    void setUp() {
        estadoActivo = new Estado(1, "Activo", "General");
        persona1 = new Persona(1, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");
        cliente1 = new Cliente(1, persona1, estadoActivo);
        cliente2 = new Cliente(2, persona1, estadoActivo);
    }

    @Test
    @DisplayName("Debe listar todos los clientes correctamente")
    void listar_DevuelveListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente1, cliente2));

        List<Cliente> resultado = clienteService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un cliente por ID existente")
    void buscarPorId_CuandoExiste_DevuelveCliente() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente1));

        Optional<Cliente> resultado = clienteService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(persona1, resultado.get().getPersona());
        verify(clienteRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el cliente por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.buscarPorId(99);

        assertFalse(resultado.isPresent());
        verify(clienteRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe buscar un cliente por ID de persona existente")
    void buscarPorPersona_CuandoExiste_DevuelveCliente() {
        when(clienteRepository.findByPersona_IdPersona(1)).thenReturn(Optional.of(cliente1));

        Optional<Cliente> resultado = clienteService.buscarPorPersona(1);

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getIdCliente());
        verify(clienteRepository, times(1)).findByPersona_IdPersona(1);
    }

    @Test
    @DisplayName("Debe guardar un cliente correctamente")
    void guardar_GuardaYDevuelveCliente() {
        when(clienteRepository.save(cliente1)).thenReturn(cliente1);

        Cliente resultado = clienteService.guardar(cliente1);

        assertNotNull(resultado);
        assertEquals(estadoActivo, resultado.getEstado());
        verify(clienteRepository, times(1)).save(cliente1);
    }

    @Test
    @DisplayName("Debe eliminar un cliente por ID")
    void eliminar_LlamaARepositorio() {
        doNothing().when(clienteRepository).deleteById(1);

        clienteService.eliminar(1);

        verify(clienteRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si el cliente existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {
        when(clienteRepository.existsById(1)).thenReturn(true);

        assertTrue(clienteService.existePorId(1));
        verify(clienteRepository, times(1)).existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si el cliente no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {
        when(clienteRepository.existsById(99)).thenReturn(false);

        assertFalse(clienteService.existePorId(99));
        verify(clienteRepository, times(1)).existsById(99);
    }

    @Test
    @DisplayName("Debe retornar true si existe un cliente asociado a la persona")
    void existePorPersona_CuandoExiste_DevuelveTrue() {
        when(clienteRepository.existsByPersona_IdPersona(1)).thenReturn(true);

        assertTrue(clienteService.existePorPersona(1));
        verify(clienteRepository, times(1)).existsByPersona_IdPersona(1);
    }

    @Test
    @DisplayName("Debe retornar false si no existe un cliente asociado a la persona")
    void existePorPersona_CuandoNoExiste_DevuelveFalse() {
        when(clienteRepository.existsByPersona_IdPersona(99)).thenReturn(false);

        assertFalse(clienteService.existePorPersona(99));
        verify(clienteRepository, times(1)).existsByPersona_IdPersona(99);
    }
}