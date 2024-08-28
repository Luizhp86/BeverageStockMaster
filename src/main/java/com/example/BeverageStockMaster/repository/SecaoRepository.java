package com.example.BeverageStockMaster.repository;

import com.example.BeverageStockMaster.domain.Secao;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SecaoRepository extends JpaRepository<Secao, Long> {
    boolean existsById(Long id);
}