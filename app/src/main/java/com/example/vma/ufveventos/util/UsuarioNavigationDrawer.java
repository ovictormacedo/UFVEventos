package com.example.vma.ufveventos.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vma.ufveventos.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by vma on 09/12/2017.
 */

public class UsuarioNavigationDrawer {
    public void setUsuarioImagem(NavigationView nv, final String imgUrl){
        View hView =  nv.getHeaderView(0);
        final CircleImageView img = (CircleImageView) hView.findViewById(R.id.imagemUsuario);

        if (imgUrl.equals("") || imgUrl.equals("default"))
            img.setImageResource(R.drawable.user_icon);
        else {
            //Recupera Bitmap
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(imgUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        img.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
        }
    }
    public void setNomeUsuario(NavigationView nv, String nome){
        View hView =  nv.getHeaderView(0);
        TextView tv = (TextView) hView.findViewById(R.id.nomeUsuario);
        tv.setText(nome);
    }
}