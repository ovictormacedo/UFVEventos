package com.example.vma.ufveventos.model;

/**
 * Created by vma on 21/07/2017.
 */

public class Usuario {
    private String id, nome, email, senha, nascimento, sexo, matricula, foto, googleId, notificacoes, agenda;

    public Usuario(String id, String nome, String email, String senha, String nascimento, String sexo, String matricula,
                   String googleId, String notificacoes, String agenda) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.nascimento = nascimento;
        this.sexo = sexo;
        this.matricula = matricula;
        this.googleId = googleId;
        this.notificacoes = notificacoes;
        this.agenda = agenda;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(String notificacoes) {
        this.notificacoes = notificacoes;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
