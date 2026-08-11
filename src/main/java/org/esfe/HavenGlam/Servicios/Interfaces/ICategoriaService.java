package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Categoria;

import java.util.List;
import java.util.Optional;

public interface ICategoriaService {
    List<Categoria> listar();

    Optional<Categoria> buscarPorId(Integer id);

    Categoria guardar(Categoria categoria);

    void eliminar(Integer id);

    boolean existePorId(Integer id);
}
