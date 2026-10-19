package br.com.fiap.bo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Gera o hash (SHA-256) da senha, para que ela não fique em texto puro no banco.
 * Em um sistema real o ideal seria BCrypt com salt; aqui usamos a API padrão do Java.
 */
final class SenhaUtil {

    private SenhaUtil() {
    }

    static String hash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(senha.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
