package com.example.BeverageStockMaster.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_discriminador")
public class Bebida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private double volume;

    @Enumerated(EnumType.STRING)
    private TipoBebida tipoBebida;

    @ManyToOne
    private Secao secao;

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

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public TipoBebida getTipoBebida() {
        return tipoBebida;
    }

    public void setTipoBebida(TipoBebida tipoBebida) {
        this.tipoBebida = tipoBebida;
    }

    public Secao getSecao() {
        return secao;
    }

    public void setSecao(Secao secao) {
        this.secao = secao;
    }

    // Enum TipoBebida
    public enum TipoBebida {
        ALCOOLICA("Alcoólica"),
        NAO_ALCOOLICA("Não Alcoólica");

        private final String descricao;
        //entro com o enum e retona a descrição
        TipoBebida(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

    }
}
