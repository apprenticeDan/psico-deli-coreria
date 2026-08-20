package com.example.demo.controller;

import com.example.demo.model.Combo;
import com.example.demo.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/combos")
public class ComboController {

    @Autowired
    private ComboService comboService;

    @PostMapping
    public Combo crearCombo(@RequestBody Combo combo) {
        return comboService.guardarCombo(combo);
    }

    @GetMapping
    public List<Combo> listarCombos() {
        return comboService.listarCombos();
    }

    @DeleteMapping("/{id}")
    public void eliminarCombo(@PathVariable Long id) {
        comboService.eliminarCombo(id);
    }
}