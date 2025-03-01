package com.dwes.restaurante.Repository;

import com.dwes.restaurante.Entity.Cliente;
import com.dwes.restaurante.Entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByMesaIdAndFecha(Long mesaId, LocalDateTime fecha);

    @Query("SELECT r FROM Reserva r WHERE r.fecha BETWEEN :inicio AND :fin")
    List<Reserva> findByFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<Reserva> findByCliente(Cliente cliente);


}
