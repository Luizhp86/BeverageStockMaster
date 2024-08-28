package com.example.BeverageStockMaster.service;

import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
import com.example.BeverageStockMaster.repository.HistoricoMovimentacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricoMovimentacaoService {

    private final HistoricoMovimentacaoRepository historicoMovimentacaoRepository;

    public HistoricoMovimentacaoService(HistoricoMovimentacaoRepository historicoMovimentacaoRepository) {
        this.historicoMovimentacaoRepository = historicoMovimentacaoRepository;
    }

    public List<HistoricoMovimentacao>  getHistoricoAgrupado() {
        return historicoMovimentacaoRepository.findAll();
    }

    public List<HistoricoMovimentacao> getHistoricoPorSecaoETipoMovimentacao(
            String secaoNome, String tipoMovimentacao, LocalDateTime inicio, LocalDateTime fim) {
        return historicoMovimentacaoRepository.findBySecaoAndTipoMovimentacaoNoMesmoDia(secaoNome, tipoMovimentacao, inicio, fim);
    }
}
