package org.esfe.HavenGlam.Modelos;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table (name = "Persona")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdPersona")
    private  Integer idPersona;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "el nombre de la persona no puede superar los 50 caracteres")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "El nombre solo puede contener letras")
    @Column(name = "Nombre", length = 50, nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "El apellido solo puede contener letras")
    @Column(name = "Apellido", length = 50, nullable = false)
    private String apellido;

    @NotBlank(message = "El telefono es obligatorio")
    @Size (max = 50, message = "El telefono no puede seperar los 8 digitos")
    @Pattern(regexp = "^[0-8]{8,8}$", message = "El teléfono solo puede contener números")
    @Column(name = "Telefono", length = 50, nullable = false)
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 50, message = "La dirección no puede superar los 50 caracteres")
    @Column(name = "Direccion", length = 50, nullable = false)
    private String direccion;

    @Size(max = 50, message = "El DUI no puede superar los 50 caracteres")
    @Pattern(regexp = "^$|^[0-9]{8}-[0-9]$", message = "El DUI debe tener el formato 00000000-0")
    @Column(name = "DUI", length = 50, nullable = true)
    private String dui;

    public Persona()
    {

    }
    public Persona(Integer idPersona, String nombre, String apellido, String telefono, String direccion, String dui) {
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.direccion = direccion;
        this.dui = dui;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }
}
