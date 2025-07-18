package com.example.gestao.carros.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "relatorios_financeiros") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Relatory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private Float valorCompra;
    private Float valorVenda;
    private Float custosAdicionais;
    private Float lucro;

    @OneToOne
    @JoinColumn(name = "veiculo_id", unique = true)
    private Vehicle vehicle;

   
}