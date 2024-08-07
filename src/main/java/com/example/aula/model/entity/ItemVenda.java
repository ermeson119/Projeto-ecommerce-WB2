package com.example.aula.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;


@Entity
public class ItemVenda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Min(1)
    private Integer quantidade;
    @ManyToOne
    private Venda venda;
    @ManyToOne
    private Produto produto;

    public ItemVenda(Long id, Integer quantidade, Produto produto, Venda venda) {
        this.id = id;
        this.quantidade = quantidade;
        this.produto = produto;
        this.venda = venda;
    }

    public ItemVenda() {}

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produtos) {
        this.produto = produtos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Double getTotal() {
        return produto.getValor().doubleValue() * this.getQuantidade();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemVenda itemVenda = (ItemVenda) o;

        return Objects.equals(produto, itemVenda.produto);
    }

    @Override
    public int hashCode() {
        return produto != null ? produto.hashCode() : 0;
    }
}
