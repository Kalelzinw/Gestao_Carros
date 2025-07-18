package com.example.gestao.carros.dto;

import com.example.gestao.carros.entities.enums.StatusVeiculo;

// NÂO use Lombok aqui para seguir a estrutura que você pediu
// com getters e setters explícitos.

/**
 * DTO para receber os dados na CRIAÇÃO de um novo veículo.
 * Contém apenas os dados que o usuário informa no momento do cadastro.
 */
public class VehicleCreateDTO {

    // --- Dados do Veículo ---
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private Integer quilometragem;
    private String localidade;
    private Float valorVenda;
    private Float valorCompra;
    private String imageUrl;
    private StatusVeiculo status;
    private Boolean visivel;
    private String placa;
    private String chassi;

    // --- ID da Entidade Relacionada ---
    private Integer userId; // Apenas o ID do usuário ao qual o veículo pertence

    // Construtor padrão
    public VehicleCreateDTO() {
    }

    // --- Getters e Setters ---

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public Integer getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Integer quilometragem) { this.quilometragem = quilometragem; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public Float getValorVenda() { return valorVenda; }
    public void setValorVenda(Float valorVenda) { this.valorVenda = valorVenda; }

    public Float getValorCompra() { return valorCompra; }
    public void setValorCompra(Float valorCompra) { this.valorCompra = valorCompra; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public StatusVeiculo getStatus() { return status; }
    public void setStatus(StatusVeiculo status) { this.status = status; }

    public Boolean getVisivel() { return visivel; }
    public void setVisivel(Boolean visivel) { this.visivel = visivel; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getChassi() { return chassi; }
    public void setChassi(String chassi) { this.chassi = chassi; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}