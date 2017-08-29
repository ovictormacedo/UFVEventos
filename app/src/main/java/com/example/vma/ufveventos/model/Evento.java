package com.example.vma.ufveventos.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vma on 21/07/2017.
 */

public class Evento {
    private int id;
    private String denominacao;
    private String horainicio;
    private String horafim;
    private String datainicio;
    private String datafim;
    private int participantes;
    private String publico;
    @SerializedName("categorias")
    @Expose
    private List<Categoria> categorias;
    @SerializedName("locais")
    @Expose
    private List<Local> locais;
    @SerializedName("servicos")
    @Expose
    private List<Servico> servicos;

    public Evento(int id, String denominacao, String horainicio,
                  String horafim, String datainicio, String datafim, int participantes, String publico,
                  List<Categoria> categorias, List<Local> locais, List<Servico> servicos) {
        this.id = id;
        this.participantes = participantes;
        this.denominacao = denominacao;
        this.horainicio = horainicio;
        this.horafim = horafim;
        this.datainicio = datainicio;
        this.datafim = datafim;
        this.publico = publico;
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
        return participantes;
    }

    public void setNumeroParticipantes(int participantes) {
        this.participantes = participantes;
    }

    public String getDenominacao() {
        return denominacao;
    }

    public void setDenominacao(String denominacao) {
        this.denominacao = denominacao;
    }

    public String getHoraInicio() {
        return horainicio;
    }

    public void setHoraInicio(String horainicio) {
        this.horainicio = horainicio;
    }

    public String getHoraFim() {
        return horafim;
    }

    public void setHoraFim(String horafim) {
        this.horafim = horafim;
    }

    public String getDataInicio() {
        return datainicio;
    }

    public void setDataInicio(String datainicio) {
        this.datainicio = datainicio;
    }

    public String getDataFim() {
        return datafim;
    }

    public void setDataFim(String dataFim) {
        this.datafim = datafim;
    }

    public String getPublicoAlvo() {
        return publico;
    }

    public void setPublicoAlvo(String publico) {
        this.publico = publico;
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