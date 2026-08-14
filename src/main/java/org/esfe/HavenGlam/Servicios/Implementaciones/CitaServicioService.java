package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Repositorios.CitaServicioRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaServicioService implements ICitaServicioService {
    @Autowired
    private CitaServicioRepository citaServicioRepository;

    @Override
    public List<CitaServicio> listar() { return citaServicioRepository.findAll();}

    @Override
    public Optional<CitaServicio> buscarPorId(Integer id){ return citaServicioRepository.findById(id);}

    @Override
    public CitaServicio guardar(CitaServicio citaServicio){ return citaServicioRepository.save(citaServicio);}

    @Override
    public void eliminar(Integer id){ citaServicioRepository.deleteById(id);}

    @Override
    public boolean existePorId(Integer id){ return citaServicioRepository.existsById(id); }
}