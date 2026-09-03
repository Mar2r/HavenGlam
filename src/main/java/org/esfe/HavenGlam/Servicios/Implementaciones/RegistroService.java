package org.esfe.HavenGlam.Servicios.Implementaciones;

import org.esfe.HavenGlam.Modelos.RegistroClienteForm;
import org.esfe.HavenGlam.Modelos.RegistroEmpleadoForm;
import org.esfe.HavenGlam.Modelos.*;
import org.esfe.HavenGlam.Repositorios.ClienteRepository;
import org.esfe.HavenGlam.Repositorios.EmpleadoRepository;
import org.esfe.HavenGlam.Repositorios.PersonaRepository;
import org.esfe.HavenGlam.Repositorios.UsuarioRepository;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IRegistroService;
import org.esfe.HavenGlam.Servicios.Interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistroService implements IRegistroService {

    // Convención asumida de datos semilla — AJUSTAR aquí si tu BD usa otros valores.
    private static final String ROL_CLIENTE = "Cliente";
    private static final String ROL_EMPLEADO = "Empleado";
    private static final String ESTADO_ACTIVO = "Activo";

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private IRolService rolService;

    @Autowired
    private IEstadoService estadoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Cliente registrarCliente(RegistroClienteForm form) {
        validarCorreoDisponible(form.getCorreo());

        Persona persona = crearPersona(form.getNombre(), form.getApellido(),
                form.getTelefono(), form.getDireccion(), form.getDui());

        Rol rolCliente = obtenerRolPorNombre(ROL_CLIENTE);
        Estado estadoActivo = obtenerEstadoPorNombre(ESTADO_ACTIVO);

        crearUsuario(persona, form.getCorreo(), form.getContra(), rolCliente, estadoActivo);

        Cliente cliente = new Cliente();
        cliente.setPersona(persona);
        cliente.setEstado(estadoActivo);
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Empleado registrarEmpleado(RegistroEmpleadoForm form) {
        validarCorreoDisponible(form.getCorreo());

        Persona persona = crearPersona(form.getNombre(), form.getApellido(),
                form.getTelefono(), form.getDireccion(), form.getDui());

        Rol rolEmpleado = obtenerRolPorNombre(ROL_EMPLEADO);
        Estado estadoActivo = obtenerEstadoPorNombre(ESTADO_ACTIVO);

        crearUsuario(persona, form.getCorreo(), form.getContra(), rolEmpleado, estadoActivo);

        Empleado empleado = new Empleado();
        empleado.setPersona(persona);
        empleado.setEstado(estadoActivo);
        return empleadoRepository.save(empleado);
    }

    private void validarCorreoDisponible(String correo) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalStateException("Ese correo ya está registrado");
        }
    }

    private Persona crearPersona(String nombre, String apellido, String telefono,
                                 String direccion, String dui) {
        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setTelefono(telefono);
        persona.setDireccion(direccion);
        persona.setDui(dui);
        return personaRepository.save(persona); // aquí se genera el IdPersona
    }

    private void crearUsuario(Persona persona, String correo, String contra, Rol rol, Estado estado) {
        Usuario usuario = new Usuario();
        usuario.setPersona(persona); // usa el IdPersona recién generado
        usuario.setCorreo(correo);
        usuario.setContra(passwordEncoder.encode(contra)); // cifrado con BCrypt
        usuario.setRol(rol);
        usuario.setEstado(estado);
        usuarioRepository.save(usuario);
    }

    private Rol obtenerRolPorNombre(String nombreRol) {
        List<Rol> roles = rolService.listar();
        return roles.stream()
                .filter(r -> r.getNombreRol() != null
                        && r.getNombreRol().trim().equalsIgnoreCase(nombreRol))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el rol '" + nombreRol + "' configurado en el sistema"));
    }

    private Estado obtenerEstadoPorNombre(String nombreEstado) {
        List<Estado> estados = estadoService.listar();
        return estados.stream()
                .filter(e -> e.getNombreEstado() != null
                        && e.getNombreEstado().trim().equalsIgnoreCase(nombreEstado))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el estado '" + nombreEstado + "' configurado en el sistema"));
    }
}