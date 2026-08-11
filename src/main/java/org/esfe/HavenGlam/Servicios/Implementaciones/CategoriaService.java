package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Categoria;
import org.esfe.HavenGlam.Repositorios.CategoriaRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService implements ICategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Override
    public Optional<Categoria> buscarPorId(Integer id){
        return categoriaRepository.findById(id);
    }

    @Override
    public Categoria guardar(Categoria categoria){
        return categoriaRepository.save(categoria);
    }

    @Override
    public void eliminar(Integer id){
        categoriaRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id){
        return categoriaRepository.existsById(id);
    }
}
