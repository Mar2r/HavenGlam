package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolService {
    List<Rol> listar();

    Optional<Rol> buscarPorId(Integer id);

    Rol guardar(Rol rol);

    void eliminar(Integer id);

    boolean existePorId(Integer id);
}