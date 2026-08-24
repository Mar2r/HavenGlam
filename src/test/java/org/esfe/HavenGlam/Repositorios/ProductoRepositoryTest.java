package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    private Estado estadoActivo;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        // Se guardan Estado y Categoria base ya que Producto tiene relaciones obligatorias (@NotNull) con ambos
        Estado estado = new Estado(null, "Activo", "General");
        estadoActivo = estadoRepository.save(estado);

        Categoria cat = new Categoria(null, "Cuidado de la Piel", estadoActivo);
        categoria = categoriaRepository.save(cat);
    }

    @Test
    @DisplayName("Debe guardar un producto correctamente y generar su ID")
    void guardar_DebePersistirProducto() {
        Producto producto = new Producto(null, "Crema Hidratante", "Crema facial",
                new BigDecimal("18.50"), 20, categoria, estadoActivo, null);

        Producto guardado = productoRepository.save(producto);

        assertNotNull(guardado);
        assertNotNull(guardado.getIdProducto());
        assertEquals("Crema Hidratante", guardado.getNombreProducto());
        assertEquals("Cuidado de la Piel", guardado.getCategoria().getNombreCategoria());
    }

    @Test
    @DisplayName("Debe buscar un producto por su ID")
    void buscarPorId_DebeRetornarProducto() {
        Producto producto = new Producto(null, "Protector Solar", "Protector SPF 50",
                new BigDecimal("22.00"), 15, categoria, estadoActivo, null);
        Producto guardado = productoRepository.save(producto);

        Optional<Producto> encontrado = productoRepository.findById(guardado.getIdProducto());

        assertTrue(encontrado.isPresent());
        assertEquals("Protector Solar", encontrado.get().getNombreProducto());
    }

    @Test
    @DisplayName("Debe listar todos los productos existentes")
    void listar_DebeRetornarListaDeProductos() {
        productoRepository.save(new Producto(null, "Shampoo", "Shampoo hidratante",
                new BigDecimal("12.00"), 30, categoria, estadoActivo, null));
        productoRepository.save(new Producto(null, "Acondicionador", "Acondicionador hidratante",
                new BigDecimal("13.00"), 25, categoria, estadoActivo, null));

        List<Producto> productos = productoRepository.findAll();

        assertNotNull(productos);
        assertTrue(productos.size() >= 2);
    }

    @Test
    @DisplayName("Debe actualizar el stock de un producto existente")
    void actualizar_DebeModificarProducto() {
        Producto producto = new Producto(null, "Mascarilla Facial", "Mascarilla de arcilla",
                new BigDecimal("9.00"), 40, categoria, estadoActivo, null);
        Producto guardado = productoRepository.save(producto);

        guardado.setStock(35);
        Producto actualizado = productoRepository.save(guardado);

        assertEquals(35, actualizado.getStock());
    }

    @Test
    @DisplayName("Debe eliminar un producto por su ID")
    void eliminar_DebeRemoverProducto() {
        Producto producto = new Producto(null, "Serum Facial", "Serum con vitamina C",
                new BigDecimal("28.00"), 10, categoria, estadoActivo, null);
        Producto guardado = productoRepository.save(producto);
        Integer id = guardado.getIdProducto();

        productoRepository.deleteById(id);
        Optional<Producto> eliminado = productoRepository.findById(id);

        assertFalse(eliminado.isPresent());
    }
}