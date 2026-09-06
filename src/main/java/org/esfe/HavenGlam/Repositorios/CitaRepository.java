package org.esfe.HavenGlam.Repositorios;

import org.esfe.HavenGlam.Modelos.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


public interface CitaRepository extends JpaRepository<Cita, Integer> {

    @Query("SELECT c FROM Cita c WHERE c.fecha = :fecha AND c.estado.nombreEstado <> 'Cancelada'")
    List<Cita> findActivasPorFecha(@Param("fecha") LocalDate fecha);
}