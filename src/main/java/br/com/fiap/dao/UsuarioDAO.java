package br.com.fiap.dao;

import br.com.fiap.bean.Usuario;
import br.com.fiap.connection.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioDAO {

    // CREATE
    public Usuario inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (id_usuario, nome, cpf, email, senha, pontos, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection()) {
            int id = SequenceUtil.proximoId(conexao, "seq_usuario");
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setString(2, usuario.getNome());
                stmt.setString(3, usuario.getCpf());
                stmt.setString(4, usuario.getEmail());
                stmt.setString(5, usuario.getSenha());
                stmt.setInt(6, usuario.getPontos());
                stmt.setString(7, usuario.getStatus());
                stmt.executeUpdate();
            }
            usuario.setIdUsuario(id);
        }
        return usuario;
    }

    // READ - buscar por id
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        }
    }

    // READ - buscar por cpf
    public Usuario buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE cpf = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        }
    }

    // READ - listar todos
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY id_usuario";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(montarUsuario(rs));
            }
        }
        return usuarios;
    }

    // UPDATE
    public boolean atualizar(Usuario usuario) throws SQLException {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return atualizar(conexao, usuario);
        }
    }

    // UPDATE usando uma conexão já aberta (permite participar de uma transação do BO)
    public boolean atualizar(Connection conexao, Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, pontos = ?, status = ? "
                + "WHERE id_usuario = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setInt(4, usuario.getPontos());
            stmt.setString(5, usuario.getStatus());
            stmt.setInt(6, usuario.getIdUsuario());
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // monta um objeto Usuario a partir do ResultSet
    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("email"),
                rs.getString("senha"),
                rs.getInt("pontos"),
                rs.getString("status")
        );
    }
}
