package br.com.fiap.resource;

import br.com.fiap.bean.Usuario;
import br.com.fiap.bo.UsuarioBO;
import br.com.fiap.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

    private final UsuarioBO usuarioBO;

    public UsuarioResource(UsuarioBO usuarioBO) {
        this.usuarioBO = usuarioBO;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario cadastrar(@RequestBody Usuario usuario) throws SQLException {
        return usuarioBO.cadastrar(usuario);
    }

    @GetMapping
    public List<Usuario> listar() throws SQLException {
        return usuarioBO.listar();
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable("id") int id) throws SQLException {
        return usuarioBO.buscarPorId(id);
    }

    @GetMapping("/cpf/{cpf}")
    public Usuario buscarPorCpf(@PathVariable("cpf") String cpf) throws SQLException {
        return usuarioBO.buscarPorCpf(cpf);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable("id") int id, @RequestBody Usuario usuario) throws SQLException {
        return usuarioBO.atualizar(id, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable("id") int id) throws SQLException {
        usuarioBO.deletar(id);
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest request) throws SQLException {
        return usuarioBO.login(request);
    }
}
