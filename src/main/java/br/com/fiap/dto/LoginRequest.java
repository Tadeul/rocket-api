package br.com.fiap.dto;

/** Corpo de POST /usuarios/login. */
public record LoginRequest(String cpf, String senha) {
}
