    package com.example.BeverageStockMaster.controller;
    import com.example.BeverageStockMaster.domain.TipoBebida;
    import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
    import com.example.BeverageStockMaster.service.EstoqueService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import com.example.BeverageStockMaster.domain.Bebida;

    import com.example.BeverageStockMaster.domain.Secao;

    import java.util.List;
    @RestController
    @RequestMapping("/api/estoque")
    public class EstoqueController {

        @Autowired
        EstoqueService estoqueService;

        @Autowired
        TipoBebidaRepository tipoBebidaRepository;


        @PostMapping("/entrada")
        public ResponseEntity<String> registrarEntrada(@RequestBody Bebida bebida, @RequestParam Long secaoId, @RequestParam String responsavel) {
            try {
                TipoBebida tipoBebida = tipoBebidaRepository.findById(bebida.getTipoBebida().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Tipo de Bebida não encontrado"));
                bebida.setTipoBebida(tipoBebida);
                estoqueService.registrarEntradaBebida(bebida, secaoId, responsavel);
                return ResponseEntity.ok("Entrada registrada com sucesso.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao registrar a entrada.");
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
        public ResponseEntity<Double> consultarVolumeTotal() {

            double volumeTotal = estoqueService.consultarVolumeTotal();
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
        public ResponseEntity<List<Secao>> consultarSecoesDisponiveis(@RequestParam Long tipoBebidaId, @RequestParam(required = false) Double volume) {
            try {
                List<Secao> secoes;

                if (volume != null) {
                    secoes = estoqueService.consultarSecoesDisponiveis(tipoBebidaId, volume);
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
        public ResponseEntity<?> saidaBebida(@PathVariable Long id, @RequestParam String responsavel) {
            try {
                estoqueService.saidaBebida(id, responsavel);
                return ResponseEntity.ok("Saída de bebida com sucesso.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao dar saída de bebida.");
            }
        }

        @GetMapping("/historico-movimentacoes")
        public ResponseEntity<List<Object[]>> consultarHistoricoMovimentacoes() {
            List<Object[]> historicoMovimentacoes = estoqueService.consultarHistoricoMovimentacoes();
            return ResponseEntity.ok(historicoMovimentacoes);
        }

        @GetMapping("/locais-disponiveis")
        public ResponseEntity<List<Secao>> consultarLocaisDisponiveis(@RequestParam double volume) {
            List<Secao> secoesDisponiveis = estoqueService.consultarLocaisDisponiveisParaVolume(volume);
            return ResponseEntity.ok(secoesDisponiveis);
        }

        @GetMapping("/secoes-disponiveis-venda")
        public ResponseEntity<List<Secao>> consultarSecoesDisponiveisParaVenda(@RequestParam Long tipoBebidaId) {
            List<Secao> secoesDisponiveis = estoqueService.consultarSecoesDisponiveisParaVenda(tipoBebidaId);
            return ResponseEntity.ok(secoesDisponiveis);
        }
    }