package com.example.gestao.carros.entities;

import java.util.List;

import com.example.gestao.carros.entities.enums.StatusVeiculo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "veiculos") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vehicle { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @Enumerated(EnumType.STRING)
    private StatusVeiculo status;

    private Boolean visivel;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> historicoGastos;

    private String placa;
    private String chassi;

    @ManyToOne // Um veículo pertence a um usuário
    @JoinColumn(name = "user_id") // Mais consistente com o nome do campo 'user'
    private User user;

    @ManyToMany(mappedBy = "favorites") // Já mapeado na classe User
    private List<User> usersFavoritaram;
    @OneToOne(mappedBy = "vehicle")
    private Relatory relatory;

   
}