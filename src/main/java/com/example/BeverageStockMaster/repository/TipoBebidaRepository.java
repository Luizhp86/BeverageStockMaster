package com.example.BeverageStockMaster.repository;

import com.example.BeverageStockMaster.domain.TipoBebida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoBebidaRepository extends JpaRepository<TipoBebida, Long> {
    Optional<TipoBebida> findByDescricaoAndRestricaoQuarentena(String descricao, boolean restricaoQuarentena);
}