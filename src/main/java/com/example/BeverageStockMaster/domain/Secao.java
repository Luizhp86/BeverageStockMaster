package com.example.BeverageStockMaster.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.Transient;

@Entity
public class Secao {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nome;
        private double utilizacaoTotal;

        @Transient
        private double capacidadeDisponivel;

        @Transient
        private transient String tipoBebidaUtilizada;

        public String getTipoBebidaUtilizada() {
                return tipoBebidaUtilizada;
        }

        public void setTipoBebidaUtilizada(String tipoBebidaUtilizada) {
                this.tipoBebidaUtilizada = tipoBebidaUtilizada;
        }

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

        public double getUtilizacaoTotal() {
                return utilizacaoTotal;
        }

        public void setUtilizacaoTotal(double utilizacaoTotal) {
                this.utilizacaoTotal = utilizacaoTotal;
        }

        public double getCapacidadeDisponivel() {
                return capacidadeDisponivel;
        }

        public void setCapacidadeDisponivel(double capacidadeDisponivel) {
                this.capacidadeDisponivel = capacidadeDisponivel;
        }
}
