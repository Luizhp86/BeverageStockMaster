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

        private String secaoNome; // Armazenar apenas o nome da seção

        private String responsavel;

        private String tipoBebidaNome; // Armazenar o nome do tipo de bebida


        // Construtor padrão (necessário para JPA)
        public HistoricoMovimentacao() {
        }

        // Construtor com argumentos
        public HistoricoMovimentacao(LocalDateTime horario, String tipoMovimentacao, double volume, String secaoNome, String responsavel, String tipoBebidaNome) {
                this.horario = horario;
                this.tipoMovimentacao = tipoMovimentacao;
                this.volume = volume;
                this.secaoNome = secaoNome;
                this.responsavel = responsavel;
                this.tipoBebidaNome = tipoBebidaNome;
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

        public String getSecaoNome() {
                return secaoNome;
        }

        public void setSecaoNome(String secaoNome) {
                this.secaoNome = secaoNome;
        }

        public String getResponsavel() {
                return responsavel;
        }

        public void setResponsavel(String responsavel) {
                this.responsavel = responsavel;
        }

        public String getTipoBebidaNome() {
                return tipoBebidaNome;
        }

        public void setTipoBebidaNome(String tipoBebidaNome) {
                this.tipoBebidaNome = tipoBebidaNome;
        }
}
