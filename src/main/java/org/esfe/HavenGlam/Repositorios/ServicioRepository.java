package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

}