package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Repositorios.ServicioRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioService implements IServicioService {
    @Autowired
    private ServicioRepository servicioRepository;

    @Override
    public List<Servicio> listar() {
        return servicioRepository.findAll();
    }

    @Override
    public Optional<Servicio> buscarPorId(Integer id){
        return servicioRepository.findById(id);
    }

    @Override
    public Servicio guardar(Servicio servicio){
        return servicioRepository.save(servicio);
    }

    @Override
    public void eliminar(Integer id){
        servicioRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id){
        return servicioRepository.existsById(id);
    }
}