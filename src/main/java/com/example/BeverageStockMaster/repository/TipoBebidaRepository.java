package com.example.BeverageStockMaster.repository;

import com.example.BeverageStockMaster.domain.TipoBebida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoBebidaRepository extends JpaRepository<TipoBebida, Long> {
    boolean existsByDescricao(String descricao);
}