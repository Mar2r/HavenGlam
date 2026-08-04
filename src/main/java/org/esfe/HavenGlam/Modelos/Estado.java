package org.esfe.HavenGlam.Modelos;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Entity
@Table(name = "Estado")

public class Estado
{
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdEstado")
    private Integer idEstado;

   @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre del estado no puede superar los 10 caracteres")
    @Column(name = "NombreEstado", length =50, nullable = false)
    private String nombreEstado;

   @NotBlank(message = "El tipo de estado es obligatorio")
    @Size(max =50, message = "El tipo de estado no puede superar los 50 caracteres")
    @Column(name = "TipoEstado", length = 50, nullable = false)
    private String tipoEstado;
   public  Estado() {

   }

    public Estado(Integer idEstado, String nombreEstado, String tipoEstado) {
        this.idEstado = idEstado;
        this.nombreEstado = nombreEstado;
        this.tipoEstado = tipoEstado;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getTipoEstado() {
        return tipoEstado;
    }

    public void setTipoEstado(String tipoEstado) {
        this.tipoEstado = tipoEstado;
    }
}
