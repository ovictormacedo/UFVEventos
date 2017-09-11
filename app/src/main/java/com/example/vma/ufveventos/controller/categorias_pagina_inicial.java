package com.example.vma.ufveventos.controller;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Categoria;
import com.example.vma.ufveventos.model.Evento;
import com.example.vma.ufveventos.model.RecyclerViewCategoriasAdapter;
import com.example.vma.ufveventos.util.RetrofitAPI;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class categorias_pagina_inicial extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView myRecyclerView;
    private RecyclerViewCategoriasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_pagina_inicial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myRecyclerView = (RecyclerView) findViewById(R.id.lista_categorias);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCategorias);
        progressBar.setProgress(0);

        //Cria objeto para acessar a API de dados Siseventos
        RetrofitAPI retrofit = new RetrofitAPI();
        Api api = retrofit.retrofit().create(Api.class);

        Observable<List<Categoria>> observable = api.getCategorias();
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Categoria>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                        Log.i("Retrofit error", "Erro:" + e.getMessage());
                        Toast.makeText(getBaseContext(), "Não foi possível carregar as categorias.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Categoria> response) {
                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                        adapter = new RecyclerViewCategoriasAdapter(getBaseContext(),response);
                        myRecyclerView.setAdapter(adapter);

                        adapter.setCategoriaClickListener(new OnEventoTelaInicialClickListener() {
                            @Override
                            public void onItemClick(Evento item) {
                                Toast.makeText(categorias_pagina_inicial.this, item.getDenominacao(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.categorias_pagina_inicial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {

        } else if (id == R.id.nav_editar_perfil) {

        } else if (id == R.id.nav_notificacoes) {

        } else if (id == R.id.nav_sair) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
