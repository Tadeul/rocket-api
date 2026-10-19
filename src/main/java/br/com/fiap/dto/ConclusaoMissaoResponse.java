package br.com.fiap.dto;

import br.com.fiap.bean.Missao;

/** Resposta de PATCH /missoes/{id}/concluir: a missão concluída e o novo saldo do usuário. */
public record ConclusaoMissaoResponse(Missao missao, int pontosUsuario) {
}
