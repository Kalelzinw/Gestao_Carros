package com.example.gestao.carros.dto;

import com.example.gestao.carros.entities.Vehicle;

// DTO para exibir um resumo do veículo em listas.
public class VehicleSummaryDTO {

    private Integer id;
    private String marca;
    private String modelo;
    private Integer ano;
    private Float valorVenda;
    private String imageUrl;
    private String localidade;

    public VehicleSummaryDTO() {
    }

    public VehicleSummaryDTO(Vehicle entity) {
        this.id = entity.getId();
        this.marca = entity.getMarca();
        this.modelo = entity.getModelo();
        this.ano = entity.getAno();
        this.valorVenda = entity.getValorVenda();
        this.imageUrl = entity.getImageUrl();
        this.localidade = entity.getLocalidade();
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Float getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(Float valorVenda) {
		this.valorVenda = valorVenda;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

    
}