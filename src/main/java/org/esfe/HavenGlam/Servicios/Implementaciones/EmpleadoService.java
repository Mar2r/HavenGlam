package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Repositorios.EmpleadoRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService implements IEmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    public List<Empleado> listar() {
        return empleadoRepository.findAll();
    }

    @Override
    public Optional<Empleado> buscarPorId(Integer id) {
        return empleadoRepository.findById(id);
    }

    @Override
    public Optional<Empleado> buscarPorPersona(Integer idPersona) {
        return empleadoRepository.findByPersona_IdPersona(idPersona);
    }

    @Override
    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    @Override
    public void eliminar(Integer id) {
        empleadoRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id) {
        return empleadoRepository.existsById(id);
    }

    @Override
    public boolean existePorPersona(Integer idPersona) {
        return empleadoRepository.existsByPersona_IdPersona(idPersona);
    }
}