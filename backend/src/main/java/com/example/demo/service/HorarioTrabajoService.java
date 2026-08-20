package com.example.demo.service;

import com.example.demo.model.HorarioTrabajo;
import com.example.demo.repository.HorarioTrabajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HorarioTrabajoService {

    @Autowired
    private HorarioTrabajoRepository horarioRepository;

    public HorarioTrabajo guardarHorario(HorarioTrabajo horario) {
        return horarioRepository.save(horario);
    }

    public List<HorarioTrabajo> listarHorarios() {
        return horarioRepository.findAll();
    }
}