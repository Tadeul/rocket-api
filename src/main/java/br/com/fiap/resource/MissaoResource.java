package br.com.fiap.resource;

import br.com.fiap.bean.Missao;
import br.com.fiap.bo.MissaoBO;
import br.com.fiap.dto.ConclusaoMissaoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/missoes")
public class MissaoResource {

    private final MissaoBO missaoBO;

    public MissaoResource(MissaoBO missaoBO) {
        this.missaoBO = missaoBO;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Missao cadastrar(@RequestBody Missao missao) throws SQLException {
        return missaoBO.cadastrar(missao);
    }

    @GetMapping
    public List<Missao> listar(@RequestParam(name = "cpf", required = false) String cpf) throws SQLException {
        return missaoBO.listar(cpf);
    }

    @GetMapping("/{id}")
    public Missao buscarPorId(@PathVariable("id") int id) throws SQLException {
        return missaoBO.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Missao atualizar(@PathVariable("id") int id, @RequestBody Missao missao) throws SQLException {
        return missaoBO.atualizar(id, missao);
    }

    @PatchMapping("/{id}/concluir")
    public ConclusaoMissaoResponse concluir(@PathVariable("id") int id) throws SQLException {
        return missaoBO.concluir(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable("id") int id) throws SQLException {
        missaoBO.deletar(id);
    }
}
