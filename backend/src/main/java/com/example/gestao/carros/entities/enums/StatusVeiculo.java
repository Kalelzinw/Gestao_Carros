package com.example.gestao.carros.entities.enums;

public enum StatusVeiculo {
    DISPONIVEL,
    RESERVADO,
    VENDIDO;

    // ADICIONE ESTE MÉTODO AQUI DENTRO DA CLASSE
    public static StatusVeiculo fromString(String text) {
        for (StatusVeiculo status : StatusVeiculo.values()) {
            // Compara o nome do enum (ex: "DISPONIVEL") com o texto recebido,
            // ignorando maiúsculas/minúsculas e espaços em branco.
            if (status.name().equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        // Se não encontrar correspondência, lança um erro.
        throw new IllegalArgumentException("Nenhum status encontrado para a string fornecida: " + text);
    }
}