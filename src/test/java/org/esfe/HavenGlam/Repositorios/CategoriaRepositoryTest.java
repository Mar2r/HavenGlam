package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
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
public class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    private Estado estadoActivo;

    @BeforeEach
    void setUp() {
        // Se guarda un Estado base ya que Categoria tiene una relación obligatoria (@NotNull) con Estado
        Estado estado = new Estado(null, "Activo", "General");
        estadoActivo = estadoRepository.save(estado);
    }

    @Test
    @DisplayName("Debe guardar una categoría correctamente y generar su ID")
    void guardar_DebePersistirCategoria() {
        // Arrange
        Categoria categoria = new Categoria(null, "Maquillaje", estadoActivo);

        // Act
        Categoria guardada = categoriaRepository.save(categoria);

        // Assert
        assertNotNull(guardada);
        assertNotNull(guardada.getIdCategoria());
        assertEquals("Maquillaje", guardada.getNombreCategoria());
        assertEquals("Activo", guardada.getEstado().getNombreEstado());
    }

    @Test
    @DisplayName("Debe buscar una categoría por su ID")
    void buscarPorId_DebeRetornarCategoria() {
        // Arrange
        Categoria categoria = new Categoria(null, "Cuidado Capilar", estadoActivo);
        Categoria guardada = categoriaRepository.save(categoria);

        // Act
        Optional<Categoria> encontrada = categoriaRepository.findById(guardada.getIdCategoria());

        // Assert
        assertTrue(encontrada.isPresent());
        assertEquals("Cuidado Capilar", encontrada.get().getNombreCategoria());
    }

    @Test
    @DisplayName("Debe listar todas las categorías existentes")
    void listar_DebeRetornarListaDeCategorias() {
        // Arrange
        categoriaRepository.save(new Categoria(null, "Manicura", estadoActivo));
        categoriaRepository.save(new Categoria(null, "Pedicura", estadoActivo));

        // Act
        List<Categoria> categorias = categoriaRepository.findAll();

        // Assert
        assertNotNull(categorias);
        assertTrue(categorias.size() >= 2);
    }

    @Test
    @DisplayName("Debe actualizar el nombre de una categoría existente")
    void actualizar_DebeModificarCategoria() {
        // Arrange
        Categoria categoria = new Categoria(null, "Perfumeria", estadoActivo);
        Categoria guardada = categoriaRepository.save(categoria);

        // Act
        guardada.setNombreCategoria("Perfumería y Fragancias");
        Categoria actualizada = categoriaRepository.save(guardada);

        // Assert
        assertEquals("Perfumería y Fragancias", actualizada.getNombreCategoria());
    }

    @Test
    @DisplayName("Debe eliminar una categoría por su ID")
    void eliminar_DebeRemoverCategoria() {
        // Arrange
        Categoria categoria = new Categoria(null, "Spa y Masajes", estadoActivo);
        Categoria guardada = categoriaRepository.save(categoria);
        Integer id = guardada.getIdCategoria();

        // Act
        categoriaRepository.deleteById(id);
        Optional<Categoria> eliminada = categoriaRepository.findById(id);

        // Assert
        assertFalse(eliminada.isPresent());
    }
}
