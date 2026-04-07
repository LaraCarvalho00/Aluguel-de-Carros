package com.pucminas.aluguelcarros.domain.enums;

public enum TipoPropriedade {
    CLIENTE("Cliente"),
    EMPRESA("Empresa"),
    BANCO("Banco");

    private final String descricao;

    TipoPropriedade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
