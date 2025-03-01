package com.dwes.restaurante.Service;

import com.dwes.restaurante.Entity.Mesa;
import com.dwes.restaurante.Repository.MesaRepository;
import com.dwes.restaurante.Repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MesaService {
    @Autowired
    private MesaRepository mesaRepository;

    private final ReservaRepository reservaRepository;

    public MesaService(MesaRepository mesaRepository, ReservaRepository reservaRepository) {
        this.mesaRepository = mesaRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Mesa> obtenerMesasDisponibles(LocalDateTime fecha) {
        return mesaRepository.findMesasDisponibles(fecha);
    }
}
