package com.example.vma.ufveventos.model;

/**
 * Created by vma on 21/07/2017.
 */

public class Local {
    private int id;
    private String descricao;
    private String lat; //latitude
    private String lng;  //latitude

    public Local(int id, String descricao, String lat, String lng) {
        this.id = id;
        this.descricao = descricao;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLatitude() {
        return lat;
    }

    public void setLatitude(String lat) {
        this.lat = lat;
    }

    public String getLongitude() {
        return lng;
    }

    public void setLongitude(String longitude) {
        this.lng = lng;
    }
}
