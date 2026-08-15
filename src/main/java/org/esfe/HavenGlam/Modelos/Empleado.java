package org.esfe.HavenGlam.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdEmpleado")
    private Integer idEmpleado;

    @NotNull(message = "La persona es obligatoria")
    @OneToOne
    @JoinColumn(name = "IdPersona", nullable = false, unique = true)
    private Persona persona;

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

    public Empleado() {
    }

    public Empleado(Integer idEmpleado, Persona persona, Estado estado) {
        this.idEmpleado = idEmpleado;
        this.persona = persona;
        this.estado = estado;
    }

    public Integer getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}