package br.com.fiap.exception;

import java.time.LocalDateTime;

/** Corpo JSON padrão devolvido em qualquer erro da API. */
public record ErroResponse(int status, String erro, String mensagem, LocalDateTime timestamp) {
}
