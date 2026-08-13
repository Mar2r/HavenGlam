package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}