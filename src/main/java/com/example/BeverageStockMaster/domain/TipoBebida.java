package com.example.BeverageStockMaster.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TipoBebida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private boolean restricaoQuarentena;


    // Construtor padrão
    public TipoBebida() {}

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isRestricaoQuarentena() {
        return restricaoQuarentena;
    }

    public void setRestricaoQuarentena(boolean restricaoQuarentena) {
        this.restricaoQuarentena = restricaoQuarentena;
    }
}
