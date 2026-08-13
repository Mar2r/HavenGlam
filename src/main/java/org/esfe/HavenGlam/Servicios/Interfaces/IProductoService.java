package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoService {
    List<Producto> listar();

    Optional<Producto> buscarPorId(Integer id);

    Producto guardar(Producto producto);

    void eliminar(Integer id);

    boolean existePorId(Integer id);
}