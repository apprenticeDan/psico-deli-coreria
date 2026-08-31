package com.psicodeli.core.infraestructura.web;

import com.psicodeli.core.aplicacion.usuario.AutenticacionService;
import com.psicodeli.core.aplicacion.usuario.GestionTrabajadorService;
import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.usuario.HorarioAsignado;
import com.psicodeli.core.dominio.usuario.Trabajador;
import com.psicodeli.core.infraestructura.web.dto.LoginRequest;
import com.psicodeli.core.infraestructura.web.dto.LoginResponse;
import com.psicodeli.core.infraestructura.web.dto.TrabajadorRequest;
import com.psicodeli.core.infraestructura.web.dto.TrabajadorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.psicodeli.core.dominio.usuario.EstadoTrabajador;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final AutenticacionService authService;
    private final GestionTrabajadorService gestionService;

    public UsuarioController(AutenticacionService authService, GestionTrabajadorService gestionService) {
        this.authService = authService;
        this.gestionService = gestionService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Result<String, ErrorDominio> result = authService.login(request.usuario(), request.password());
        
        if (result.isOk()) {
            return ResponseEntity.ok(new LoginResponse(result.getValue()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result.getError().getClass().getSimpleName());
        }
    }

    @GetMapping("/trabajadores")
    public ResponseEntity<List<TrabajadorResponse>> listarTrabajadores() {
        List<TrabajadorResponse> response = gestionService.listarTrabajadores().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trabajadores")
    public ResponseEntity<?> crearTrabajador(@RequestBody TrabajadorRequest request) {
        Optional<HorarioAsignado> horario = Optional.empty();
        if (request.horarioDias() != null && !request.horarioDias().isEmpty() &&
            request.horarioHoraInicio() != null && request.horarioHoraFin() != null) {
            horario = Optional.of(new HorarioAsignado(request.horarioDias(), request.horarioHoraInicio(), request.horarioHoraFin()));
        }

        String ci = request.cedulaIdentidad() != null ? request.cedulaIdentidad() : "1234567";
        String tel = request.telefono() != null ? request.telefono() : "70000000";

        Result<Trabajador, ErrorDominio> result = gestionService.registrarTrabajador(
                request.nombreCompleto(),
                ci,
                tel,
                request.usuario(),
                request.password(),
                request.rol(),
                horario
        );

        if (result.isOk()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result.getValue()));
        } else {
            return ResponseEntity.badRequest().body(result.getError().getClass().getSimpleName());
        }
    }

    @PatchMapping("/trabajadores/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        String estadoStr = request.get("estado");
        if (estadoStr == null || estadoStr.isBlank()) {
            return ResponseEntity.badRequest().body("El campo 'estado' es requerido");
        }

        try {
            EstadoTrabajador nuevoEstado = EstadoTrabajador.valueOf(estadoStr.toUpperCase());
            Result<Trabajador, ErrorDominio> result = gestionService.cambiarEstado(id, nuevoEstado);

            if (result.isOk()) {
                return ResponseEntity.ok(toResponse(result.getValue()));
            } else {
                return ResponseEntity.badRequest().body(result.getError().getClass().getSimpleName());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Estado inválido. Usar ACTIVO o INACTIVO");
        }
    }

    @PutMapping("/trabajadores/{id}/password")
    public ResponseEntity<?> cambiarPassword(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        String nuevaPassword = request.get("password");
        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            return ResponseEntity.badRequest().body("La contraseña no puede estar vacía");
        }

        Result<Trabajador, ErrorDominio> result = gestionService.cambiarPassword(id, nuevaPassword.trim());

        if (result.isOk()) {
            return ResponseEntity.ok(toResponse(result.getValue()));
        } else {
            return ResponseEntity.badRequest().body(result.getError().getClass().getSimpleName());
        }
    }

    private TrabajadorResponse toResponse(Trabajador t) {
        return new TrabajadorResponse(
                t.id(),
                t.nombreCompleto(),
                t.cedulaIdentidad(),
                t.telefono(),
                t.credencial().usuario(),
                t.rol(),
                t.estado()
        );
    }
}
