package br.com.fiap.exception;

/**
 * Exceção de negócio da API. Carrega o código HTTP que deve ser devolvido ao cliente:
 * a camada BO lança o erro e o ApiExceptionHandler o converte em resposta JSON.
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int status;

    public ApiException(int status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    /** 400 - dados enviados inválidos. */
    public static ApiException invalido(String mensagem) {
        return new ApiException(400, mensagem);
    }

    /** 401 - credenciais inválidas. */
    public static ApiException naoAutorizado(String mensagem) {
        return new ApiException(401, mensagem);
    }

    /** 403 - recurso pertence a outro usuário. */
    public static ApiException proibido(String mensagem) {
        return new ApiException(403, mensagem);
    }

    /** 404 - recurso inexistente. */
    public static ApiException naoEncontrado(String mensagem) {
        return new ApiException(404, mensagem);
    }

    /** 409 - conflito com o estado atual do recurso (duplicidade, já concluído etc.). */
    public static ApiException conflito(String mensagem) {
        return new ApiException(409, mensagem);
    }

    /** 422 - dados válidos, mas a regra de negócio não permite a operação. */
    public static ApiException regraDeNegocio(String mensagem) {
        return new ApiException(422, mensagem);
    }
}
