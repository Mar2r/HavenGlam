package org.esfe.HavenGlam.Repositorios;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PersonaRepository  extends  JpaRepository<Persona, Integer>{
}
