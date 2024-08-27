package com.example.BeverageStockMaster.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistoricoMovimentacao {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private LocalDateTime horario;

        private String tipoMovimentacao; // "ENTRADA" ou "SAIDA"

        private double volume;

        @ManyToOne
        private Secao secao;

        private String responsavel;

        // Construtor padrão (necessário para JPA)
        public HistoricoMovimentacao() {
        }

        // Construtor com argumentos
        public HistoricoMovimentacao(LocalDateTime horario, String tipoMovimentacao, double volume, Secao secao, String responsavel) {
                this.horario = horario;
                this.tipoMovimentacao = tipoMovimentacao;
                this.volume = volume;
                this.secao = secao;
                this.responsavel = responsavel;
        }

        // Getters e Setters
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public LocalDateTime getHorario() {
                return horario;
        }

        public void setHorario(LocalDateTime horario) {
                this.horario = horario;
        }

        public String getTipoMovimentacao() {
                return tipoMovimentacao;
        }

        public void setTipoMovimentacao(String tipoMovimentacao) {
                this.tipoMovimentacao = tipoMovimentacao;
        }

        public double getVolume() {
                return volume;
        }

        public void setVolume(double volume) {
                this.volume = volume;
        }

        public Secao getSecao() {
                return secao;
        }

        public void setSecao(Secao secao) {
                this.secao = secao;
        }

        public String getResponsavel() {
                return responsavel;
        }

        public void setResponsavel(String responsavel) {
                this.responsavel = responsavel;
        }
}
