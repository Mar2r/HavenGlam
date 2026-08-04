package org.esfe.HavenGlam.Repositorios;
import org.esfe.HavenGlam.Modelos.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
public interface EstadoRepository extends JpaRepository<Estado, Integer> {

}
