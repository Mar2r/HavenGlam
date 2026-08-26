package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.*;
import org.esfe.HavenGlam.Repositorios.ClienteRepository;
import org.esfe.HavenGlam.Repositorios.EmpleadoRepository;
import org.esfe.HavenGlam.Repositorios.PersonaRepository;
import org.esfe.HavenGlam.Repositorios.UsuarioRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.RegistroService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IRolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistroServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private IRolService rolService;

    @Mock
    private IEstadoService estadoService;

    @InjectMocks
    private RegistroService registroService;

    private Estado estadoActivo;
    private Rol rolCliente;
    private Rol rolEmpleado;
    private RegistroClienteForm formCliente;
    private RegistroEmpleadoForm formEmpleado;

    @BeforeEach
    void setUp() {
        estadoActivo = new Estado(1, "Activo", "General");
        rolCliente = new Rol(1, "Cliente", estadoActivo);
        rolEmpleado = new Rol(2, "Empleado", estadoActivo);

        formCliente = new RegistroClienteForm();
        formCliente.setNombre("Juan");
        formCliente.setApellido("Pérez");
        formCliente.setTelefono("70000001");
        formCliente.setDireccion("San Salvador");
        formCliente.setDui("12345678-9");
        formCliente.setCorreo("juan@haven.com");
        formCliente.setContra("123456");

        formEmpleado = new RegistroEmpleadoForm();
        formEmpleado.setNombre("María");
        formEmpleado.setApellido("Gómez");
        formEmpleado.setTelefono("70000002");
        formEmpleado.setDireccion("San Salvador");
        formEmpleado.setDui("98765432-1");
        formEmpleado.setCorreo("maria@haven.com");
        formEmpleado.setContra("123456");
    }

    @Test
    @DisplayName("registrarCliente - Con datos válidos debe crear Persona, Usuario y Cliente correctamente")
    void registrarCliente_ConDatosValidos_CreaPersonaUsuarioYCliente() {
        Persona personaGuardada = new Persona(1, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");
        Cliente clienteGuardado = new Cliente(1, personaGuardada, estadoActivo);

        when(usuarioRepository.existsByCorreo("juan@haven.com")).thenReturn(false);
        when(personaRepository.save(any(Persona.class))).thenReturn(personaGuardada);
        when(rolService.listar()).thenReturn(Arrays.asList(rolCliente, rolEmpleado));
        when(estadoService.listar()).thenReturn(Collections.singletonList(estadoActivo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        Cliente resultado = registroService.registrarCliente(formCliente);

        assertNotNull(resultado);
        assertEquals(personaGuardada, resultado.getPersona());
        assertEquals(estadoActivo, resultado.getEstado());

        // Verifica el orden lógico: Persona -> Usuario (con IdPersona ya generado) -> Cliente
        verify(personaRepository, times(1)).save(any(Persona.class));

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        Usuario usuarioCreado = usuarioCaptor.getValue();
        assertEquals(personaGuardada, usuarioCreado.getPersona());
        assertEquals("juan@haven.com", usuarioCreado.getCorreo());
        assertEquals(rolCliente, usuarioCreado.getRol());
        assertEquals(estadoActivo, usuarioCreado.getEstado());

        ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository, times(1)).save(clienteCaptor.capture());
        assertEquals(personaGuardada, clienteCaptor.getValue().getPersona());
        assertEquals(estadoActivo, clienteCaptor.getValue().getEstado());

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    @DisplayName("registrarCliente - Con correo ya registrado debe lanzar excepción y no crear nada")
    void registrarCliente_ConCorreoYaRegistrado_LanzaExcepcion() {
        when(usuarioRepository.existsByCorreo("juan@haven.com")).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registroService.registrarCliente(formCliente));

        assertEquals("Ese correo ya está registrado", ex.getMessage());
        verify(personaRepository, never()).save(any(Persona.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("registrarCliente - Si no existe el rol 'Cliente' configurado debe lanzar excepción")
    void registrarCliente_CuandoNoExisteRolCliente_LanzaExcepcion() {
        Persona personaGuardada = new Persona(1, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");

        when(usuarioRepository.existsByCorreo("juan@haven.com")).thenReturn(false);
        when(personaRepository.save(any(Persona.class))).thenReturn(personaGuardada);
        when(rolService.listar()).thenReturn(Collections.singletonList(rolEmpleado));

        assertThrows(IllegalStateException.class,
                () -> registroService.registrarCliente(formCliente));

        verify(clienteRepository, never()).save(any(Cliente.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrarCliente - Si no existe el estado 'Activo' configurado debe lanzar excepción")
    void registrarCliente_CuandoNoExisteEstadoActivo_LanzaExcepcion() {
        Persona personaGuardada = new Persona(1, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");
        Estado estadoInactivo = new Estado(2, "Inactivo", "General");

        when(usuarioRepository.existsByCorreo("juan@haven.com")).thenReturn(false);
        when(personaRepository.save(any(Persona.class))).thenReturn(personaGuardada);
        when(rolService.listar()).thenReturn(Collections.singletonList(rolCliente));
        when(estadoService.listar()).thenReturn(Collections.singletonList(estadoInactivo));

        assertThrows(IllegalStateException.class,
                () -> registroService.registrarCliente(formCliente));

        verify(clienteRepository, never()).save(any(Cliente.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrarEmpleado - Con datos válidos debe crear Persona, Usuario y Empleado correctamente")
    void registrarEmpleado_ConDatosValidos_CreaPersonaUsuarioYEmpleado() {
        Persona personaGuardada = new Persona(2, "María", "Gómez", "70000002", "San Salvador", "98765432-1");
        Empleado empleadoGuardado = new Empleado(1, personaGuardada, estadoActivo);

        when(usuarioRepository.existsByCorreo("maria@haven.com")).thenReturn(false);
        when(personaRepository.save(any(Persona.class))).thenReturn(personaGuardada);
        when(rolService.listar()).thenReturn(Arrays.asList(rolCliente, rolEmpleado));
        when(estadoService.listar()).thenReturn(Collections.singletonList(estadoActivo));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoGuardado);

        Empleado resultado = registroService.registrarEmpleado(formEmpleado);

        assertNotNull(resultado);
        assertEquals(personaGuardada, resultado.getPersona());
        assertEquals(estadoActivo, resultado.getEstado());

        verify(personaRepository, times(1)).save(any(Persona.class));

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        assertEquals(rolEmpleado, usuarioCaptor.getValue().getRol());
        assertEquals(personaGuardada, usuarioCaptor.getValue().getPersona());

        verify(empleadoRepository, times(1)).save(any(Empleado.class));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("registrarEmpleado - Con correo ya registrado debe lanzar excepción y no crear nada")
    void registrarEmpleado_ConCorreoYaRegistrado_LanzaExcepcion() {
        when(usuarioRepository.existsByCorreo("maria@haven.com")).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registroService.registrarEmpleado(formEmpleado));

        assertEquals("Ese correo ya está registrado", ex.getMessage());
        verify(personaRepository, never()).save(any(Persona.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }
}