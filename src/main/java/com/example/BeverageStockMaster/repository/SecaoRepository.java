package com.example.BeverageStockMaster.repository;

import com.example.BeverageStockMaster.domain.Secao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecaoRepository extends JpaRepository<Secao, Long> {
    List<Secao> findByCapacidadeAtualGreaterThan(double volume);
    boolean existsById(Long id);
}