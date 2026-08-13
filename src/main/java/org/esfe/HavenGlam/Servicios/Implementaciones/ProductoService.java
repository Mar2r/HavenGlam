package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Producto;
import org.esfe.HavenGlam.Repositorios.ProductoRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService implements IProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id){
        return productoRepository.findById(id);
    }

    @Override
    public Producto guardar(Producto producto){
        return productoRepository.save(producto);
    }

    @Override
    public void eliminar(Integer id){
        productoRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id){
        return productoRepository.existsById(id);
    }
}