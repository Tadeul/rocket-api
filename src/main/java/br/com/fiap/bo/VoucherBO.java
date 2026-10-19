package br.com.fiap.bo;

import br.com.fiap.bean.Usuario;
import br.com.fiap.bean.Voucher;
import br.com.fiap.connection.ConnectionFactory;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dao.VoucherDAO;
import br.com.fiap.dto.GerarVoucherRequest;
import br.com.fiap.dto.ResgateRequest;
import br.com.fiap.exception.ApiException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class VoucherBO {

    /** Pontos descontados do usuário para gerar um voucher. */
    public static final int CUSTO_EM_PONTOS = 50;
    public static final double VALOR_PASSAGEM = 4.40;
    public static final int VALIDADE_EM_DIAS = 30;
    public static final String BENEFICIO_PADRAO = "Passagem de metrô gratuita";

    private final VoucherDAO voucherDAO;
    private final UsuarioDAO usuarioDAO;

    public VoucherBO(VoucherDAO voucherDAO, UsuarioDAO usuarioDAO) {
        this.voucherDAO = voucherDAO;
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Gera um voucher de mobilidade para o usuário, descontando CUSTO_EM_PONTOS do saldo.
     * O desconto dos pontos e a gravação do voucher ocorrem na mesma transação.
     */
    public Voucher gerar(GerarVoucherRequest request) throws SQLException {
        if (request == null || request.cpfUsuario() == null || request.cpfUsuario().isBlank()) {
            throw ApiException.invalido("Informe o CPF do usuário.");
        }
        String cpf = request.cpfUsuario().replaceAll("\\D", "");
        if (!cpf.matches("\\d{11}")) {
            throw ApiException.invalido("O CPF deve conter exatamente 11 dígitos numéricos.");
        }
        String beneficio = request.tipoBeneficio() == null || request.tipoBeneficio().isBlank()
                ? BENEFICIO_PADRAO : request.tipoBeneficio().trim();
        if (beneficio.length() > 100) {
            throw ApiException.invalido("O tipo de benefício deve ter no máximo 100 caracteres.");
        }

        Usuario usuario = usuarioDAO.buscarPorCpf(cpf);
        if (usuario == null) {
            throw ApiException.naoEncontrado("Usuário não encontrado para o CPF informado.");
        }
        if (!Usuario.STATUS_ATIVO.equals(usuario.getStatus())) {
            throw ApiException.regraDeNegocio("Usuário inativo não pode gerar vouchers.");
        }
        if (!usuario.possuiPontos(CUSTO_EM_PONTOS)) {
            throw ApiException.regraDeNegocio("Pontos insuficientes: o voucher custa " + CUSTO_EM_PONTOS
                    + " pontos e o usuário possui " + usuario.getPontos() + ".");
        }

        Voucher voucher = new Voucher(cpf, beneficio, VALOR_PASSAGEM, LocalDate.now().plusDays(VALIDADE_EM_DIAS));
        // garante que o código gerado ainda não existe
        for (int tentativa = 0; tentativa < 10 && voucherDAO.buscarPorCodigo(voucher.getCodigo()) != null; tentativa++) {
            voucher.setCodigo(voucher.gerarCodigo());
        }
        usuario.debitarPontos(CUSTO_EM_PONTOS);

        try (Connection conexao = ConnectionFactory.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                usuarioDAO.atualizar(conexao, usuario);
                voucherDAO.inserir(conexao, voucher);
                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        }
        return voucher;
    }

    /** Lista todos os vouchers ou, quando o CPF é informado, apenas os daquele usuário. */
    public List<Voucher> listar(String cpf) throws SQLException {
        if (cpf == null || cpf.isBlank()) {
            return voucherDAO.listarTodos();
        }
        return voucherDAO.listarPorUsuario(cpf.replaceAll("\\D", ""));
    }

    public Voucher buscarPorId(int id) throws SQLException {
        Voucher voucher = voucherDAO.buscarPorId(id);
        if (voucher == null) {
            throw ApiException.naoEncontrado("Voucher não encontrado.");
        }
        return voucher;
    }

    public Voucher buscarPorCodigo(String codigo) throws SQLException {
        Voucher voucher = voucherDAO.buscarPorCodigo(codigo == null ? "" : codigo.trim().toUpperCase());
        if (voucher == null) {
            throw ApiException.naoEncontrado("Voucher não encontrado para o código informado.");
        }
        return voucher;
    }

    /** Atualiza tipo de benefício, valor e validade de um voucher que ainda está disponível. */
    public Voucher atualizar(int id, Voucher dados) throws SQLException {
        Voucher atual = buscarPorId(id);
        if (dados == null) {
            throw ApiException.invalido("Informe os dados do voucher.");
        }
        if (!atual.estaDisponivel()) {
            throw ApiException.conflito("Voucher já utilizado não pode ser alterado.");
        }
        String beneficio = dados.getTipoBeneficio() == null ? "" : dados.getTipoBeneficio().trim();
        if (beneficio.isEmpty() || beneficio.length() > 100) {
            throw ApiException.invalido("O tipo de benefício é obrigatório e deve ter no máximo 100 caracteres.");
        }
        if (dados.getValor() <= 0) {
            throw ApiException.invalido("O valor do voucher deve ser maior que zero.");
        }
        if (dados.getValidade() == null || dados.getValidade().isBefore(LocalDate.now())) {
            throw ApiException.invalido("A validade deve ser uma data de hoje em diante.");
        }
        atual.setTipoBeneficio(beneficio);
        atual.setValor(dados.getValor());
        atual.setValidade(dados.getValidade());
        voucherDAO.atualizar(atual);
        return atual;
    }

    /**
     * Resgata o voucher: precisa estar disponível, dentro da validade e pertencer ao CPF informado.
     * Depois do resgate o status passa de DISPONIVEL para UTILIZADO e não pode ser resgatado de novo.
     */
    public Voucher resgatar(int id, ResgateRequest request) throws SQLException {
        if (request == null || request.cpf() == null || request.cpf().isBlank()) {
            throw ApiException.invalido("Informe o CPF do usuário que está resgatando.");
        }
        String cpf = request.cpf().replaceAll("\\D", "");
        Voucher voucher = buscarPorId(id);

        if (!voucher.validarVoucher(cpf)) {
            if (!voucher.pertenceA(cpf)) {
                throw ApiException.proibido("Este voucher não pertence ao CPF informado.");
            }
            if (!voucher.estaDisponivel()) {
                throw ApiException.conflito("Este voucher já foi utilizado.");
            }
            throw ApiException.regraDeNegocio("Este voucher está expirado.");
        }
        voucher.resgatarVoucher();
        voucherDAO.atualizar(voucher);
        return voucher;
    }

    public void deletar(int id) throws SQLException {
        buscarPorId(id);
        voucherDAO.deletar(id);
    }
}
