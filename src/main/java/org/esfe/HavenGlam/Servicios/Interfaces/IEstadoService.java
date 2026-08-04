package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Estado;

import java.util.List;
import java.util.Optional;

public interface IEstadoService {

    List<Estado> listar();

    Optional<Estado> buscarPorId(Integer id);

    Estado guardar(Estado estado);

    void eliminar(Integer id);

    boolean existePorId(Integer id);
}