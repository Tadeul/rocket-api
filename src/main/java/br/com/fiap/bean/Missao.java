package br.com.fiap.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Missao {

    public static final String STATUS_DISPONIVEL = "DISPONIVEL";
    public static final String STATUS_CONCLUIDA = "CONCLUIDA";
    public static final Set<String> CATEGORIAS = Set.of("SUSTENTABILIDADE", "ENGAJAMENTO", "MOBILIDADE");

    //atributos
    private int idMissao;
    private String titulo;
    private String descricao;
    private int pontos;
    private String categoria;
    private String status;
    private String cpfUsuario;

    //construtores
    public Missao() {
        this.status = STATUS_DISPONIVEL;
    }

    public Missao(String titulo, String descricao, int pontos, String categoria, String cpfUsuario) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.pontos = pontos;
        this.categoria = categoria;
        this.cpfUsuario = cpfUsuario;
    }

    //construtor usado ao carregar uma missão vinda do banco (DAO)
    public Missao(int idMissao, String titulo, String descricao, int pontos, String categoria,
                  String status, String cpfUsuario) {
        this.idMissao = idMissao;
        this.titulo = titulo;
        this.descricao = descricao;
        this.pontos = pontos;
        this.categoria = categoria;
        this.status = status;
        this.cpfUsuario = cpfUsuario;
    }

    //getters e setters
    public int getIdMissao() { return idMissao; }

    public void setIdMissao(int idMissao) { this.idMissao = idMissao; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getPontos() { return pontos; }

    public void setPontos(int pontos) { this.pontos = pontos; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getCpfUsuario() { return cpfUsuario; }

    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }

    //regras de negócio

    /** Remove espaços, padroniza a categoria em maiúsculas e deixa o CPF só com dígitos. */
    public void normalizar() {
        if (titulo != null) titulo = titulo.trim();
        if (descricao != null) descricao = descricao.trim();
        if (categoria != null) categoria = categoria.trim().toUpperCase();
        if (cpfUsuario != null) cpfUsuario = cpfUsuario.replaceAll("\\D", "");
    }

    /** Lista os problemas do cadastro da missão (lista vazia = válida). */
    public List<String> errosDeCadastro() {
        List<String> erros = new ArrayList<>();
        if (titulo == null || titulo.isEmpty()) {
            erros.add("O título é obrigatório.");
        } else if (titulo.length() > 150) {
            erros.add("O título deve ter no máximo 150 caracteres.");
        }
        if (descricao != null && descricao.length() > 255) {
            erros.add("A descrição deve ter no máximo 255 caracteres.");
        }
        if (pontos < 1 || pontos > 1000) {
            erros.add("Os pontos da missão devem estar entre 1 e 1000.");
        }
        if (categoria == null || !CATEGORIAS.contains(categoria)) {
            erros.add("A categoria deve ser Sustentabilidade, Engajamento ou Mobilidade.");
        }
        if (cpfUsuario == null || !cpfUsuario.matches("\\d{11}")) {
            erros.add("O CPF do usuário deve conter exatamente 11 dígitos numéricos.");
        }
        return erros;
    }

    public boolean estaDisponivel() {
        return STATUS_DISPONIVEL.equalsIgnoreCase(status);
    }

    /**
     * Conclui a missão caso ainda esteja disponível.
     * Retorna false se ela já havia sido concluída (só pode ser concluída uma vez).
     */
    public boolean concluirMissao() {
        if (!estaDisponivel()) {
            return false;
        }
        this.status = STATUS_CONCLUIDA;
        return true;
    }

    public String mostrarMissao() {
        return "Missão: " + titulo
                + "\nDescrição: " + descricao
                + "\nCategoria: " + categoria
                + "\nPontos: " + pontos
                + "\nStatus: " + status;
    }
}
