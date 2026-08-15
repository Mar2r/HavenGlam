package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Cliente;

import java.util.List;
import java.util.Optional;

public interface IClienteService {
    List<Cliente> listar();

    Optional<Cliente> buscarPorId(Integer id);

    Optional<Cliente> buscarPorPersona(Integer idPersona);

    Cliente guardar(Cliente cliente);

    void eliminar(Integer id);

    boolean existePorId(Integer id);

    boolean existePorPersona(Integer idPersona);
}