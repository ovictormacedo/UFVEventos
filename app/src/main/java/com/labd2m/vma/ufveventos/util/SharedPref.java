package com.labd2m.vma.ufveventos.util;

public class SharedPref {
    String agendaKey = "UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0agenda";
    String key = "UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0";
    public String getAgendaKey() {
        return agendaKey;
    }
    public String getKey(){
        return "UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0";
    }
    public String getUserKey(String id){
        return "UFVEVENTOS"+id;
    }
}
