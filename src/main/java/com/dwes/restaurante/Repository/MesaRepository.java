package com.dwes.restaurante.Repository;

import com.dwes.restaurante.Entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    @Query("SELECT m FROM Mesa m WHERE m.id NOT IN (SELECT r.mesa.id FROM Reserva r WHERE r.fecha = :fecha)")
    List<Mesa> findMesasDisponibles(@Param("fecha") LocalDateTime fecha);


}
