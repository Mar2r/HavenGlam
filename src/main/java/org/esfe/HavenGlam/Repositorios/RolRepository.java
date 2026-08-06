package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RolRepository extends JpaRepository<Rol, Integer> {

}