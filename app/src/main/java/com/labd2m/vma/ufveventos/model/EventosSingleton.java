package com.labd2m.vma.ufveventos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vma on 13/12/2017.
 */

public class EventosSingleton {
    private static final EventosSingleton ourInstance = new EventosSingleton();

    private List<Evento> eventos = new ArrayList<>();

    public static EventosSingleton getInstance() {
        return ourInstance;
    }

    private EventosSingleton() {
    }

    public void addEvento(Evento e){
        eventos.add(e);
    }

    public Evento getEvento(int i){
        return eventos.get(i);
    }

    public int tamanho(){
        return eventos.size();
    }
}
