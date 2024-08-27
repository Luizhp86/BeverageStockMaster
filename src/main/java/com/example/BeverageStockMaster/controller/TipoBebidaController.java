package com.example.BeverageStockMaster.controller;
import com.example.BeverageStockMaster.domain.TipoBebida;
import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-bebidas")
public class TipoBebidaController {

    @Autowired
    TipoBebidaRepository tipoBebidaRepository;



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
        try {
            tipoBebidaRepository.deleteById(id);
            return ResponseEntity.ok("Tipo de bebida deletado com sucesso.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Erro: Este tipo de bebida está associado a uma ou mais seções e não pode ser deletado.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao tentar deletar o tipo de bebida.");
        }
    }
}
