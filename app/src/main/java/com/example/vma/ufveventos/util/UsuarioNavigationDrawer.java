package com.example.vma.ufveventos.util;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vma.ufveventos.R;

/**
 * Created by vma on 09/12/2017.
 */

public class UsuarioNavigationDrawer {
    public void setUsuarioImagem(NavigationView nv, String url){
        View hView =  nv.getHeaderView(0);
        ImageView img = (ImageView) hView.findViewById(R.id.imagemUsuario);
        if (url.isEmpty())
            img.setImageResource(R.drawable.user_icon);
        else{
        }
    }
    public void setNomeUsuario(NavigationView nv, String nome){
        View hView =  nv.getHeaderView(0);
        TextView tv = (TextView) hView.findViewById(R.id.nomeUsuario);
        tv.setText(nome);
    }
}
