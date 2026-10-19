package br.com.fiap.dao;

import br.com.fiap.bean.Voucher;
import br.com.fiap.connection.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoucherDAO {

    // CREATE
    public Voucher inserir(Voucher voucher) throws SQLException {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return inserir(conexao, voucher);
        }
    }

    // CREATE usando uma conexão já aberta (permite participar de uma transação do BO)
    public Voucher inserir(Connection conexao, Voucher voucher) throws SQLException {
        String sql = "INSERT INTO voucher (id_voucher, codigo, cpf_usuario, tipo_beneficio, valor, status, validade) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        int id = SequenceUtil.proximoId(conexao, "seq_voucher");
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, voucher.getCodigo());
            stmt.setString(3, voucher.getCpfUsuario());
            stmt.setString(4, voucher.getTipoBeneficio());
            stmt.setDouble(5, voucher.getValor());
            stmt.setString(6, voucher.getStatus());
            stmt.setDate(7, Date.valueOf(voucher.getValidade()));
            stmt.executeUpdate();
        }
        voucher.setIdVoucher(id);
        return voucher;
    }

    // READ - buscar por id
    public Voucher buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM voucher WHERE id_voucher = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarVoucher(rs) : null;
            }
        }
    }

    // READ - buscar por código
    public Voucher buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM voucher WHERE codigo = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarVoucher(rs) : null;
            }
        }
    }

    // READ - listar os vouchers de um usuário
    public List<Voucher> listarPorUsuario(String cpfUsuario) throws SQLException {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM voucher WHERE cpf_usuario = ? ORDER BY id_voucher";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpfUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vouchers.add(montarVoucher(rs));
                }
            }
        }
        return vouchers;
    }

    // READ - listar todos
    public List<Voucher> listarTodos() throws SQLException {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM voucher ORDER BY id_voucher";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vouchers.add(montarVoucher(rs));
            }
        }
        return vouchers;
    }

    // UPDATE
    public boolean atualizar(Voucher voucher) throws SQLException {
        String sql = "UPDATE voucher SET tipo_beneficio = ?, valor = ?, status = ?, validade = ? "
                + "WHERE id_voucher = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, voucher.getTipoBeneficio());
            stmt.setDouble(2, voucher.getValor());
            stmt.setString(3, voucher.getStatus());
            stmt.setDate(4, Date.valueOf(voucher.getValidade()));
            stmt.setInt(5, voucher.getIdVoucher());
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM voucher WHERE id_voucher = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // monta um objeto Voucher a partir do ResultSet
    private Voucher montarVoucher(ResultSet rs) throws SQLException {
        Date validade = rs.getDate("validade");
        return new Voucher(
                rs.getInt("id_voucher"),
                rs.getString("codigo"),
                rs.getString("cpf_usuario"),
                rs.getString("tipo_beneficio"),
                rs.getDouble("valor"),
                rs.getString("status"),
                validade != null ? validade.toLocalDate() : null
        );
    }
}
