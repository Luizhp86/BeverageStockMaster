    package com.example.BeverageStockMaster.domain;
    import jakarta.persistence.*;

    import java.time.LocalDate;

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "tipo_discriminador")
    public class Bebida {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nome;

        private double volume;

        @ManyToOne
        private TipoBebida tipoBebida;

        @ManyToOne
        private Secao secao;

        private String responsavel;

        private LocalDate dataEntrada;

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

        public String getResponsavel() {
            return responsavel;
        }

        public void setResponsavel(String responsavel) {
            this.responsavel = responsavel;
        }

        public LocalDate getDataEntrada() {
            return dataEntrada;
        }

        public void setDataEntrada(LocalDate dataEntrada) {
            this.dataEntrada = dataEntrada;
        }
    }
