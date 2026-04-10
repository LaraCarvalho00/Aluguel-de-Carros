package com.pucminas.aluguelcarros.domain.enums;

public enum Perfil {
    CLIENTE("Cliente"),
    AGENTE("Agente"),
    ADMIN("Administrador");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
