package com.example.BeverageStockMaster.repository;
import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HistoricoMovimentacaoRepository extends JpaRepository<HistoricoMovimentacao, Long> {

    @Query("SELECT h.horario, h.tipoMovimentacao, SUM(h.volume), h.secaoNome, h.responsavel " +
            "FROM HistoricoMovimentacao h " +
            "GROUP BY h.horario, h.tipoMovimentacao, h.secaoNome, h.responsavel " +
            "ORDER BY h.horario")
    List<Object[]> findHistoricoAgrupadoPorDataETipo();

}