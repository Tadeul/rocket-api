package br.com.fiap.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões JDBC com o Oracle (padrão Connection Factory).
 * Cada valor pode ser sobrescrito por variável de ambiente (DB_URL, DB_USER,
 * DB_PASSWORD), o que permite configurar o deploy na nuvem sem alterar o código.
 * Quando a variável não existe, usa o valor padrão definido aqui.
 */
public class ConnectionFactory {

    private static final String URL = valor("DB_URL", "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL");
    private static final String USER = valor("DB_USER", "RM571074");
    private static final String PASSWORD = valor("DB_PASSWORD", "150208");

    private ConnectionFactory() {
    }

    private static String valor(String variavel, String padrao) {
        String env = System.getenv(variavel);
        return (env == null || env.isBlank()) ? padrao : env;
    }

    /**
     * Abre e retorna uma nova conexão com o banco de dados.
     * Quem chamar é responsável por fechá-la (try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
