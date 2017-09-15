package com.example.vma.ufveventos.model;

/**
 * Created by vma on 07/09/2017.
 */

public class UsuarioSingleton {
    private static final UsuarioSingleton ourInstance = new UsuarioSingleton();

    private int id;
    private String nome, email, senha, nascimento="", sexo, matricula;

    public static UsuarioSingleton getInstance() {
        return ourInstance;
    }

    private UsuarioSingleton() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
