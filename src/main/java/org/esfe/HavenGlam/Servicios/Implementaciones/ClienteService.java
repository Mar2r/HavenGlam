package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Repositorios.ClienteRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements IClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> buscarPorPersona(Integer idPersona) {
        return clienteRepository.findByPersona_IdPersona(idPersona);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminar(Integer id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id) {
        return clienteRepository.existsById(id);
    }

    @Override
    public boolean existePorPersona(Integer idPersona) {
        return clienteRepository.existsByPersona_IdPersona(idPersona);
    }
}