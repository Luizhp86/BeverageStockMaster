package com.example.BeverageStockMaster.controller;
import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.domain.TipoBebida;
import com.example.BeverageStockMaster.repository.SecaoRepository;
import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (secao.getTipoBebida() == null || secao.getTipoBebida().getId() == null) {
            return ResponseEntity.badRequest().body("Nenhum tipo de bebida disponível. Cadastre um tipo antes de criar uma seção.");
        }

        TipoBebida tipoBebida = tipoBebidaRepository.findById(secao.getTipoBebida().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Bebida não encontrado"));

        secao.setTipoBebida(tipoBebida);
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
        secaoRepository.deleteById(id);
        return ResponseEntity.ok("Seção deletada com sucesso.");
    }
}
