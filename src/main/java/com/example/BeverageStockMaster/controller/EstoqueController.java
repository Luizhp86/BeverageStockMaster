package com.example.BeverageStockMaster.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.BeverageStockMaster.domain.Bebida;
import com.example.BeverageStockMaster.domain.Bebida.TipoBebida;
import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.service.EstoqueService;

import java.util.List;
@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/entrada")
    public ResponseEntity<?> registrarEntrada(@RequestBody Bebida bebida, @RequestParam Long secaoId, @RequestParam String responsavel) {
        if (secaoId <= 0) {
            return ResponseEntity.badRequest().body("O número da seção deve ser positivo.");
        }

        try {
            estoqueService.registrarEntradaBebida(bebida, secaoId, responsavel);
            return ResponseEntity.ok("Bebida registrada com sucesso na seção " + secaoId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/saida")
    public ResponseEntity<?> registrarSaida(@RequestBody Bebida bebida, @RequestParam Long secaoId, @RequestParam String responsavel) {
        if (bebida.getVolume() <= 0) {
            return ResponseEntity.badRequest().body("O volume deve ser maior que zero.");
        }

        try {
            estoqueService.registrarSaidaBebida(bebida, secaoId, responsavel);
            return ResponseEntity.ok("Bebida removida com sucesso da seção " + secaoId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/volume-total")
    public ResponseEntity<Double> consultarVolumeTotal(
            @RequestParam TipoBebida tipoBebida,
            @RequestParam(required = false) Long secaoId) {

        double volumeTotal = estoqueService.consultarVolumeTotalPorTipoESecao(tipoBebida, secaoId);
        return ResponseEntity.ok(volumeTotal);
    }

    @GetMapping("/secoes/{secaoId}/bebidas")
    public ResponseEntity<List<Bebida>> consultarBebidasPorSecao(@PathVariable Long secaoId) {
        try {
            List<Bebida> bebidas = estoqueService.consultarBebidasPorSecao(secaoId);
            return ResponseEntity.ok(bebidas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/secoes")
    public ResponseEntity<List<Secao>> consultarSecoesDisponiveis(@RequestParam(required = false) Double volume) {
        try {
            List<Secao> secoes;
            if (volume != null) {
                secoes = estoqueService.consultarSecoesDisponiveis(volume);
            } else {
                secoes = estoqueService.consultarTodasSecoes();  // Usando o novo método
            }
            return ResponseEntity.ok(secoes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/quantidade-por-secao")
    public ResponseEntity<?> consultarQuantidadePorSecao() {
        List<Object[]> resultado = estoqueService.consultarQuantidadePorSecao();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/todas-bebidas")
    public ResponseEntity<List<Bebida>> consultarTodasBebidas() {
        List<Bebida> bebidas = estoqueService.consultarTodasBebidas();
        return ResponseEntity.ok(bebidas);
    }

    @DeleteMapping("/bebida/{id}")
    public ResponseEntity<?> deletarBebida(@PathVariable Long id, @RequestParam String responsavel) {
        try {
            estoqueService.deletarBebida(id, responsavel);
            return ResponseEntity.ok("Bebida deletada com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar bebida.");
        }
    }

    @GetMapping("/historico-movimentacoes")
    public ResponseEntity<List<Object[]>> consultarHistoricoMovimentacoes() {
        List<Object[]> historicoMovimentacoes = estoqueService.consultarHistoricoMovimentacoes();
        return ResponseEntity.ok(historicoMovimentacoes);
    }
}