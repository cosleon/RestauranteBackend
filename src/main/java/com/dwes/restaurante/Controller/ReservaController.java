package com.dwes.restaurante.Controller;

import com.dwes.restaurante.DTO.ReservaDTO;
import com.dwes.restaurante.DTO.UserRegisterDTO;
import com.dwes.restaurante.Entity.Cliente;
import com.dwes.restaurante.Entity.Mesa;
import com.dwes.restaurante.Entity.Reserva;
import com.dwes.restaurante.Entity.UserEntity;
import com.dwes.restaurante.Repository.ClienteRepository;
import com.dwes.restaurante.Repository.ReservaRepository;
import com.dwes.restaurante.Repository.UserEntityRepository;
import com.dwes.restaurante.Service.MesaService;
import com.dwes.restaurante.Service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/reservas")
public class ReservaController {
     @Autowired
     private ReservaService reservaService;
     @Autowired
     private ReservaRepository reservaRepository;
    @Autowired
    private UserEntityRepository userRepository;
    @Autowired
    private ClienteRepository clienteRepository;


    /** Listar todas las reservas */
    @GetMapping
    public ResponseEntity<List<Reserva>> getAllReservas() {
        return ResponseEntity.ok(reservaRepository.findAll());
    }

    /** Crear una nueva reserva verificando disponibilidad */
    @PostMapping
    public ResponseEntity<?> createReserva(@RequestBody Reserva reserva, Authentication authentication) {
        String username = authentication.getName();  // O también puedes usar authentication.getPrincipal() dependiendo de tu configuración
        System.out.println(username);
        // Buscar el UserEntity en la base de datos por el username (o email)
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Si no se encuentra el usuario
        }

        Long userId = user.getId();

        Cliente cliente = clienteRepository.findClienteByUserId(userId);

        // Verificar si la mesa ya está reservada en ese horario


        // Guardar la reserva si está disponible
        reserva.setCliente(cliente);
        Reserva nuevaReserva = reservaRepository.save(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<Reserva>> getMisReservas(Authentication authentication) {
        String username = authentication.getName();  // Obtener el nombre de usuario desde el JWT
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Cliente cliente = clienteRepository.findClienteByUserId(user.getId());
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Reserva> reservas = reservaRepository.findByCliente(cliente);
        return ResponseEntity.ok(reservas);
    }


    /** Obtener reservas de un día específico en formato DTO */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<ReservaDTO>> getReservasPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<ReservaDTO> reservas = reservaService.getReservasPorDia(fecha);
        return ResponseEntity.ok(reservas);
    }



    /**borrar*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();  // Obtener el nombre de usuario desde el JWT
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Cliente cliente = clienteRepository.findClienteByUserId(user.getId());
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Reserva reserva = reservaRepository.findById(id).orElse(null);;
        if(reserva.getCliente().getId()==cliente.getId()){
            reservaRepository.delete(reserva);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.noContent().build();
    }
}
