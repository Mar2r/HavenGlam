package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByPersona_IdPersona(Integer idPersona);

    boolean existsByPersona_IdPersona(Integer idPersona);
}