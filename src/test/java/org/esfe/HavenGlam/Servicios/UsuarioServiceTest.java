package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Modelos.Rol;
import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Repositorios.UsuarioRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.UsuarioService;
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
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario1;
    private Usuario usuario2;
    private Estado estadoActivo;
    private Rol rolCliente;
    private Persona persona1;

    @BeforeEach
    void setUp() {
        estadoActivo = new Estado(1, "Activo", "General");
        rolCliente = new Rol(1, "Cliente", estadoActivo);
        persona1 = new Persona(1, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");
        usuario1 = new Usuario(1, "juan@haven.com", "hash1", rolCliente, estadoActivo, persona1);
        usuario2 = new Usuario(2, "maria@haven.com", "hash2", rolCliente, estadoActivo, persona1);
    }

    @Test
    @DisplayName("Debe listar todos los usuarios correctamente")
    void listar_DevuelveListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        List<Usuario> resultado = usuarioService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un usuario por ID existente")
    void buscarPorId_CuandoExiste_DevuelveUsuario() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));

        Optional<Usuario> resultado = usuarioService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("juan@haven.com", resultado.get().getCorreo());
        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el usuario por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorId(99);

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe buscar un usuario por correo existente")
    void buscarPorCorreo_CuandoExiste_DevuelveUsuario() {
        when(usuarioRepository.findByCorreo("juan@haven.com")).thenReturn(Optional.of(usuario1));

        Optional<Usuario> resultado = usuarioService.buscarPorCorreo("juan@haven.com");

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getIdUsuario());
        verify(usuarioRepository, times(1)).findByCorreo("juan@haven.com");
    }

    @Test
    @DisplayName("Debe guardar un usuario correctamente")
    void guardar_GuardaYDevuelveUsuario() {
        when(usuarioRepository.save(usuario1)).thenReturn(usuario1);

        Usuario resultado = usuarioService.guardar(usuario1);

        assertNotNull(resultado);
        assertEquals(rolCliente, resultado.getRol());
        verify(usuarioRepository, times(1)).save(usuario1);
    }

    @Test
    @DisplayName("Debe eliminar un usuario por ID")
    void eliminar_LlamaARepositorio() {
        doNothing().when(usuarioRepository).deleteById(1);

        usuarioService.eliminar(1);

        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si el usuario existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {
        when(usuarioRepository.existsById(1)).thenReturn(true);

        assertTrue(usuarioService.existePorId(1));
        verify(usuarioRepository, times(1)).existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si el usuario no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {
        when(usuarioRepository.existsById(99)).thenReturn(false);

        assertFalse(usuarioService.existePorId(99));
        verify(usuarioRepository, times(1)).existsById(99);
    }

    @Test
    @DisplayName("Debe retornar true si existe un usuario con ese correo")
    void existePorCorreo_CuandoExiste_DevuelveTrue() {
        when(usuarioRepository.existsByCorreo("juan@haven.com")).thenReturn(true);

        assertTrue(usuarioService.existePorCorreo("juan@haven.com"));
        verify(usuarioRepository, times(1)).existsByCorreo("juan@haven.com");
    }

    @Test
    @DisplayName("Debe retornar false si no existe un usuario con ese correo")
    void existePorCorreo_CuandoNoExiste_DevuelveFalse() {
        when(usuarioRepository.existsByCorreo("noexiste@haven.com")).thenReturn(false);

        assertFalse(usuarioService.existePorCorreo("noexiste@haven.com"));
        verify(usuarioRepository, times(1)).existsByCorreo("noexiste@haven.com");
    }
}