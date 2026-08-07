package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Repositorios.PersonaRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService  implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public  List<Persona> listar() {return  personaRepository.findAll();}

    @Override
    public Optional<Persona> buscarPorId(Integer id){
        return personaRepository.findById(id);
    }
    @Override
    public  Persona guardar(Persona persona){
        boolean esEdicion = persona.getIdPersona() != null
                && personaRepository.existsById(persona.getIdPersona());
        if (esEdicion){
            return personaRepository.save(persona);
        }
        return  personaRepository.save(persona);
    }
    @Override
    public void eliminar(Integer id)
    {
        personaRepository.deleteById(id);
    }
    @Override
    public  boolean existePorId(Integer id)
    {
        return  personaRepository.existsById(id);
    }
}
