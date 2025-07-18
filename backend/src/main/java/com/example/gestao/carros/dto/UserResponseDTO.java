package com.example.gestao.carros.dto;

import java.time.LocalDate;

public record UserResponseDTO(
    Integer id,       // Id do usuário
    String fotoUrl,   // URL da foto
    String name,      // Nome
    String email,     // Email
    String tel,       // Telefone
    LocalDate dateBorn // Data de nascimento
) {}