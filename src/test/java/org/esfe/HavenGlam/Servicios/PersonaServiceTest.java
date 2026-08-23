package org.esfe.HavenGlam.Servicios;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Repositorios.PersonaRepository;
import org.esfe.HavenGlam.Servicios.Implementaciones.PersonaService;
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
public class PersonaServiceTest {
    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaService personaService;

    private Persona persona1;
    private Persona persona2;

    @BeforeEach
    void setUp(){
        persona1 = new Persona(1, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");
        persona2 = new Persona(2, "María", "Gómez", "70000002", "Santa Ana", "98765432-1");
    }
    @Test
    @DisplayName("Debe listar todas las personas correctamente")
    void listar_DevuelveListaDePersonas(){
        when(personaRepository.findAll()).thenReturn(Arrays.asList(persona1, persona2));
        List<Persona> resultado = personaService.listar();

        assertNotNull(resultado);
        assertEquals(2,resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        verify(personaRepository, times(1)).findAll();
    }
    @Test
    @DisplayName("Debe buscar una persona por ID existente")
    void buscarPorId_CuandoExiste_DevuelvePersona(){
        when(personaRepository.findById(1)).thenReturn(Optional.of(persona1));

        Optional<Persona> resultado = personaService.buscarPorId(1);
        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
        verify(personaRepository, times(1)).findById(1);
    }
    @Test
    @DisplayName("Debe retornar un Optional vacío cuando la persona por ID no existe")
    void buscarPorId_CuandoNoExiste_DevuelveOptionalVacio() {

        when(personaRepository.findById(99)).thenReturn(Optional.empty());


        Optional<Persona> resultado = personaService.buscarPorId(99);


        assertFalse(resultado.isPresent());
        verify(personaRepository, times(1)).findById(99);
    }
    @Test
    @DisplayName("Debe guardar una persona nueva correctamente (sin ID)")
    void guardar_PersonaNueva_GuardaYDevuelvePersona() {

        Persona nueva = new Persona(null, "Carlos", "López", "70000003", "San Miguel", "11223344-5");
        when(personaRepository.save(nueva)).thenReturn(persona1);


        Persona resultado = personaService.guardar(nueva);


        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(personaRepository, times(1)).save(nueva);
    }
    @Test
    @DisplayName("Debe guardar (actualizar) una persona existente correctamente")
    void guardar_PersonaExistente_ActualizaYDevuelvePersona() {

        when(personaRepository.existsById(1)).thenReturn(true);
        when(personaRepository.save(persona1)).thenReturn(persona1);


        Persona resultado = personaService.guardar(persona1);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(personaRepository, times(1)).existsById(1);
        verify(personaRepository, times(1)).save(persona1);
    }
    @Test
    @DisplayName("Debe eliminar una persona por ID")
    void eliminar_LlamaARepositorio() {

        doNothing().when(personaRepository).deleteById(1);


        personaService.eliminar(1);


        verify(personaRepository, times(1)).deleteById(1);
    }
    @Test
    @DisplayName("Debe retornar true si la persona existe por ID")
    void existePorId_CuandoExiste_DevuelveTrue() {

        when(personaRepository.existsById(1)).thenReturn(true);


        boolean resultado = personaService.existePorId(1);


        assertTrue(resultado);
        verify(personaRepository, times(1)).existsById(1);
    }
    @Test
    @DisplayName("Debe retornar false si la persona no existe por ID")
    void existePorId_CuandoNoExiste_DevuelveFalse() {

        when(personaRepository.existsById(99)).thenReturn(false);


        boolean resultado = personaService.existePorId(99);


        assertFalse(resultado);
        verify(personaRepository, times(1)).existsById(99);
    }

}
