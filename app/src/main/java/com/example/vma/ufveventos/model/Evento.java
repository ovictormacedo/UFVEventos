package com.example.vma.ufveventos.model;

import java.util.List;

/**
 * Created by vma on 21/07/2017.
 */

public class Evento {
    private int id, numeroParticipantes;
    private String denominacao, horaInicio, horaFim, dataInicio, dataFim, publicoAlvo;
    private List<Servico> servicos;
    private List<Categoria> categorias;
    private List<Local> locais;

    public Evento(int id, int numeroParticipantes, String denominacao, String horaInicio,
                  String horaFim, String dataInicio, String dataFim, String publicoAlvo,
                  List<Servico> servicos, List<Categoria> categorias, List<Local> locais) {
        this.id = id;
        this.numeroParticipantes = numeroParticipantes;
        this.denominacao = denominacao;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.publicoAlvo = publicoAlvo;
        this.servicos = servicos;
        this.categorias = categorias;
        this.locais = locais;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroParticipantes() {
        return numeroParticipantes;
    }

    public void setNumeroParticipantes(int numeroParticipantes) {
        this.numeroParticipantes = numeroParticipantes;
    }

    public String getDenominacao() {
        return denominacao;
    }

    public void setDenominacao(String denominacao) {
        this.denominacao = denominacao;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getPublicoAlvo() {
        return publicoAlvo;
    }

    public void setPublicoAlvo(String publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public List<Local> getLocais() {
        return locais;
    }

    public void setLocais(List<Local> locais) {
        this.locais = locais;
    }
}
