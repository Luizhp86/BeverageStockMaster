package com.example.BeverageStockMaster.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Secao {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nome;
        private double capacidadeAtual;
        private double capacidadeMaxima;


        // Default constructor
        public Secao() {}

        // Constructor with arguments
        public Secao(Long id, String nome, double capacidadeAtual, double capacidadeMaxima, List<Bebida> bebidas) {
                this.id = id;
                this.nome = nome;
                this.capacidadeAtual = capacidadeAtual;
                this.capacidadeMaxima = capacidadeMaxima;
        }

        // Getters and Setters
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

        public double getCapacidadeMaxima() {
                return capacidadeMaxima;
        }

        public void setCapacidadeMaxima(double capacidadeMaxima) {
                this.capacidadeMaxima = capacidadeMaxima;
        }


}
