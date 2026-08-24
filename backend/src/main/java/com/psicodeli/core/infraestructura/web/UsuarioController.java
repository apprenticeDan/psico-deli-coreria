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

        Result<Trabajador, ErrorDominio> result = gestionService.registrarTrabajador(
                request.nombreCompleto(),
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

    private TrabajadorResponse toResponse(Trabajador t) {
        return new TrabajadorResponse(
                t.id(),
                t.nombreCompleto(),
                t.credencial().usuario(),
                t.rol(),
                t.estado()
        );
    }
}
