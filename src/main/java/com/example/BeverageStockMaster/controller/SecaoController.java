package com.example.BeverageStockMaster.controller;
import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.repository.SecaoRepository;
import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secoes")
public class SecaoController {

    @Autowired
    SecaoRepository secaoRepository;

    @Autowired
    TipoBebidaRepository tipoBebidaRepository;


    @PostMapping("/nova")
    public ResponseEntity<String> criarSecao(@RequestBody Secao secao) {
        secao.setUtilizacaoTotal(0);

        secaoRepository.save(secao);
        return ResponseEntity.ok("Seção criada com sucesso.");
    }

    @GetMapping
    public ResponseEntity<List<Secao>> listarSecoes() {
        List<Secao> secoes = secaoRepository.findAll();
        return ResponseEntity.ok(secoes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarSecao(@PathVariable Long id) {
        try {
            secaoRepository.deleteById(id);
            return ResponseEntity.ok("Seção deletada com sucesso.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Erro: Não é possível deletar uma seção que contém bebidas cadastradas.");
        }
    }
}
