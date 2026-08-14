package org.esfe.HavenGlam.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

@Entity
@Table(name = "CitaServicio")
public class CitaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdCitaServicio")
    private Integer idCitaServicio;

    @NotNull(message = "La cita es obligatoria")
    @ManyToOne
    @JoinColumn(name = "IdCita", nullable = false)
    private Cita cita;

    @NotNull(message = "El servicio es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdServicio", nullable = false)
    private Servicio servicio;

    @NotNull(message = "El precio al momento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
    @Column(name = "PrecioAlMomento", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioAlMomento;

    public CitaServicio() {
    }

    public CitaServicio(Integer idCitaServicio, Cita cita, Servicio servicio, BigDecimal precioAlMomento) {
        this.idCitaServicio = idCitaServicio;
        this.cita = cita;
        this.servicio = servicio;
        this.precioAlMomento = precioAlMomento;
    }

    public Integer getIdCitaServicio() {
        return idCitaServicio;
    }

    public void setIdCitaServicio(Integer idCitaServicio) {
        this.idCitaServicio = idCitaServicio;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public BigDecimal getPrecioAlMomento() {
        return precioAlMomento;
    }

    public void setPrecioAlMomento(BigDecimal precioAlMomento) {
        this.precioAlMomento = precioAlMomento;
    }
}