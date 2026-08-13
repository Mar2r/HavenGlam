package org.esfe.HavenGlam.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

@Entity
@Table(name = "Producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdProducto")
    private Integer idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 50, message = "El nombre del producto no puede superar los 50 caracteres")
    @Column(name = "NombreProducto", length = 50, nullable = false)
    private String nombreProducto;

    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    @Column(name = "Descripcion", length = 200, nullable = true)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    @Column(name = "Precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "Stock", nullable = false)
    private Integer stock;

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

    public Producto() {
    }

    public Producto(Integer idProducto, String nombreProducto, String descripcion, BigDecimal precio,
                    Integer stock, Categoria categoria, Estado estado, String imagenUrl) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
    }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}