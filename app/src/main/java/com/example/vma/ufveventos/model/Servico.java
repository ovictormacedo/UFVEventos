package com.example.vma.ufveventos.model;

/**
 * Created by vma on 21/07/2017.
 */

public class Servico {
    private int id;
    private String nome;

    public Servico(int id, String nome) {
        this.id = id;
        this.nome = nome;
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
}