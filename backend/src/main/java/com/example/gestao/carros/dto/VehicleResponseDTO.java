package com.example.gestao.carros.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.entities.Vehicle;
import com.example.gestao.carros.entities.enums.StatusVeiculo;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VehicleResponseDTO {

	private Integer id;
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
	private boolean isFavorited;
	private UserResponseDTO user;
	private List<DocumentDTO> documents;

	public VehicleResponseDTO() {
	}

	public VehicleResponseDTO(Vehicle vehicleEntity) {
		this.id = vehicleEntity.getId();
		this.marca = vehicleEntity.getMarca();
		this.modelo = vehicleEntity.getModelo();
		this.ano = vehicleEntity.getAno();
		this.cor = vehicleEntity.getCor();
		this.quilometragem = vehicleEntity.getQuilometragem();
		this.localidade = vehicleEntity.getLocalidade();
		this.valorVenda = vehicleEntity.getValorVenda();
		this.valorCompra = vehicleEntity.getValorCompra();
		this.imageUrl = vehicleEntity.getImageUrl();
		this.status = vehicleEntity.getStatus();
		this.visivel = vehicleEntity.getVisivel();
		this.placa = vehicleEntity.getPlaca();
		this.chassi = vehicleEntity.getChassi();
		this.isFavorited = false;

		if (vehicleEntity.getUser() != null) {
			User userEntity = vehicleEntity.getUser();
			this.user = new UserResponseDTO(userEntity.getId(), userEntity.getFotoUrl(), userEntity.getName(),
					userEntity.getEmail(), userEntity.getTel(), userEntity.getDateBorn());
		}

		// CORREÇÃO: Usando o método correto 'getHistoricoGastos()' da sua entidade
		// Vehicle
		if (vehicleEntity.getHistoricoGastos() != null) {
			this.documents = vehicleEntity.getHistoricoGastos().stream().map(DocumentDTO::new)
					.collect(Collectors.toList());
		}
	}

	// --- Getters e Setters Completos ---

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

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public Integer getQuilometragem() {
		return quilometragem;
	}

	public void setQuilometragem(Integer quilometragem) {
		this.quilometragem = quilometragem;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public Float getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(Float valorVenda) {
		this.valorVenda = valorVenda;
	}

	public Float getValorCompra() {
		return valorCompra;
	}

	public void setValorCompra(Float valorCompra) {
		this.valorCompra = valorCompra;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public StatusVeiculo getStatus() {
		return status;
	}

	public void setStatus(StatusVeiculo status) {
		this.status = status;
	}

	public Boolean getVisivel() {
		return visivel;
	}

	public void setVisivel(Boolean visivel) {
		this.visivel = visivel;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getChassi() {
		return chassi;
	}

	public void setChassi(String chassi) {
		this.chassi = chassi;
	}

	@JsonProperty("isFavorited")
	public boolean isFavorited() {
		return isFavorited;
	}

	public void setFavorited(boolean favorited) {
		isFavorited = favorited;
	}

	public UserResponseDTO getUser() {
		return user;
	}

	public void setUser(UserResponseDTO user) {
		this.user = user;
	}

	public List<DocumentDTO> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDTO> documents) {
		this.documents = documents;
	}
}
