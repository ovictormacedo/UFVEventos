package com.labd2m.vma.ufveventos.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vma on 21/07/2017.
 */

public class Evento {
    private int id;
    private String denominacao = null;
    private String horainicio = null;
    private String horafim = null;
    private String datainicio = null;
    private String datafim = null;
    private String descricao_evento = null;
    private String programacao_evento = null;
    private int participantes = 0;
    private String publico = null;
    private int teminscricao;
    private float valorinscricao;
    private String linklocalinscricao = null;
    private int mostrarparticipantes;

    @SerializedName("categorias")
    @Expose
    private List<Categoria> categorias = null;
    @SerializedName("locais")
    @Expose
    private List<Local> locais = null;
    @SerializedName("servicos")
    @Expose
    private List<Servico> servicos = null;

    public Evento(){}

    public Evento(int id, String denominacao, String horainicio, String horafim, String datainicio,
                  String datafim, String descricao_evento, String programacao_evento, int participantes,
                  String publico, List<Categoria> categorias, List<Local> locais, List<Servico> servicos,
                  int teminscricao, float valorinscricao, String linklocalinscricao, int mostrarparticipantes) {
        this.id = id;
        this.denominacao = denominacao;
        this.horainicio = horainicio;
        this.horafim = horafim;
        this.datainicio = datainicio;
        this.datafim = datafim;
        this.descricao_evento = descricao_evento;
        this.programacao_evento = programacao_evento;
        this.participantes = participantes;
        this.publico = publico;
        this.categorias = categorias;
        this.locais = locais;
        this.servicos = servicos;
        this.teminscricao = teminscricao;
        this.valorinscricao = valorinscricao;
        this.linklocalinscricao = linklocalinscricao;
        this.mostrarparticipantes = mostrarparticipantes;
    }

    public String getDescricao_evento() {
        return descricao_evento;
    }

    public void setDescricao_evento(String descricao_evento) {
        this.descricao_evento = descricao_evento;
    }

    public String getProgramacao_evento() {
        return programacao_evento;
    }

    public void setProgramacao_evento(String programacao_evento) {
        this.programacao_evento = programacao_evento;
    }

    public int getTeminscricao() {
        return teminscricao;
    }

    public void setTeminscricao(int teminscricao) {
        this.teminscricao = teminscricao;
    }

    public float getValorinscricao() {
        return valorinscricao;
    }

    public void setValorinscricao(float valorinscricao) {
        this.valorinscricao = valorinscricao;
    }

    public String getLinklocalinscricao() {
        return linklocalinscricao;
    }

    public void setLinklocalinscricao(String linklocalinscricao) {
        this.linklocalinscricao = linklocalinscricao;
    }

    public int getMostrarparticipantes() {
        return mostrarparticipantes;
    }

    public void setMostrarparticipantes(int mostrarparticipantes) {
        this.mostrarparticipantes = mostrarparticipantes;
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