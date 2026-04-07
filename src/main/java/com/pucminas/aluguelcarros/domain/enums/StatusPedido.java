package com.pucminas.aluguelcarros.domain.enums;

public enum StatusPedido {
    PENDENTE("Pendente"),
    EM_ANALISE("Em Análise"),
    APROVADO("Aprovado"),
    REPROVADO("Reprovado"),
    CONTRATADO("Contratado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
