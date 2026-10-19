package br.com.fiap.bo;

import br.com.fiap.bean.Missao;
import br.com.fiap.bean.Usuario;
import br.com.fiap.connection.ConnectionFactory;
import br.com.fiap.dao.MissaoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dto.ConclusaoMissaoResponse;
import br.com.fiap.exception.ApiException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class MissaoBO {

    private final MissaoDAO missaoDAO;
    private final UsuarioDAO usuarioDAO;

    public MissaoBO(MissaoDAO missaoDAO, UsuarioDAO usuarioDAO) {
        this.missaoDAO = missaoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    /** Cadastra uma missão para um usuário ATIVO existente. A missão sempre nasce DISPONIVEL. */
    public Missao cadastrar(Missao missao) throws SQLException {
        if (missao == null) {
            throw ApiException.invalido("Informe os dados da missão.");
        }
        missao.normalizar();
        List<String> erros = missao.errosDeCadastro();
        if (!erros.isEmpty()) {
            throw ApiException.invalido(String.join(" ", erros));
        }
        Usuario usuario = usuarioDAO.buscarPorCpf(missao.getCpfUsuario());
        if (usuario == null) {
            throw ApiException.naoEncontrado("Usuário não encontrado para o CPF informado.");
        }
        if (!Usuario.STATUS_ATIVO.equals(usuario.getStatus())) {
            throw ApiException.regraDeNegocio("Usuário inativo não pode receber missões.");
        }
        missao.setStatus(Missao.STATUS_DISPONIVEL);
        return missaoDAO.inserir(missao);
    }

    /** Lista todas as missões ou, quando o CPF é informado, apenas as daquele usuário. */
    public List<Missao> listar(String cpf) throws SQLException {
        if (cpf == null || cpf.isBlank()) {
            return missaoDAO.listarTodas();
        }
        return missaoDAO.listarPorUsuario(cpf.replaceAll("\\D", ""));
    }

    public Missao buscarPorId(int id) throws SQLException {
        Missao missao = missaoDAO.buscarPorId(id);
        if (missao == null) {
            throw ApiException.naoEncontrado("Missão não encontrada.");
        }
        return missao;
    }

    /** Atualiza título, descrição, pontos e categoria. Missão já concluída não pode ser alterada. */
    public Missao atualizar(int id, Missao dados) throws SQLException {
        Missao atual = buscarPorId(id);
        if (dados == null) {
            throw ApiException.invalido("Informe os dados da missão.");
        }
        if (!atual.estaDisponivel()) {
            throw ApiException.conflito("Missão já concluída não pode ser alterada.");
        }
        atual.setTitulo(dados.getTitulo());
        atual.setDescricao(dados.getDescricao());
        atual.setPontos(dados.getPontos());
        atual.setCategoria(dados.getCategoria());
        atual.normalizar();
        List<String> erros = atual.errosDeCadastro();
        if (!erros.isEmpty()) {
            throw ApiException.invalido(String.join(" ", erros));
        }
        missaoDAO.atualizar(atual);
        return atual;
    }

    /**
     * Conclui a missão (só pode ser concluída uma vez) e soma os pontos ao saldo do usuário.
     * As duas gravações acontecem na mesma transação: ou as duas valem, ou nenhuma.
     */
    public ConclusaoMissaoResponse concluir(int id) throws SQLException {
        Missao missao = buscarPorId(id);
        Usuario usuario = usuarioDAO.buscarPorCpf(missao.getCpfUsuario());
        if (usuario == null) {
            throw ApiException.naoEncontrado("Usuário dono da missão não encontrado.");
        }
        if (!missao.concluirMissao()) {
            throw ApiException.conflito("Essa missão já havia sido concluída.");
        }
        usuario.adicionarPontos(missao.getPontos());

        try (Connection conexao = ConnectionFactory.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                missaoDAO.atualizar(conexao, missao);
                usuarioDAO.atualizar(conexao, usuario);
                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        }
        return new ConclusaoMissaoResponse(missao, usuario.getPontos());
    }

    public void deletar(int id) throws SQLException {
        buscarPorId(id);
        missaoDAO.deletar(id);
    }
}
