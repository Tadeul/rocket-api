package br.com.fiap.dto;

/** Corpo de POST /vouchers. O tipo de benefício é opcional (padrão: passagem de metrô gratuita). */
public record GerarVoucherRequest(String cpfUsuario, String tipoBeneficio) {
}
