package com.example.BeverageStockMaster.repository;

import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
import com.example.BeverageStockMaster.domain.Secao;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HistoricoMovimentacaoRepository extends JpaRepository<HistoricoMovimentacao, Long> {
    List<HistoricoMovimentacao> findBySecaoAndTipoMovimentacao(Secao secao, String tipoMovimentacao, Sort sort);

    @Query("SELECT h.horario, h.tipoMovimentacao, SUM(h.volume), h.secao.nome, h.responsavel " +
            "FROM HistoricoMovimentacao h " +
            "GROUP BY h.horario, h.tipoMovimentacao, h.secao.nome, h.responsavel " +
            "ORDER BY h.horario")
    List<Object[]> findHistoricoAgrupadoPorDataETipo();

}