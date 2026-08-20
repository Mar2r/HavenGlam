package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CitaRepositoryTest {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    private Cliente cliente;
    private Empleado empleado;
    private Estado estadoActivo;

    @BeforeEach
    void setUp() {

        // Crear Estado
        estadoActivo = estadoRepository.save(
                new Estado(null, "Activo", "General")
        );

        // Crear Persona para el Cliente
        Persona personaCliente = personaRepository.save(
                new Persona(
                        null,
                        "Juan",
                        "Pérez",
                        "70000001",
                        "San Salvador",
                        "12345678-9"
                )
        );

        cliente = clienteRepository.save(
                new Cliente(
                        null,
                        personaCliente,
                        estadoActivo
                )
        );

        // Crear Persona para el Empleado
        Persona personaEmpleado = personaRepository.save(
                new Persona(
                        null,
                        "María",
                        "Gómez",
                        "70000002",
                        "San Salvador",
                        "98765432-1"
                )
        );

        empleado = empleadoRepository.save(
                new Empleado(
                        null,
                        personaEmpleado,
                        estadoActivo
                )
        );
    }

    @Test
    @DisplayName("Debe guardar una cita correctamente y generar su ID")
    void guardar_DebePersistirCita() {

        // Arrange
        Cita cita = new Cita(
                null,
                cliente,
                empleado,
                LocalDate.of(2026, 8, 20),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                estadoActivo,
                "Cita de prueba",
                LocalDateTime.now()
        );

        // Act
        Cita guardada = citaRepository.save(cita);

        // Assert
        assertNotNull(guardada);
        assertNotNull(guardada.getIdCita());
        assertEquals(LocalDate.of(2026, 8, 20), guardada.getFecha());
        assertEquals(LocalTime.of(10, 0), guardada.getHora());
        assertEquals(LocalTime.of(11, 0), guardada.getHoraFin());
        assertEquals("Cita de prueba", guardada.getObservaciones());
        assertEquals("Activo", guardada.getEstado().getNombreEstado());
    }

    @Test
    @DisplayName("Debe buscar una cita por su ID")
    void buscarPorId_DebeRetornarCita() {

        // Arrange
        Cita cita = new Cita(
                null,
                cliente,
                empleado,
                LocalDate.of(2026, 8, 21),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                estadoActivo,
                "Cita para búsqueda",
                LocalDateTime.now()
        );

        Cita guardada = citaRepository.save(cita);

        // Act
        Optional<Cita> encontrada =
                citaRepository.findById(guardada.getIdCita());

        // Assert
        assertTrue(encontrada.isPresent());
        assertEquals(
                "Cita para búsqueda",
                encontrada.get().getObservaciones()
        );
    }

    @Test
    @DisplayName("Debe listar todas las citas existentes")
    void listar_DebeRetornarListaDeCitas() {

        // Arrange
        citaRepository.save(
                new Cita(
                        null,
                        cliente,
                        empleado,
                        LocalDate.of(2026, 8, 22),
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        estadoActivo,
                        "Cita de manicura",
                        LocalDateTime.now()
                )
        );

        citaRepository.save(
                new Cita(
                        null,
                        cliente,
                        empleado,
                        LocalDate.of(2026, 8, 23),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        estadoActivo,
                        "Cita de pedicura",
                        LocalDateTime.now()
                )
        );

        // Act
        List<Cita> citas = citaRepository.findAll();

        // Assert
        assertNotNull(citas);
        assertTrue(citas.size() >= 2);
    }

    @Test
    @DisplayName("Debe actualizar las observaciones de una cita existente")
    void actualizar_DebeModificarCita() {

        // Arrange
        Cita cita = new Cita(
                null,
                cliente,
                empleado,
                LocalDate.of(2026, 8, 24),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                estadoActivo,
                "Observación original",
                LocalDateTime.now()
        );

        Cita guardada = citaRepository.save(cita);

        // Act
        guardada.setObservaciones("Observación actualizada");

        Cita actualizada = citaRepository.save(guardada);

        // Assert
        assertEquals(
                "Observación actualizada",
                actualizada.getObservaciones()
        );
    }

    @Test
    @DisplayName("Debe eliminar una cita por su ID")
    void eliminar_DebeRemoverCita() {

        // Arrange
        Cita cita = new Cita(
                null,
                cliente,
                empleado,
                LocalDate.of(2026, 8, 25),
                LocalTime.of(16, 0),
                LocalTime.of(17, 0),
                estadoActivo,
                "Cita para eliminar",
                LocalDateTime.now()
        );

        Cita guardada = citaRepository.save(cita);
        Integer id = guardada.getIdCita();

        // Act
        citaRepository.deleteById(id);

        Optional<Cita> eliminada =
                citaRepository.findById(id);

        // Assert
        assertFalse(eliminada.isPresent());
    }
}