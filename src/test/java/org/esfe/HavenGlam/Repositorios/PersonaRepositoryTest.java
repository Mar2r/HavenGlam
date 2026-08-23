package org.esfe.HavenGlam.Repositorios;
import org.esfe.HavenGlam.Modelos.Persona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PersonaRepositoryTest {
    @Autowired
    private PersonaRepository personaRepository;

    @Test
    @DisplayName("Debe guardar una persona correctamente y generar su ID")
    void guardar_DebePersistirPersona(){
        Persona persona = new Persona(null, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");

        Persona guardada = personaRepository.save(persona);

        assertNotNull(guardada);
        assertNotNull(guardada.getIdPersona());
        assertEquals("Juan", guardada.getNombre());
        assertEquals("Pérez", guardada.getApellido());
        assertEquals("70000001", guardada.getTelefono());
        assertEquals("San Salvador", guardada.getDireccion());
        assertEquals("12345678-9", guardada.getDui());
    }
    @Test
    @DisplayName("Debe guardar una persona correctamente sin DUI(Camppo nulo)")
    void guardar_DebePersistirPersonaSinDUI(){
        Persona persona = new Persona(null, "María", "Gómez", "70000002", "Santa Ana", null);


        Persona guardada = personaRepository.save(persona);

        assertNotNull(guardada);
        assertNotNull(guardada.getIdPersona());
        assertNull(guardada.getDui());
    }
    @Test
    @DisplayName("Debe buscar una persona por su ID")
    void buscarPorId_DebeRetornarPersona(){
        Persona persona = new Persona(null, "Carlos", "López", "70000003", "San Miguel", "11223344-5");
        Persona guardada = personaRepository.save(persona);


        Optional<Persona> encontrada = personaRepository.findById(guardada.getIdPersona());
         assertTrue(encontrada.isPresent());
         assertEquals("Carlos", encontrada.get().getNombre());
    }
    @Test
    @DisplayName("Debe listar todas las personas existentes")
    void listar_DebeRetornarListaDePersonas(){
        personaRepository.save(new Persona(null, "Ana", "Martínez", "70000004", "Sonsonate", "22334455-6"));
        personaRepository.save(new Persona(null, "Luis", "Ramírez", "70000005", "La Libertad", "33445566-7"));

        // Act
        List<Persona> personas = personaRepository.findAll();

        // Assert
        assertNotNull(personas);
        assertTrue(personas.size() >= 2);
    }
    @Test
    @DisplayName("Debe actualizar los datos de una persona existente")
    void actualizar_DebeModificarPersona(){
        Persona persona = new Persona(null, "Pedro", "Hernández", "70000006", "Ahuachapán", "44556677-8");
        Persona guardada = personaRepository.save(persona);


        guardada.setTelefono("70009999");
        guardada.setDireccion("San Salvador Centro");
       Persona actualizada = personaRepository.save(guardada);

        assertEquals("70009999", actualizada.getTelefono());
        assertEquals("San Salvador Centro", actualizada.getDireccion());
    }
    @Test
    @DisplayName("Debe eliminar una persona por su ID")
    void eliminar_DebeRemoverPersona(){
        Persona persona = new Persona(null, "Sofía", "Castro", "70000007", "Usulután", "55667788-9");
        Persona guardada = personaRepository.save(persona);
        Integer id = guardada.getIdPersona();

        personaRepository.deleteById(id);

        Optional<Persona> eliminada = personaRepository.findById(id);
        assertFalse(eliminada.isPresent());
    }
}
