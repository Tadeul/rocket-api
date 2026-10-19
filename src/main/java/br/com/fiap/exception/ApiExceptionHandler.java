package br.com.fiap.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Tratamento centralizado de exceções: toda falha vira um JSON ErroResponse
 * com o status HTTP adequado (400, 401, 403, 404, 405, 409, 415, 422, 500...).
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErroResponse> tratarApiException(ApiException ex) {
        return resposta(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErroResponse> tratarSqlException(SQLException ex) {
        log.error("Erro de banco de dados", ex);
        return resposta(500, "Erro ao acessar o banco de dados. Tente novamente mais tarde.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarExcecaoGenerica(Exception ex) {
        log.error("Erro inesperado", ex);
        return resposta(500, "Erro interno do servidor.");
    }

    /** Erros do próprio Spring MVC (JSON malformado, método não permitido, rota inexistente...). */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        int status = statusCode.value();
        return ResponseEntity.status(status).body(criar(status, mensagemPadrao(status)));
    }

    private ResponseEntity<ErroResponse> resposta(int status, String mensagem) {
        return ResponseEntity.status(status).body(criar(status, mensagem));
    }

    private ErroResponse criar(int status, String mensagem) {
        HttpStatus http = HttpStatus.resolve(status);
        String erro = http != null ? http.getReasonPhrase() : "Erro";
        return new ErroResponse(status, erro, mensagem, LocalDateTime.now());
    }

    private String mensagemPadrao(int status) {
        return switch (status) {
            case 400 -> "Requisição inválida: verifique o corpo e os parâmetros enviados.";
            case 404 -> "Recurso não encontrado.";
            case 405 -> "Método HTTP não permitido para este recurso.";
            case 415 -> "Tipo de conteúdo não suportado. Use application/json.";
            default -> "Erro ao processar a requisição.";
        };
    }
}
