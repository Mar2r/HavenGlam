package org.esfe.HavenGlam.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "Cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdCita")
    private Integer idCita;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdCliente", nullable = false)
    private Cliente cliente;

    @NotNull(message = "El empleado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdEmpleado", nullable = false)
    private Empleado empleado;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "Fecha", nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Column(name = "Hora", nullable = false)
    private LocalTime hora;

    @NotNull(message = "La hora de finalización es obligatoria")
    @Column(name = "HoraFin", nullable = false)
    private LocalTime horaFin;

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

    @NotBlank(message = "Las observaciones son obligatorias")
    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    @Column(name = "Observaciones", length = 500, nullable = false)
    private String observaciones;

    @Column(name = "FechaCreacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Cita() {
    }

    public Cita(Integer idCita, Cliente cliente, Empleado empleado, LocalDate fecha, LocalTime hora,
                LocalTime horaFin, Estado estado, String observaciones, LocalDateTime fechaCreacion) {
        this.idCita = idCita;
        this.cliente = cliente;
        this.empleado = empleado;
        this.fecha = fecha;
        this.hora = hora;
        this.horaFin = horaFin;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}