package org.esfe.HavenGlam.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdUsuario")
    private Integer idUsuario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 70, message = "El correo no puede superar los 70 caracteres")
    @Column(name = "Correo", length = 70, nullable = false, unique = true)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    @Column(name = "Contra", length = 255, nullable = false)
    private String contra;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdRol", nullable = false)
    private Rol rol;

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

    @NotNull(message = "La persona es obligatoria")
    @OneToOne
    @JoinColumn(name = "IdPersona", nullable = false)
    private Persona persona;

    public Usuario() {
    }

    public Usuario(Integer idUsuario, String correo, String contra, Rol rol, Estado estado, Persona persona) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.contra = contra;
        this.rol = rol;
        this.estado = estado;
        this.persona = persona;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContra() { return contra; }
    public void setContra(String contra) { this.contra = contra; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }
}