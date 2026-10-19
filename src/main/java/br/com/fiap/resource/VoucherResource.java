package br.com.fiap.resource;

import br.com.fiap.bean.Voucher;
import br.com.fiap.bo.VoucherBO;
import br.com.fiap.dto.GerarVoucherRequest;
import br.com.fiap.dto.ResgateRequest;
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
@RequestMapping("/vouchers")
public class VoucherResource {

    private final VoucherBO voucherBO;

    public VoucherResource(VoucherBO voucherBO) {
        this.voucherBO = voucherBO;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Voucher gerar(@RequestBody GerarVoucherRequest request) throws SQLException {
        return voucherBO.gerar(request);
    }

    @GetMapping
    public List<Voucher> listar(@RequestParam(name = "cpf", required = false) String cpf) throws SQLException {
        return voucherBO.listar(cpf);
    }

    @GetMapping("/{id}")
    public Voucher buscarPorId(@PathVariable("id") int id) throws SQLException {
        return voucherBO.buscarPorId(id);
    }

    @GetMapping("/codigo/{codigo}")
    public Voucher buscarPorCodigo(@PathVariable("codigo") String codigo) throws SQLException {
        return voucherBO.buscarPorCodigo(codigo);
    }

    @PutMapping("/{id}")
    public Voucher atualizar(@PathVariable("id") int id, @RequestBody Voucher voucher) throws SQLException {
        return voucherBO.atualizar(id, voucher);
    }

    @PatchMapping("/{id}/resgatar")
    public Voucher resgatar(@PathVariable("id") int id, @RequestBody ResgateRequest request) throws SQLException {
        return voucherBO.resgatar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable("id") int id) throws SQLException {
        voucherBO.deletar(id);
    }
}
