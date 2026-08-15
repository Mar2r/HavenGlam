package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    List<Usuario> listar();

    Optional<Usuario> buscarPorId(Integer id);

    Optional<Usuario> buscarPorCorreo(String correo);

    Usuario guardar(Usuario usuario);

    void eliminar(Integer id);

    boolean existePorId(Integer id);

    boolean existePorCorreo(String correo);
}