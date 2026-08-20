package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CitaServicioRepositoryTest {

    @Autowired
    private CitaServicioRepository citaServicioRepository;

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

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Cita cita;
    private Servicio servicio;

    @BeforeEach
    void setUp() {

        // Crear Estado
        Estado estadoActivo = estadoRepository.save(
                new Estado(null, "Activo", "General")
        );

        // Crear Persona para Cliente
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

        Cliente cliente = clienteRepository.save(
                new Cliente(
                        null,
                        personaCliente,
                        estadoActivo
                )
        );

        // Crear Persona para Empleado
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

        Empleado empleado = empleadoRepository.save(
                new Empleado(
                        null,
                        personaEmpleado,
                        estadoActivo
                )
        );

        // Crear Cita
        cita = citaRepository.save(
                new Cita(
                        null,
                        cliente,
                        empleado,
                        LocalDate.of(2026, 8, 20),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        estadoActivo,
                        "Cita de prueba",
                        LocalDateTime.now()
                )
        );

        // Crear Categoría
        Categoria categoria = categoriaRepository.save(
                new Categoria(
                        null,
                        "Belleza",
                        estadoActivo
                )
        );

        // Crear Servicio
        servicio = servicioRepository.save(
                new Servicio(
                        null,
                        "Corte de cabello",
                        "Servicio de corte de cabello",
                        new BigDecimal("25.00"),
                        LocalTime.of(1, 0),
                        categoria,
                        estadoActivo,
                        null
                )
        );
    }

    @Test
    @DisplayName("Debe guardar una cita-servicio correctamente y generar su ID")
    void guardar_DebePersistirCitaServicio() {

        // Arrange
        CitaServicio citaServicio = new CitaServicio(
                null,
                cita,
                servicio,
                new BigDecimal("25.00")
        );

        // Act
        CitaServicio guardado =
                citaServicioRepository.save(citaServicio);

        // Assert
        assertNotNull(guardado);
        assertNotNull(guardado.getIdCitaServicio());
        assertEquals(
                new BigDecimal("25.00"),
                guardado.getPrecioAlMomento()
        );
        assertNotNull(guardado.getCita());
        assertNotNull(guardado.getServicio());
    }

    @Test
    @DisplayName("Debe buscar una cita-servicio por su ID")
    void buscarPorId_DebeRetornarCitaServicio() {

        // Arrange
        CitaServicio citaServicio = new CitaServicio(
                null,
                cita,
                servicio,
                new BigDecimal("30.00")
        );

        CitaServicio guardado =
                citaServicioRepository.save(citaServicio);

        // Act
        Optional<CitaServicio> encontrado =
                citaServicioRepository.findById(
                        guardado.getIdCitaServicio()
                );

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals(
                new BigDecimal("30.00"),
                encontrado.get().getPrecioAlMomento()
        );
    }

    @Test
    @DisplayName("Debe listar todas las citas-servicios existentes")
    void listar_DebeRetornarListaDeCitaServicios() {

        // Arrange
        citaServicioRepository.save(
                new CitaServicio(
                        null,
                        cita,
                        servicio,
                        new BigDecimal("20.00")
                )
        );

        citaServicioRepository.save(
                new CitaServicio(
                        null,
                        cita,
                        servicio,
                        new BigDecimal("35.00")
                )
        );

        // Act
        List<CitaServicio> citaServicios =
                citaServicioRepository.findAll();

        // Assert
        assertNotNull(citaServicios);
        assertTrue(citaServicios.size() >= 2);
    }

    @Test
    @DisplayName("Debe actualizar el precio de una cita-servicio existente")
    void actualizar_DebeModificarCitaServicio() {

        // Arrange
        CitaServicio citaServicio = new CitaServicio(
                null,
                cita,
                servicio,
                new BigDecimal("25.00")
        );

        CitaServicio guardado =
                citaServicioRepository.save(citaServicio);

        // Act
        guardado.setPrecioAlMomento(
                new BigDecimal("40.00")
        );

        CitaServicio actualizado =
                citaServicioRepository.save(guardado);

        // Assert
        assertEquals(
                new BigDecimal("40.00"),
                actualizado.getPrecioAlMomento()
        );
    }

    @Test
    @DisplayName("Debe eliminar una cita-servicio por su ID")
    void eliminar_DebeRemoverCitaServicio() {

        // Arrange
        CitaServicio citaServicio = new CitaServicio(
                null,
                cita,
                servicio,
                new BigDecimal("45.00")
        );

        CitaServicio guardado =
                citaServicioRepository.save(citaServicio);

        Integer id = guardado.getIdCitaServicio();

        // Act
        citaServicioRepository.deleteById(id);

        Optional<CitaServicio> eliminado =
                citaServicioRepository.findById(id);

        // Assert
        assertFalse(eliminado.isPresent());
    }
}