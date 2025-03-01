package com.dwes.restaurante.Controller;

import com.dwes.restaurante.DTO.LoginRequestDTO;
import com.dwes.restaurante.DTO.LoginResponseDTO;
import com.dwes.restaurante.DTO.UserRegisterDTO;
import com.dwes.restaurante.Entity.Cliente;
import com.dwes.restaurante.Repository.ClienteRepository;
import com.dwes.restaurante.config.JwtTokenProvider;
import com.dwes.restaurante.Entity.UserEntity;
import com.dwes.restaurante.Repository.UserEntityRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints de autenticación")
public class AuthController {
    @Autowired
    private UserEntityRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/register")
    public ResponseEntity<UserEntity> save(@RequestBody UserRegisterDTO userDTO) {
        // Crear usuario
        UserEntity userEntity = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .authorities(List.of("ROLE_USER"))
                .build();

        // Crear cliente y asociarlo con el usuario
        Cliente cliente = Cliente.builder()
                .nombre(userDTO.getUsername())
                .email(userDTO.getEmail())
                .telefono("123456789") // Prueba con un valor fijo para verificar
                .user(userEntity) // Relación con el usuario
                .build();

        // Asignar cliente a usuario
        userEntity.setCliente(cliente);

        // Guardar en el orden correcto
        userRepository.save(userEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(userEntity);
    }


    @PostMapping("/secretButNotExtremlySecret/register")
    public ResponseEntity<UserEntity> saveAdmin(@RequestBody UserRegisterDTO userDTO) {
        UserEntity userEntity = this.userRepository.save(
                UserEntity.builder()
                        .username(userDTO.getUsername())
                        .password(passwordEncoder.encode(userDTO.getPassword()))
                        .email(userDTO.getEmail())
                        .authorities(List.of("ROLE_ADMIN"))
                        .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(userEntity);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        try {
            System.out.println("Intentando login con usuario: " + loginDTO.getUsername());
            System.out.println("Contraseña ingresada: " + loginDTO.getPassword());

            //Validamos al usuario en Spring (hacemos login manualmente)
            UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            Authentication auth = authenticationManager.authenticate(userPassAuthToken);    //valida el usuario y devuelve un objeto Authentication con sus datos
            //Obtenemos el UserEntity del usuario logueado
            UserEntity user = (UserEntity) auth.getPrincipal();

            //Generamos un token con los datos del usuario (la clase tokenProvider ha hemos creado nosotros para no poner aquí todo el código
            String token = this.tokenProvider.generateToken(auth);

            //Devolvemos un código 200 con el username y token JWT
            return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), token));
        }catch (Exception e) {  //Si el usuario no es válido, salta una excepción BadCredentialsException
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "path", "/auth/login",
                            "message", "Credenciales erróneas",
                            "timestamp", new Date()
                    )
            );
        }
    }
}
