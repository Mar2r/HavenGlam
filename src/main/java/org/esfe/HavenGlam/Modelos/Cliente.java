package org.esfe.HavenGlam.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdCliente")
    private Integer idCliente;

    @NotNull(message = "La persona es obligatoria")
    @OneToOne
    @JoinColumn(name = "IdPersona", nullable = false, unique = true)
    private Persona persona;

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

    public Cliente() {
    }

    public Cliente(Integer idCliente, Persona persona, Estado estado) {
        this.idCliente = idCliente;
        this.persona = persona;
        this.estado = estado;
    }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}