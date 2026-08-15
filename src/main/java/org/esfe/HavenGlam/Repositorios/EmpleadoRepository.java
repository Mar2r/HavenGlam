package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    Optional<Empleado> findByPersona_IdPersona(Integer idPersona);

    boolean existsByPersona_IdPersona(Integer idPersona);
}