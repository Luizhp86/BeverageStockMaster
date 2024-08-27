package com.example.BeverageStockMaster.domain;

import jakarta.persistence.*;

@Entity
public class Secao {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nome;
        private double capacidadeAtual;
        private double capacidadeMaxima;

        @ManyToOne
        private TipoBebida tipoBebida;

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

        public double getCapacidadeMaxima() {
                return capacidadeMaxima;
        }

        public void setCapacidadeMaxima(double capacidadeMaxima) {
                this.capacidadeMaxima = capacidadeMaxima;
        }

        public TipoBebida getTipoBebida() {
                return tipoBebida;
        }

        public void setTipoBebida(TipoBebida tipoBebida) {
                this.tipoBebida = tipoBebida;
        }
}
