package org.esfe.HavenGlam.Repositorios;


import org.esfe.HavenGlam.Modelos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByEstado_NombreEstadoIgnoreCase(String nombreEstado);

}
