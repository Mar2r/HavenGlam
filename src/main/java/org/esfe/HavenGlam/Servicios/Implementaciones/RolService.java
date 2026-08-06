package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Rol;
import org.esfe.HavenGlam.Repositorios.RolRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService implements IRolService
{
    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> buscarPorId(Integer id){
        return rolRepository.findById(id);
    }

    @Override
    public Rol guardar(Rol rol){
        return rolRepository.save(rol);
    }

    @Override
    public void eliminar(Integer id){
        rolRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id){
        return rolRepository.existsById(id);
    }
}
