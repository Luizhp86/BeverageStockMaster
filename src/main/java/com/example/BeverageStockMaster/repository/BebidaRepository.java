package com.example.BeverageStockMaster.repository;

import com.example.BeverageStockMaster.domain.Bebida;
import com.example.BeverageStockMaster.domain.Secao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BebidaRepository extends JpaRepository<Bebida, Long> {
    List<Bebida> findBySecao(Secao secao);

    @Query("SELECT s.nome, b.tipoBebida, SUM(b.volume) " +
            "FROM Bebida b JOIN b.secao s " +
            "GROUP BY s.nome, b.tipoBebida")
    List<Object[]> findQuantidadePorSecaoETipo();
    boolean existsBySecaoId(Long secaoId);

}