package br.com.fiap.dao;

import br.com.fiap.bean.Missao;
import br.com.fiap.connection.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MissaoDAO {

    // CREATE
    public Missao inserir(Missao missao) throws SQLException {
        String sql = "INSERT INTO missao (id_missao, titulo, descricao, pontos, categoria, status, cpf_usuario) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection()) {
            int id = SequenceUtil.proximoId(conexao, "seq_missao");
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setString(2, missao.getTitulo());
                stmt.setString(3, missao.getDescricao());
                stmt.setInt(4, missao.getPontos());
                stmt.setString(5, missao.getCategoria());
                stmt.setString(6, missao.getStatus());
                stmt.setString(7, missao.getCpfUsuario());
                stmt.executeUpdate();
            }
            missao.setIdMissao(id);
        }
        return missao;
    }

    // READ - buscar por id
    public Missao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM missao WHERE id_missao = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarMissao(rs) : null;
            }
        }
    }

    // READ - listar as missões de um usuário
    public List<Missao> listarPorUsuario(String cpfUsuario) throws SQLException {
        List<Missao> missoes = new ArrayList<>();
        String sql = "SELECT * FROM missao WHERE cpf_usuario = ? ORDER BY id_missao";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpfUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    missoes.add(montarMissao(rs));
                }
            }
        }
        return missoes;
    }

    // READ - listar todas
    public List<Missao> listarTodas() throws SQLException {
        List<Missao> missoes = new ArrayList<>();
        String sql = "SELECT * FROM missao ORDER BY id_missao";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                missoes.add(montarMissao(rs));
            }
        }
        return missoes;
    }

    // UPDATE
    public boolean atualizar(Missao missao) throws SQLException {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return atualizar(conexao, missao);
        }
    }

    // UPDATE usando uma conexão já aberta (permite participar de uma transação do BO)
    public boolean atualizar(Connection conexao, Missao missao) throws SQLException {
        String sql = "UPDATE missao SET titulo = ?, descricao = ?, pontos = ?, categoria = ?, status = ? "
                + "WHERE id_missao = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, missao.getTitulo());
            stmt.setString(2, missao.getDescricao());
            stmt.setInt(3, missao.getPontos());
            stmt.setString(4, missao.getCategoria());
            stmt.setString(5, missao.getStatus());
            stmt.setInt(6, missao.getIdMissao());
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM missao WHERE id_missao = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // monta um objeto Missao a partir do ResultSet
    private Missao montarMissao(ResultSet rs) throws SQLException {
        return new Missao(
                rs.getInt("id_missao"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getInt("pontos"),
                rs.getString("categoria"),
                rs.getString("status"),
                rs.getString("cpf_usuario")
        );
    }
}
