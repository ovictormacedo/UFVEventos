package com.example.vma.ufveventos.controller;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Evento;
import com.example.vma.ufveventos.model.RecyclerViewEventosTelaInicialAdapter;
import com.example.vma.ufveventos.model.UsuarioSingleton;
import com.example.vma.ufveventos.util.RetrofitAPI;

import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class inicial extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView myRecyclerView;
    private RecyclerViewEventosTelaInicialAdapter adapter;
    private List<Evento> eventos;
    private int offset,limit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        eventos = new ArrayList<>();
        myRecyclerView = (RecyclerView) findViewById(R.id.lista_eventos);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewEventosTelaInicialAdapter(getBaseContext(),eventos);
        myRecyclerView.setAdapter(adapter);
        adapter.setOnEventoTelaInicialClickListener(new OnEventoTelaInicialClickListener() {
            @Override
            public void onItemClick(Evento item) {
                Toast.makeText(inicial.this, item.getDenominacao(), Toast.LENGTH_LONG).show();
            }
        });

        //Cria objeto para acessar a API de dados Siseventos
        RetrofitAPI retrofit = new RetrofitAPI();
        final Api api = retrofit.retrofit().create(Api.class);

        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarTelaInicial);
        progressBar.setProgress(0);

        offset = 100;
        limit = 110;
        Observable<List<Evento>> observable = api.getEventos(offset,limit);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Evento>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                        Log.i("Retrofit error", "Erro:" + e.getMessage());
                        Toast.makeText(getBaseContext(), "Não foi possível carregar os eventos.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Evento> response) {
                        //Copia resultados para a lista de eventos
                        for (int i = 0; i < response.size(); i++)
                            eventos.add(response.get(i));
                        //Atualiza RecyclerView
                        adapter.notifyDataSetChanged();
                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                    }
                });

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) myRecyclerView
                    .getLayoutManager();

            myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy > 0){ //Verifica se o scroll foi pra baixo
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int totalItemCount = linearLayoutManager.getItemCount();
                        int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                        if (progressBar.getVisibility() != View.VISIBLE &&
                                (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            //Carrega mais 5 itens
                            //Mostra barra de carregamento
                            progressBar.setVisibility(View.VISIBLE);
                            //Atualiza offset e limit, ou seja, busca mais 10 eventos
                            offset = limit+1;
                            limit += 10;
                            Observable<List<Evento>> observable = api.getEventos(offset,limit);
                            observable.subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<List<Evento>>() {
                                        @Override
                                        public void onCompleted() {
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            //Encerra barra de carregamento
                                            progressBar.setVisibility(View.GONE);
                                            Log.i("Retrofit error", "Erro:" + e.getMessage());
                                            Toast.makeText(getBaseContext(), "Não foi possível carregar os eventos.", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onNext(List<Evento> response) {
                                            //Copia resultados para a lista de eventos
                                            for (int i = 0; i < response.size(); i++)
                                                eventos.add(response.get(i));
                                            //Atualiza RecyclerView
                                            adapter.notifyDataSetChanged();
                                            //Encerra barra de carregamento
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                            }
                    }
                }
            });
    }

    public void escolher_categorias(View view){
        //Dispara intent para a tela de categorias
        Intent it = new Intent(getBaseContext(),categorias_pagina_inicial.class);
        startActivity(it);
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
        getMenuInflater().inflate(R.menu.inicial, menu);
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
            Intent it = new Intent(getBaseContext(),inicial.class);
            startActivity(it);
        } else if (id == R.id.nav_editar_perfil) {
            Intent it = new Intent(getBaseContext(),editar_perfil.class);
            startActivity(it);
        } else if (id == R.id.nav_notificacoes) {
            Intent it = new Intent(getBaseContext(),notificacoes.class);
            startActivity(it);
        } else if (id == R.id.nav_sair) {
            Intent it = new Intent(getBaseContext(),login.class);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
