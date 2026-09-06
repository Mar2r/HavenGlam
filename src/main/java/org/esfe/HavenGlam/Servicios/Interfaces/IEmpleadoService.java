package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Empleado;

import java.util.List;
import java.util.Optional;

public interface IEmpleadoService {
    List<Empleado> listar();

    Optional<Empleado> buscarPorId(Integer id);

    Optional<Empleado> buscarPorPersona(Integer idPersona);

    Empleado guardar(Empleado empleado);

    Empleado cambiarEstado(Integer id, Integer idEstado);

    void eliminar(Integer id);

    boolean existePorId(Integer id);

    boolean existePorPersona(Integer idPersona);
}