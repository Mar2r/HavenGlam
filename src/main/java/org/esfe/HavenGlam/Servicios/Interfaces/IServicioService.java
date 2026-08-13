package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Servicio;

import java.util.List;
import java.util.Optional;

public interface IServicioService {
    List<Servicio> listar();

    Optional<Servicio> buscarPorId(Integer id);

    Servicio guardar(Servicio servicio);

    void eliminar(Integer id);

    boolean existePorId(Integer id);
}