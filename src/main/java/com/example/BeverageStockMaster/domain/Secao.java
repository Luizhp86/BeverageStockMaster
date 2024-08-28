package com.example.BeverageStockMaster.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Secao {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nome;
        private double capacidadeAtual;

        // Removido o campo tipoBebida

        // Getters e Setters
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getNome() {
                return nome;
        }

        public void setNome(String nome) {
                this.nome = nome;
        }

        public double getCapacidadeAtual() {
                return capacidadeAtual;
        }

        public void setCapacidadeAtual(double capacidadeAtual) {
                this.capacidadeAtual = capacidadeAtual;
        }


}
