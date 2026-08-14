package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface CitaRepository extends JpaRepository<Cita, Integer> {

}