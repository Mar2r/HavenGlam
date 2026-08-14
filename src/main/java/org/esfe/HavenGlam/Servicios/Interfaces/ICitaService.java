package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Cita;

import java.util.List;
import java.util.Optional;

public interface ICitaService {
    List<Cita> listar();

    Optional<Cita> buscarPorId(Integer id);

    Cita guardar(Cita cita);

    void eliminar(Integer id);

    boolean existePorId(Integer id);
}