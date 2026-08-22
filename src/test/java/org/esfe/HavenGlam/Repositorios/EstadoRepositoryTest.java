package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Estado;
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
public class EstadoRepositoryTest {

    @Autowired
    private EstadoRepository estadoRepository;

    @Test
    @DisplayName("Debe guardar un estado correctamente y generar su ID")
    void guardar_DebePersistirEstado() {
        // Arrange
        Estado estado = new Estado(null, "Activo", "General");

        // Act
        Estado guardado = estadoRepository.save(estado);

        // Assert
        assertNotNull(guardado);
        assertNotNull(guardado.getIdEstado());
        assertEquals("Activo", guardado.getNombreEstado());
        assertEquals("General", guardado.getTipoEstado());
    }

    @Test
    @DisplayName("Debe buscar un estado por su ID")
    void buscarPorId_DebeRetornarEstado() {
        // Arrange
        Estado estado = new Estado(null, "Inactivo", "General");
        Estado guardado = estadoRepository.save(estado);

        // Act
        Optional<Estado> encontrado = estadoRepository.findById(guardado.getIdEstado());

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Inactivo", encontrado.get().getNombreEstado());
    }

    @Test
    @DisplayName("Debe listar todos los estados existentes")
    void listar_DebeRetornarListaDeEstados() {
        // Arrange
        estadoRepository.save(new Estado(null, "Pendiente", "General"));
        estadoRepository.save(new Estado(null, "Completado", "General"));

        // Act
        List<Estado> estados = estadoRepository.findAll();

        // Assert
        assertNotNull(estados);
        assertTrue(estados.size() >= 2);
    }

    @Test
    @DisplayName("Debe actualizar el nombre de un estado existente")
    void actualizar_DebeModificarEstado() {
        // Arrange
        Estado estado = new Estado(null, "Borrador", "General");
        Estado guardado = estadoRepository.save(estado);

        // Act
        guardado.setNombreEstado("Borrador Final");
        Estado actualizado = estadoRepository.save(guardado);

        // Assert
        assertEquals("Borrador Final", actualizado.getNombreEstado());
    }

    @Test
    @DisplayName("Debe eliminar un estado por su ID")
    void eliminar_DebeRemoverEstado() {
        // Arrange
        Estado estado = new Estado(null, "Temporal", "General");
        Estado guardado = estadoRepository.save(estado);
        Integer id = guardado.getIdEstado();

        // Act
        estadoRepository.deleteById(id);
        Optional<Estado> eliminado = estadoRepository.findById(id);

        // Assert
        assertFalse(eliminado.isPresent());
    }
}
