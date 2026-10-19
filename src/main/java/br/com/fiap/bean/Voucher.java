package br.com.fiap.bean;

import java.time.LocalDate;
import java.util.Random;

public class Voucher {

    public static final String STATUS_DISPONIVEL = "DISPONIVEL";
    public static final String STATUS_UTILIZADO = "UTILIZADO";

    //atributos
    private int idVoucher;
    private String codigo;
    private String cpfUsuario;
    private String tipoBeneficio;
    private double valor;
    private String status;
    private LocalDate validade;

    //construtores
    public Voucher() {
        this.status = STATUS_DISPONIVEL;
    }

    public Voucher(String cpfUsuario, String tipoBeneficio, double valor, LocalDate validade) {
        this();
        this.cpfUsuario = cpfUsuario;
        this.tipoBeneficio = tipoBeneficio;
        this.valor = valor;
        this.validade = validade;
        this.codigo = novoCodigo();
    }

    //construtor usado ao carregar um voucher vindo do banco (DAO)
    public Voucher(int idVoucher, String codigo, String cpfUsuario, String tipoBeneficio,
                   double valor, String status, LocalDate validade) {
        this.idVoucher = idVoucher;
        this.codigo = codigo;
        this.cpfUsuario = cpfUsuario;
        this.tipoBeneficio = tipoBeneficio;
        this.valor = valor;
        this.status = status;
        this.validade = validade;
    }

    //getters e setters
    public int getIdVoucher() { return idVoucher; }

    public void setIdVoucher(int idVoucher) { this.idVoucher = idVoucher; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCpfUsuario() { return cpfUsuario; }

    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }

    public String getTipoBeneficio() { return tipoBeneficio; }

    public void setTipoBeneficio(String tipoBeneficio) { this.tipoBeneficio = tipoBeneficio; }

    public double getValor() { return valor; }

    public void setValor(double valor) { this.valor = valor; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDate getValidade() { return validade; }

    public void setValidade(LocalDate validade) { this.validade = validade; }

    //regras de negócio

    /** Gera um código no padrão ROCK + 6 dígitos aleatórios (ex.: ROCK483920). */
    public String gerarCodigo() {
        return novoCodigo();
    }

    private static String novoCodigo() {
        int numero = 100000 + new Random().nextInt(900000);
        return "ROCK" + numero;
    }

    public boolean estaDisponivel() {
        return STATUS_DISPONIVEL.equalsIgnoreCase(status);
    }

    public boolean expirado() {
        return validade != null && validade.isBefore(LocalDate.now());
    }

    public boolean pertenceA(String cpfInformado) {
        return cpfUsuario != null && cpfUsuario.equals(cpfInformado);
    }

    /** O voucher só pode ser usado se estiver disponível, dentro da validade e for do CPF informado. */
    public boolean validarVoucher(String cpfInformado) {
        return estaDisponivel() && !expirado() && pertenceA(cpfInformado);
    }

    /** Resgata o voucher (DISPONIVEL -> UTILIZADO). Retorna false se já foi utilizado. */
    public boolean resgatarVoucher() {
        if (!estaDisponivel()) {
            return false;
        }
        this.status = STATUS_UTILIZADO;
        return true;
    }

    public String mostrarVoucher() {
        return "Voucher: " + codigo
                + "\nBenefício: " + tipoBeneficio
                + "\nValor: " + valor
                + "\nStatus: " + status
                + "\nValidade: " + validade;
    }
}
