package com.biopark.cepex_system.domain.monitoria;

import lombok.Getter;

@Getter
public enum SelectionType {
    ENTREVISTA("Entrevista"),
    ANALISE_HISTORICO("Análise de Histórico"),
    ENTREVISTA_ANALISE_HISTORICO("Entrevista e Análise de Histórico");

    private final String descricao;

    /**
     * Construtor do enum.
     * @param descricao A descrição legível do modelo de seleção.
     */
    SelectionType(String descricao) {
        this.descricao = descricao;
    }

}
