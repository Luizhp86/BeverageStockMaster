package com.example.BeverageStockMaster.repository;

import com.example.BeverageStockMaster.domain.Bebida;
import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.domain.TipoBebida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BebidaRepository extends JpaRepository<Bebida, Long> {
    List<Bebida> findBySecao(Secao secao);
    List<Bebida> findByTipoBebida(TipoBebida tipoBebida);

    @Query("SELECT s.nome, b.tipoBebida.descricao, SUM(b.volume) " +
            "FROM Bebida b JOIN b.secao s " +
            "GROUP BY s.nome, b.tipoBebida.descricao")
    List<Object[]> findQuantidadePorSecaoETipo();

    boolean existsBySecaoIdAndTipoBebidaRestricaoQuarentenaAndDataEntrada(Long secaoId, boolean restricaoQuarentena, LocalDate dataEntrada);

}