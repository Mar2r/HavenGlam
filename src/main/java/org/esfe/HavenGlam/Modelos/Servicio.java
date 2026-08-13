package org.esfe.HavenGlam.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "Servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdServicio")
    private Integer idServicio;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 50, message = "El nombre del servicio no puede superar los 50 caracteres")
    @Column(name = "NombreServicio", length = 50, nullable = false)
    private String nombreServicio;

    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    @Column(name = "Descripcion", length = 200, nullable = true)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    @Column(name = "Precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "La duración es obligatoria")
    @Column(name = "DuracionMinutos", nullable = false)
    private LocalTime duracionMinutos;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne
    @JoinColumn(name = "IdCategoria", nullable = false)
    private Categoria categoria;

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

    @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
    @Column(name = "ImagenUrl", length = 500, nullable = true)
    private String imagenUrl;

    public Servicio() {
    }

    public Servicio(Integer idServicio, String nombreServicio, String descripcion, BigDecimal precio,
                    LocalTime duracionMinutos, Categoria categoria, Estado estado, String imagenUrl) {
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
        this.categoria = categoria;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
    }

    public Integer getIdServicio() { return idServicio; }
    public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public LocalTime getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(LocalTime duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}