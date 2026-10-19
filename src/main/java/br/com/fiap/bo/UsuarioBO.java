package br.com.fiap.bo;

import br.com.fiap.bean.Usuario;
import br.com.fiap.dao.MissaoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dao.VoucherDAO;
import br.com.fiap.dto.LoginRequest;
import br.com.fiap.exception.ApiException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO;
    private final MissaoDAO missaoDAO;
    private final VoucherDAO voucherDAO;

    public UsuarioBO(UsuarioDAO usuarioDAO, MissaoDAO missaoDAO, VoucherDAO voucherDAO) {
        this.usuarioDAO = usuarioDAO;
        this.missaoDAO = missaoDAO;
        this.voucherDAO = voucherDAO;
    }

    /** Cadastra um usuário: valida os dados, impede CPF duplicado e grava com 0 pontos e status ATIVO. */
    public Usuario cadastrar(Usuario usuario) throws SQLException {
        if (usuario == null) {
            throw ApiException.invalido("Informe os dados do usuário.");
        }
        usuario.normalizar();
        List<String> erros = usuario.errosDeCadastro();
        if (!erros.isEmpty()) {
            throw ApiException.invalido(String.join(" ", erros));
        }
        if (usuarioDAO.buscarPorCpf(usuario.getCpf()) != null) {
            throw ApiException.conflito("Já existe um usuário cadastrado com este CPF.");
        }
        usuario.setSenha(SenhaUtil.hash(usuario.getSenha()));
        usuario.setPontos(0);
        usuario.setStatus(Usuario.STATUS_ATIVO);
        return usuarioDAO.inserir(usuario);
    }

    public List<Usuario> listar() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(int id) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw ApiException.naoEncontrado("Usuário não encontrado.");
        }
        return usuario;
    }

    public Usuario buscarPorCpf(String cpf) throws SQLException {
        String cpfLimpo = cpf == null ? "" : cpf.replaceAll("\\D", "");
        Usuario usuario = usuarioDAO.buscarPorCpf(cpfLimpo);
        if (usuario == null) {
            throw ApiException.naoEncontrado("Usuário não encontrado para o CPF informado.");
        }
        return usuario;
    }

    /**
     * Atualiza nome, e-mail, status e (opcionalmente) a senha.
     * O CPF e os pontos não podem ser alterados por aqui: o CPF é a chave de ligação com
     * missões e vouchers, e os pontos só mudam concluindo missões ou gerando vouchers.
     */
    public Usuario atualizar(int id, Usuario dados) throws SQLException {
        Usuario atual = buscarPorId(id);
        if (dados == null) {
            throw ApiException.invalido("Informe os dados do usuário.");
        }
        dados.normalizar();
        if (dados.getCpf() != null && !dados.getCpf().isEmpty() && !dados.getCpf().equals(atual.getCpf())) {
            throw ApiException.invalido("O CPF não pode ser alterado.");
        }
        atual.setNome(dados.getNome());
        atual.setEmail(dados.getEmail());
        if (dados.getStatus() != null && !dados.getStatus().isEmpty()) {
            if (!Usuario.STATUS_ATIVO.equals(dados.getStatus()) && !Usuario.STATUS_INATIVO.equals(dados.getStatus())) {
                throw ApiException.invalido("O status deve ser ATIVO ou INATIVO.");
            }
            atual.setStatus(dados.getStatus());
        }
        List<String> erros = atual.errosDeCadastro(false);
        if (!erros.isEmpty()) {
            throw ApiException.invalido(String.join(" ", erros));
        }
        if (dados.getSenha() != null && !dados.getSenha().isEmpty()) {
            if (dados.getSenha().length() < 6) {
                throw ApiException.invalido("A senha deve ter no mínimo 6 caracteres.");
            }
            atual.setSenha(SenhaUtil.hash(dados.getSenha()));
        }
        usuarioDAO.atualizar(atual);
        return atual;
    }

    /** Exclui o usuário, desde que ele não tenha missões ou vouchers vinculados. */
    public void deletar(int id) throws SQLException {
        Usuario usuario = buscarPorId(id);
        boolean temMissoes = !missaoDAO.listarPorUsuario(usuario.getCpf()).isEmpty();
        boolean temVouchers = !voucherDAO.listarPorUsuario(usuario.getCpf()).isEmpty();
        if (temMissoes || temVouchers) {
            throw ApiException.conflito("Não é possível excluir: o usuário possui missões ou vouchers vinculados.");
        }
        usuarioDAO.deletar(id);
    }

    /** Autentica por CPF e senha. */
    public Usuario login(LoginRequest request) throws SQLException {
        if (request == null || request.cpf() == null || request.senha() == null) {
            throw ApiException.invalido("Informe CPF e senha.");
        }
        Usuario usuario = usuarioDAO.buscarPorCpf(request.cpf().replaceAll("\\D", ""));
        if (usuario == null || !SenhaUtil.hash(request.senha()).equals(usuario.getSenha())) {
            throw ApiException.naoAutorizado("CPF ou senha inválidos.");
        }
        return usuario;
    }
}
