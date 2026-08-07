package org.esfe.HavenGlam.Servicios.Interfaces;
import  org.esfe.HavenGlam.Modelos.Persona;

import java.util.List;
import java.util.Optional;

public interface IPersonaService {
    List<Persona> listar();

    Optional<Persona> buscarPorId(Integer id);

    Persona guardar(Persona persona);

    void eliminar(Integer id);

    boolean existePorId(Integer id);
}
