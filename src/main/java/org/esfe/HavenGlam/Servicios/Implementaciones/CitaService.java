package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Repositorios.CitaRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaService implements ICitaService {
    @Autowired
    private CitaRepository citaRepository;

    @Override
    public List<Cita> listar() { return citaRepository.findAll(); }

    @Override
    public Optional<Cita> buscarPorId(Integer id){ return citaRepository.findById(id);}

    @Override
    public Cita guardar(Cita cita){ return citaRepository.save(cita); }

    @Override
    public void eliminar(Integer id){ citaRepository.deleteById(id); }

    @Override
    public boolean existePorId(Integer id){ return citaRepository.existsById(id); }
}