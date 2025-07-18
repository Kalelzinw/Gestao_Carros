package com.example.gestao.carros.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção personalizada para ser lançada quando um recurso não é encontrado no banco de dados.
 * A anotação @ResponseStatus faz o Spring retornar um código HTTP 404 Not Found automaticamente.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
