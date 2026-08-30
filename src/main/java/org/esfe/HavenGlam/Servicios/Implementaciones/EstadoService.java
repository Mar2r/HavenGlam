package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Repositorios.EstadoRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoService implements IEstadoService
{

    @Autowired
    private EstadoRepository estadoRepository;

    @Override
    public List<Estado> listar() {
        return estadoRepository.findAll();
    }

    @Override
    public List<Estado> listarPorTipo(String tipoEstado) {
        return estadoRepository.findByTipoEstado(tipoEstado);
    }

    @Override
    public Optional<Estado> buscarPorId(Integer id){
        return  estadoRepository.findById(id);
    }
   @Override
    public Estado guardar(Estado estado){
        boolean esEdicion = estado.getIdEstado() != null
                && estadoRepository.existsById(estado.getIdEstado());
        if (esEdicion){
            return estadoRepository.save(estado);
        }
        return estadoRepository.save(estado);
   }
   @Override
    public void eliminar(Integer id){
        estadoRepository.deleteById(id);
   }
   @Override
    public boolean existePorId(Integer id){
        return estadoRepository.existsById(id);
   }
}

