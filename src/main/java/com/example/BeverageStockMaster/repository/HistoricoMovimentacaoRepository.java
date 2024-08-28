package com.example.BeverageStockMaster.repository;
import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoMovimentacaoRepository extends JpaRepository<HistoricoMovimentacao, Long> {

    @Query("SELECT h.horario, h.tipoMovimentacao, SUM(h.volume), h.secaoNome, h.responsavel, h.tipoBebidaNome " +
            "FROM HistoricoMovimentacao h " +
            "GROUP BY h.horario, h.tipoMovimentacao, h.secaoNome, h.responsavel, h.tipoBebidaNome " +
            "ORDER BY h.horario")
    List<Object[]> findHistoricoAgrupadoPorDataETipo();



    @Query("SELECT h FROM HistoricoMovimentacao h " +
            "WHERE h.secaoNome = :secaoNome " +
            "AND h.tipoMovimentacao = :tipoMovimentacao " +
            "AND h.horario BETWEEN :inicio AND :fim")
    List<HistoricoMovimentacao> findBySecaoAndTipoMovimentacaoNoMesmoDia(
            @Param("secaoNome") String secaoNome,
            @Param("tipoMovimentacao") String tipoMovimentacao,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}