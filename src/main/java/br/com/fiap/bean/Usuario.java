package br.com.fiap.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Usuario {

    public static final String STATUS_ATIVO = "ATIVO";
    public static final String STATUS_INATIVO = "INATIVO";

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    //atributos
    private int idUsuario;
    private String nome;
    private String cpf;
    private String email;
    // a senha pode ser recebida no JSON, mas nunca é devolvida nas respostas
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;
    private int pontos;
    private String status;

    //construtores
    // sem valores padrão de propósito: ao receber um JSON sem "status", o campo fica nulo
    // e o BO sabe que o cliente não pediu para alterá-lo
    public Usuario() {
    }

    public Usuario(String nome, String cpf, String email, String senha) {
        this.status = STATUS_ATIVO;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

    //construtor usado ao carregar um usuário vindo do banco (DAO)
    public Usuario(int idUsuario, String nome, String cpf, String email, String senha, int pontos, String status) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.pontos = pontos;
        this.status = status;
    }

    //getters e setters
    public int getIdUsuario() { return idUsuario; }

    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }

    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }

    public void setSenha(String senha) { this.senha = senha; }

    public int getPontos() { return pontos; }

    public void setPontos(int pontos) { this.pontos = pontos; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    //regras de negócio

    /** Remove espaços e máscara: nome/e-mail sem espaços nas pontas e CPF só com dígitos. */
    public void normalizar() {
        if (nome != null) nome = nome.trim();
        if (email != null) email = email.trim();
        if (cpf != null) cpf = cpf.replaceAll("\\D", "");
        if (status != null) status = status.trim().toUpperCase();
    }

    /** Lista os problemas do cadastro (lista vazia = cadastro válido). */
    public List<String> errosDeCadastro() {
        return errosDeCadastro(true);
    }

    /** Igual ao anterior, mas permite ignorar a senha (na edição ela é opcional e já está guardada em hash). */
    public List<String> errosDeCadastro(boolean validarSenha) {
        List<String> erros = new ArrayList<>();
        if (nome == null || nome.trim().isEmpty()) {
            erros.add("O nome é obrigatório.");
        } else if (nome.trim().length() > 100) {
            erros.add("O nome deve ter no máximo 100 caracteres.");
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            erros.add("O CPF deve conter exatamente 11 dígitos numéricos.");
        }
        if (email == null || email.trim().isEmpty()) {
            erros.add("O e-mail é obrigatório.");
        } else if (email.trim().length() > 150 || !EMAIL.matcher(email.trim()).matches()) {
            erros.add("O e-mail informado é inválido.");
        }
        if (validarSenha && (senha == null || senha.length() < 6)) {
            erros.add("A senha deve ter no mínimo 6 caracteres.");
        }
        return erros;
    }

    /** Valida se as informações do cadastro estão preenchidas corretamente. */
    public boolean validarCadastro() {
        return errosDeCadastro().isEmpty();
    }

    /** Soma pontos ao saldo (usado ao concluir missões). Não aceita valores menores ou iguais a zero. */
    public void adicionarPontos(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de pontos deve ser positiva.");
        }
        this.pontos += quantidade;
    }

    public boolean possuiPontos(int quantidade) {
        return this.pontos >= quantidade;
    }

    /** Desconta pontos do saldo (usado ao gerar vouchers). Não permite saldo negativo. */
    public void debitarPontos(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de pontos deve ser positiva.");
        }
        if (!possuiPontos(quantidade)) {
            throw new IllegalArgumentException("Saldo de pontos insuficiente.");
        }
        this.pontos -= quantidade;
    }

    public String mostrarUsuario() {
        return "Usuário: " + nome
                + "\nCPF: " + cpf
                + "\nEmail: " + email
                + "\nPontos: " + pontos
                + "\nStatus: " + status;
    }
}
