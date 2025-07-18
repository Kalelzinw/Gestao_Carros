package com.example.gestao.carros.dto;

import java.time.LocalDate;

public record UserUpdateDTO(
    String email,
    String tel,
    String fotoUrl,
    String password
) {}
