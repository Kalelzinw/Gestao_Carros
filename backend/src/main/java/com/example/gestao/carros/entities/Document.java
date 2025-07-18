package com.example.gestao.carros.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documentos") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Document { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String tipo; 
    private String arquivo; 

    @ManyToOne
    @JoinColumn(name = "veiculo_id") 
    private Vehicle vehicle; 

    
}