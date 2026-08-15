package org.esfe.HavenGlam.Servicios.Interfaces;

import org.esfe.HavenGlam.Modelos.RegistroClienteForm;
import org.esfe.HavenGlam.Modelos.RegistroEmpleadoForm;
import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Modelos.Empleado;

public interface IRegistroService {

    Cliente registrarCliente(RegistroClienteForm form);

    Empleado registrarEmpleado(RegistroEmpleadoForm form);
}