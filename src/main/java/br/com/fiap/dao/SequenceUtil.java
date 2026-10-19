package br.com.fiap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Busca o próximo valor de uma sequence Oracle (usado para gerar os ids). */
final class SequenceUtil {

    private SequenceUtil() {
    }

    static int proximoId(Connection conexao, String sequence) throws SQLException {
        String sql = "SELECT " + sequence + ".NEXTVAL FROM dual";
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
