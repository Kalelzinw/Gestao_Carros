
package com.example.gestao.carros.dto;

import java.time.LocalDate;

public record RegisterRequestDTO ( String fotoUrl,String name, String email,String cpf, String tel, LocalDate dateBorn,  String password, String cpassword  ) {
}
