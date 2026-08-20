package com.example.demo.controller;

import com.example.demo.model.HorarioTrabajo;
import com.example.demo.service.HorarioTrabajoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioTrabajoController {

    @Autowired
    private HorarioTrabajoService horarioService;

    @PostMapping
    public HorarioTrabajo registrarHorario(@RequestBody HorarioTrabajo horario) {
        return horarioService.guardarHorario(horario);
    }

    @GetMapping
    public List<HorarioTrabajo> listarHorarios() {
        return horarioService.listarHorarios();
    }
}