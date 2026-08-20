package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Rol;
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
public class RolRepositoryTest {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    private Estado estadoActivo;

    @BeforeEach
    void setUp() {
        // Se guarda un Estado base ya que Rol tiene una relación obligatoria (@NotNull) con Estado
        Estado estado = new Estado(null, "Activo", "General");
        estadoActivo = estadoRepository.save(estado);
    }

    @Test
    @DisplayName("Debe guardar un rol correctamente y generar su ID")
    void guardar_DebePersistirRol() {
        // Arrange
        Rol rol = new Rol(null, "Administrador", estadoActivo);

        // Act
        Rol guardado = rolRepository.save(rol);

        // Assert
        assertNotNull(guardado);
        assertNotNull(guardado.getIdRol());
        assertEquals("Administrador", guardado.getNombreRol());
        assertEquals("Activo", guardado.getEstado().getNombreEstado());
    }

    @Test
    @DisplayName("Debe buscar un rol por su ID")
    void buscarPorId_DebeRetornarRol() {
        // Arrange
        Rol rol = new Rol(null, "Empleado", estadoActivo);
        Rol guardado = rolRepository.save(rol);

        // Act
        Optional<Rol> encontrado = rolRepository.findById(guardado.getIdRol());

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Empleado", encontrado.get().getNombreRol());
    }

    @Test
    @DisplayName("Debe listar todos los roles existentes")
    void listar_DebeRetornarListaDeRoles() {
        // Arrange
        rolRepository.save(new Rol(null, "Cliente", estadoActivo));
        rolRepository.save(new Rol(null, "Recepcionista", estadoActivo));

        // Act
        List<Rol> roles = rolRepository.findAll();

        // Assert
        assertNotNull(roles);
        assertTrue(roles.size() >= 2);
    }

    @Test
    @DisplayName("Debe actualizar el nombre de un rol existente")
    void actualizar_DebeModificarRol() {
        // Arrange
        Rol rol = new Rol(null, "Supervisor", estadoActivo);
        Rol guardado = rolRepository.save(rol);

        // Act
        guardado.setNombreRol("Supervisor General");
        Rol actualizado = rolRepository.save(guardado);

        // Assert
        assertEquals("Supervisor General", actualizado.getNombreRol());
    }

    @Test
    @DisplayName("Debe eliminar un rol por su ID")
    void eliminar_DebeRemoverRol() {
        // Arrange
        Rol rol = new Rol(null, "Temporal", estadoActivo);
        Rol guardado = rolRepository.save(rol);
        Integer id = guardado.getIdRol();

        // Act
        rolRepository.deleteById(id);
        Optional<Rol> eliminado = rolRepository.findById(id);

        // Assert
        assertFalse(eliminado.isPresent());
    }
}