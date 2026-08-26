package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.junit.jupiter.api.BeforeEach;
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
public class EmpleadoRepositoryTest {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    private Estado estadoActivo;

    @BeforeEach
    void setUp() {
        // Se guarda un Estado base ya que Empleado tiene una relación obligatoria (@NotNull) con Estado
        estadoActivo = estadoRepository.save(new Estado(null, "Activo", "General"));
    }

    @Test
    @DisplayName("Debe guardar un empleado correctamente y generar su ID")
    void guardar_DebePersistirEmpleado() {
        Persona persona = personaRepository.save(
                new Persona(null, "María", "Gómez", "70000002", "San Salvador", "98765432-1")
        );
        Empleado empleado = new Empleado(null, persona, estadoActivo);

        Empleado guardado = empleadoRepository.save(empleado);

        assertNotNull(guardado);
        assertNotNull(guardado.getIdEmpleado());
        assertEquals("María", guardado.getPersona().getNombre());
        assertEquals("Activo", guardado.getEstado().getNombreEstado());
    }

    @Test
    @DisplayName("Debe buscar un empleado por su ID")
    void buscarPorId_DebeRetornarEmpleado() {
        Persona persona = personaRepository.save(
                new Persona(null, "Ana", "Martínez", "70000004", "Sonsonate", "22334455-6")
        );
        Empleado guardado = empleadoRepository.save(new Empleado(null, persona, estadoActivo));

        Optional<Empleado> encontrado = empleadoRepository.findById(guardado.getIdEmpleado());

        assertTrue(encontrado.isPresent());
        assertEquals("Ana", encontrado.get().getPersona().getNombre());
    }

    @Test
    @DisplayName("Debe buscar un empleado por el ID de su persona asociada")
    void buscarPorPersona_DebeRetornarEmpleado() {
        Persona persona = personaRepository.save(
                new Persona(null, "Luis", "Ramírez", "70000005", "La Libertad", "33445566-7")
        );
        empleadoRepository.save(new Empleado(null, persona, estadoActivo));

        Optional<Empleado> encontrado = empleadoRepository.findByPersona_IdPersona(persona.getIdPersona());

        assertTrue(encontrado.isPresent());
        assertEquals(persona.getIdPersona(), encontrado.get().getPersona().getIdPersona());
    }

    @Test
    @DisplayName("Debe confirmar si existe un empleado asociado a una persona")
    void existePorPersona_DebeRetornarTrueCuandoExiste() {
        Persona persona = personaRepository.save(
                new Persona(null, "Sofía", "Castro", "70000007", "Usulután", "55667788-9")
        );
        empleadoRepository.save(new Empleado(null, persona, estadoActivo));

        assertTrue(empleadoRepository.existsByPersona_IdPersona(persona.getIdPersona()));
    }

    @Test
    @DisplayName("Debe listar todos los empleados existentes")
    void listar_DebeRetornarListaDeEmpleados() {
        Persona persona1 = personaRepository.save(
                new Persona(null, "Pedro", "Hernández", "70000006", "Ahuachapán", "44556677-8")
        );
        Persona persona2 = personaRepository.save(
                new Persona(null, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9")
        );
        empleadoRepository.save(new Empleado(null, persona1, estadoActivo));
        empleadoRepository.save(new Empleado(null, persona2, estadoActivo));

        List<Empleado> empleados = empleadoRepository.findAll();

        assertNotNull(empleados);
        assertTrue(empleados.size() >= 2);
    }

    @Test
    @DisplayName("Debe eliminar un empleado por su ID")
    void eliminar_DebeRemoverEmpleado() {
        Persona persona = personaRepository.save(
                new Persona(null, "Carlos", "López", "70000003", "San Miguel", "11223344-5")
        );
        Empleado guardado = empleadoRepository.save(new Empleado(null, persona, estadoActivo));
        Integer id = guardado.getIdEmpleado();

        empleadoRepository.deleteById(id);
        Optional<Empleado> eliminado = empleadoRepository.findById(id);

        assertFalse(eliminado.isPresent());
    }
}