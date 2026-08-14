package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.CitaServicio;

import java.util.List;
import java.util.Optional;

public interface ICitaServicioService {

    List<CitaServicio> listar();

    Optional<CitaServicio> buscarPorId(Integer id);

    CitaServicio guardar(CitaServicio citaServicio);

    void eliminar(Integer id);

    boolean existePorId(Integer id);
}