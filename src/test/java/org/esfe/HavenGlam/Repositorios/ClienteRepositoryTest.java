package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Cliente;
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
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    private Estado estadoActivo;

    @BeforeEach
    void setUp() {
        // Se guarda un Estado base ya que Cliente tiene una relación obligatoria (@NotNull) con Estado
        estadoActivo = estadoRepository.save(new Estado(null, "Activo", "General"));
    }

    @Test
    @DisplayName("Debe guardar un cliente correctamente y generar su ID")
    void guardar_DebePersistirCliente() {
        Persona persona = personaRepository.save(
                new Persona(null, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9")
        );
        Cliente cliente = new Cliente(null, persona, estadoActivo);

        Cliente guardado = clienteRepository.save(cliente);

        assertNotNull(guardado);
        assertNotNull(guardado.getIdCliente());
        assertEquals("Juan", guardado.getPersona().getNombre());
        assertEquals("Activo", guardado.getEstado().getNombreEstado());
    }

    @Test
    @DisplayName("Debe buscar un cliente por su ID")
    void buscarPorId_DebeRetornarCliente() {
        Persona persona = personaRepository.save(
                new Persona(null, "Ana", "Martínez", "70000004", "Sonsonate", "22334455-6")
        );
        Cliente guardado = clienteRepository.save(new Cliente(null, persona, estadoActivo));

        Optional<Cliente> encontrado = clienteRepository.findById(guardado.getIdCliente());

        assertTrue(encontrado.isPresent());
        assertEquals("Ana", encontrado.get().getPersona().getNombre());
    }

    @Test
    @DisplayName("Debe buscar un cliente por el ID de su persona asociada")
    void buscarPorPersona_DebeRetornarCliente() {
        Persona persona = personaRepository.save(
                new Persona(null, "Luis", "Ramírez", "70000005", "La Libertad", "33445566-7")
        );
        clienteRepository.save(new Cliente(null, persona, estadoActivo));

        Optional<Cliente> encontrado = clienteRepository.findByPersona_IdPersona(persona.getIdPersona());

        assertTrue(encontrado.isPresent());
        assertEquals(persona.getIdPersona(), encontrado.get().getPersona().getIdPersona());
    }

    @Test
    @DisplayName("Debe confirmar si existe un cliente asociado a una persona")
    void existePorPersona_DebeRetornarTrueCuandoExiste() {
        Persona persona = personaRepository.save(
                new Persona(null, "Sofía", "Castro", "70000007", "Usulután", "55667788-9")
        );
        clienteRepository.save(new Cliente(null, persona, estadoActivo));

        assertTrue(clienteRepository.existsByPersona_IdPersona(persona.getIdPersona()));
    }

    @Test
    @DisplayName("Debe listar todos los clientes existentes")
    void listar_DebeRetornarListaDeClientes() {
        Persona persona1 = personaRepository.save(
                new Persona(null, "Pedro", "Hernández", "70000006", "Ahuachapán", "44556677-8")
        );
        Persona persona2 = personaRepository.save(
                new Persona(null, "María", "Gómez", "70000002", "Santa Ana", "98765432-1")
        );
        clienteRepository.save(new Cliente(null, persona1, estadoActivo));
        clienteRepository.save(new Cliente(null, persona2, estadoActivo));

        List<Cliente> clientes = clienteRepository.findAll();

        assertNotNull(clientes);
        assertTrue(clientes.size() >= 2);
    }

    @Test
    @DisplayName("Debe eliminar un cliente por su ID")
    void eliminar_DebeRemoverCliente() {
        Persona persona = personaRepository.save(
                new Persona(null, "Carlos", "López", "70000003", "San Miguel", "11223344-5")
        );
        Cliente guardado = clienteRepository.save(new Cliente(null, persona, estadoActivo));
        Integer id = guardado.getIdCliente();

        clienteRepository.deleteById(id);
        Optional<Cliente> eliminado = clienteRepository.findById(id);

        assertFalse(eliminado.isPresent());
    }
}