package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Usuario;
import org.esfe.HavenGlam.Repositorios.UsuarioRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // TODO (etapa de seguridad): cifrar usuario.getContra() con BCrypt antes de guardar.
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}