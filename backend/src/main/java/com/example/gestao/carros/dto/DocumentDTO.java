package com.example.gestao.carros.dto;

import com.example.gestao.carros.entities.Document;

/**
 * DTO para representar os dados de um documento associado a um veículo.
 */
public class DocumentDTO {

    private Integer id;
    private String tipo;
    private String arquivo; // Este campo guardará o nome do arquivo salvo no disco

    // Construtor padrão
    public DocumentDTO() {
    }

    // Construtor que transforma uma entidade Document neste DTO
    public DocumentDTO(Document entity) {
        this.id = entity.getId();
        this.tipo = entity.getTipo();
        this.arquivo = entity.getArquivo();
    }

    // --- Getters e Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }
}
