package org.esfe.HavenGlam.Servicios;

import org.esfe.HavenGlam.Modelos.Rol;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Repositorios.RolRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.RolService;
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
public class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private Rol rol1;
    private Rol rol2;
    private Estado estadoActivo;

    @BeforeEach
    void setUp() {
        estadoActivo = new Estado(1, "Activo", "General");
        rol1 = new Rol(1, "Administrador", estadoActivo);
        rol2 = new Rol(2, "Empleado", estadoActivo);
    }

    @Test
    @DisplayName("Debe listar todos los roles correctamente")
    void listar_DevuelveListaDeRoles() {
        // Arrange
        when(rolRepository.findAll()).thenReturn(Arrays.asList(rol1, rol2));

        // Act
        List<Rol> resultado = rolService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Administrador", resultado.get(0).getNombreRol());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un rol por ID existente")
    void buscarPorId_CuandoExiste_DevuelveRol() {
        // Arrange
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol1));

        // Act
        Optional<Rol> resultado = rolService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Administrador", resultado.get().getNombreRol());
        verify(rolRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el rol por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {
        // Arrange
        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        Optional<Rol> resultado = rolService.buscarPorId(99);

        // Assert
        assertFalse(resultado.isPresent());
        verify(rolRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe guardar un rol correctamente")
    void guardar_GuardaYDevuelveRol() {
        // Arrange
        when(rolRepository.save(rol1)).thenReturn(rol1);

        // Act
        Rol resultado = rolService.guardar(rol1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Administrador", resultado.getNombreRol());
        verify(rolRepository, times(1)).save(rol1);
    }

    @Test
    @DisplayName("Debe eliminar un rol por ID")
    void eliminar_LlamaARepositorio() {
        // Arrange
        doNothing().when(rolRepository).deleteById(1);

        // Act
        rolService.eliminar(1);

        // Assert
        verify(rolRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar true si el rol existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {
        // Arrange
        when(rolRepository.existsById(1)).thenReturn(true);

        // Act
        boolean resultado = rolService.existePorId(1);

        // Assert
        assertTrue(resultado);
        verify(rolRepository, times(1)).existsById(1);
    }

    @Test
    @DisplayName("Debe retornar false si el rol no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {
        // Arrange
        when(rolRepository.existsById(99)).thenReturn(false);

        // Act
        boolean resultado = rolService.existePorId(99);

        // Assert
        assertFalse(resultado);
        verify(rolRepository, times(1)).existsById(99);
    }
}