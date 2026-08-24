package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ServicioRepositoryTest {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    private Estado estadoActivo;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        // Se guardan Estado y Categoria base ya que Servicio tiene relaciones obligatorias (@NotNull) con ambos
        Estado estado = new Estado(null, "Activo", "General");
        estadoActivo = estadoRepository.save(estado);

        Categoria cat = new Categoria(null, "Cabello", estadoActivo);
        categoria = categoriaRepository.save(cat);
    }

    @Test
    @DisplayName("Debe guardar un servicio correctamente y generar su ID")
    void guardar_DebePersistirServicio() {
        Servicio servicio = new Servicio(null, "Corte de Cabello", "Corte clásico",
                new BigDecimal("15.00"), LocalTime.of(0, 30), categoria, estadoActivo, null);

        Servicio guardado = servicioRepository.save(servicio);

        assertNotNull(guardado);
        assertNotNull(guardado.getIdServicio());
        assertEquals("Corte de Cabello", guardado.getNombreServicio());
        assertEquals("Cabello", guardado.getCategoria().getNombreCategoria());
    }

    @Test
    @DisplayName("Debe buscar un servicio por su ID")
    void buscarPorId_DebeRetornarServicio() {
        Servicio servicio = new Servicio(null, "Tinte", "Tinte completo",
                new BigDecimal("35.00"), LocalTime.of(1, 30), categoria, estadoActivo, null);
        Servicio guardado = servicioRepository.save(servicio);

        Optional<Servicio> encontrado = servicioRepository.findById(guardado.getIdServicio());

        assertTrue(encontrado.isPresent());
        assertEquals("Tinte", encontrado.get().getNombreServicio());
    }

    @Test
    @DisplayName("Debe listar todos los servicios existentes")
    void listar_DebeRetornarListaDeServicios() {
        servicioRepository.save(new Servicio(null, "Manicure", "Manicure básico",
                new BigDecimal("10.00"), LocalTime.of(0, 20), categoria, estadoActivo, null));
        servicioRepository.save(new Servicio(null, "Pedicure", "Pedicure básico",
                new BigDecimal("12.00"), LocalTime.of(0, 25), categoria, estadoActivo, null));

        List<Servicio> servicios = servicioRepository.findAll();

        assertNotNull(servicios);
        assertTrue(servicios.size() >= 2);
    }

    @Test
    @DisplayName("Debe actualizar el precio de un servicio existente")
    void actualizar_DebeModificarServicio() {
        Servicio servicio = new Servicio(null, "Peinado", "Peinado de fiesta",
                new BigDecimal("20.00"), LocalTime.of(0, 45), categoria, estadoActivo, null);
        Servicio guardado = servicioRepository.save(servicio);

        guardado.setPrecio(new BigDecimal("25.00"));
        Servicio actualizado = servicioRepository.save(guardado);

        assertEquals(0, new BigDecimal("25.00").compareTo(actualizado.getPrecio()));
    }

    @Test
    @DisplayName("Debe eliminar un servicio por su ID")
    void eliminar_DebeRemoverServicio() {
        Servicio servicio = new Servicio(null, "Depilación", "Depilación facial",
                new BigDecimal("8.00"), LocalTime.of(0, 15), categoria, estadoActivo, null);
        Servicio guardado = servicioRepository.save(servicio);
        Integer id = guardado.getIdServicio();

        servicioRepository.deleteById(id);
        Optional<Servicio> eliminado = servicioRepository.findById(id);

        assertFalse(eliminado.isPresent());
    }
}