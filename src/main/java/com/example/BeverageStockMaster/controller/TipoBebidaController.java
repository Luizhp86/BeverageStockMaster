package com.example.BeverageStockMaster.controller;

import com.example.BeverageStockMaster.domain.TipoBebida;
import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-bebidas")
public class TipoBebidaController {

    private final TipoBebidaRepository tipoBebidaRepository;

    public TipoBebidaController(TipoBebidaRepository tipoBebidaRepository) {
        this.tipoBebidaRepository = tipoBebidaRepository;
    }

    @PostMapping("/nova")
    public ResponseEntity<String> criarTipoBebida(@RequestBody TipoBebida tipoBebida) {
        tipoBebidaRepository.save(tipoBebida);
        return ResponseEntity.ok("Tipo de Bebida criado com sucesso.");
    }

    @GetMapping
    public ResponseEntity<List<TipoBebida>> listarTiposBebidas() {
        List<TipoBebida> tiposBebidas = tipoBebidaRepository.findAll();
        return ResponseEntity.ok(tiposBebidas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarTipoBebida(@PathVariable Long id) {
        tipoBebidaRepository.deleteById(id);
        return ResponseEntity.ok("Tipo de Bebida deletado com sucesso.");
    }
}
