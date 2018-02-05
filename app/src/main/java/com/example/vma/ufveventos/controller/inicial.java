package com.example.vma.ufveventos.controller;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.vma.ufveventos.model.EventosSingleton;
import com.example.vma.ufveventos.model.RecyclerViewEventosTelaInicialAdapter;
import com.example.vma.ufveventos.model.UsuarioSingleton;
import com.example.vma.ufveventos.util.Permission;
import com.example.vma.ufveventos.util.RetrofitAPI;
import com.example.vma.ufveventos.util.UsuarioNavigationDrawer;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class inicial extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView myRecyclerView;
    private RecyclerViewEventosTelaInicialAdapter adapter;
    private List<Evento> eventos;
    private int offset,limit;
    UsuarioSingleton usuario = UsuarioSingleton.getInstance();
    private RetrofitAPI retrofit;
    EventosSingleton eventosSing = EventosSingleton.getInstance();
    GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onResume(){
        super.onResume();
        //Seta dados do usuário no navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        UsuarioNavigationDrawer und = new UsuarioNavigationDrawer();
        und.setNomeUsuario(navigationView,usuario.getNome());
        und.setUsuarioImagem(navigationView, usuario.getFoto());
    }

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

        //Google Analytics
        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("inicial");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //Seta dados do usuário no navigation drawer
        UsuarioNavigationDrawer und = new UsuarioNavigationDrawer();
        und.setNomeUsuario(navigationView,usuario.getNome());
        und.setUsuarioImagem(navigationView, usuario.getFoto());

        eventos = new ArrayList<>();
        myRecyclerView = (RecyclerView) findViewById(R.id.lista_eventos);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewEventosTelaInicialAdapter(getBaseContext(),eventos);
        myRecyclerView.setAdapter(adapter);
        adapter.setOnEventoTelaInicialClickListener(new OnEventoTelaInicialClickListener() {
            @Override
            public void onItemClick(Evento item) {
                Intent it;
                //Verifica se o evento possui descrição ou programação
                if (item.getDescricao_evento() == "" && item.getProgramacao_evento() == "")
                    it = new Intent(getBaseContext(),detalhes_evento_sem_descricao.class);
                else
                    it = new Intent(getBaseContext(),detalhes_evento_com_descricao.class);

                Gson gson = new Gson();
                String json = gson.toJson(item);
                it.putExtra("evento", json);
                startActivity(it);
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("224128381554-g15qnhlokg544p5746fv9q5tg1b0c1aa.apps.googleusercontent.com")
                .requestProfile()
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Start initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        //Cria objeto para acessar a API de dados Siseventos
        retrofit = new RetrofitAPI();
        final Api api = retrofit.retrofit().create(Api.class);

        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarTelaInicial);
        progressBar.setProgress(View.VISIBLE);

        //Verifica se a lista possui algum evento
        if (eventosSing.tamanho() == 0) {
            offset = 100;
            limit = 110;
            Observable<List<Evento>> observable = api.getEventos(offset, limit);
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
                            for (int i = 0; i < response.size(); i++) {
                                eventos.add(response.get(i));
                                eventosSing.addEvento(response.get(i));
                            }
                            //Atualiza RecyclerView
                            adapter.notifyDataSetChanged();
                            //Encerra barra de carregamento
                            progressBar.setVisibility(View.GONE);
                        }
                    });
            }else{
                //Copia singleton para a lista de eventos
                for (int i = 0; i < eventosSing.tamanho(); i++)
                    eventos.add(eventosSing.getEvento(i));

                //Atualiza RecyclerView
                adapter.notifyDataSetChanged();

                //Encerra barra de carregamento
                progressBar.setVisibility(View.GONE);
            }

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
                                            for (int i = 0; i < response.size(); i++) {
                                                eventos.add(response.get(i));
                                                eventosSing.addEvento(response.get(i));
                                            }
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
        //Requsita permissão para utilizar a agenda
        Permission permission = new Permission();
        permission.requestPermissionCalendar(getParent(),getBaseContext());
    }

    public void escolher_categorias(View view){
        //Dispara intent para a tela de categorias
        Intent it = new Intent(getBaseContext(),categorias_pagina_inicial.class);
        startActivityForResult(it,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Limpa lista de eventos
            eventos.clear();
            //Cria objeto para acessar a API de dados Siseventos
            RetrofitAPI retrofit = new RetrofitAPI();
            final Api api = retrofit.retrofit().create(Api.class);

            myRecyclerView = (RecyclerView) findViewById(R.id.lista_eventos);
            myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new RecyclerViewEventosTelaInicialAdapter(getBaseContext(),eventos);
            myRecyclerView.setAdapter(adapter);

            //Inicia barra de carregamento
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarTelaInicial);
            progressBar.setVisibility(View.VISIBLE);

            offset = 100;
            limit = 110;
            Observable<List<Evento>> observable = api.getEventosPorUsuario(usuario.getId(),offset,limit);
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
                            if (response.size() == 0)
                                Toast.makeText(getBaseContext(),"Não existem eventos no momento.",Toast.LENGTH_SHORT).show();

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
                            Observable<List<Evento>> observable = api.getEventosPorUsuario(usuario.getId(),offset,limit);
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
                                            for (int i = 0; i < response.size(); i++) {
                                                eventos.add(response.get(i));
                                                eventosSing.addEvento(response.get(i));
                                            }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            Intent it = new Intent(getBaseContext(),inicial.class);
            startActivity(it);
        }
        else if(id == R.id.nav_sobre){
            Intent it = new Intent(getBaseContext(), sobre.class);
            startActivity(it);
        }
        else if (id == R.id.nav_editar_perfil) {
            Intent it;
            //Se não é um usuário logado com a conta Google pode editar o perfil
            if (usuario.getGoogleId().equals("default") || usuario.getGoogleId().equals("") ){
                it = new Intent(getBaseContext(), editar_perfil.class);
                startActivity(it);
            }
            else{
                Toast.makeText(getBaseContext(),"Funcionalidade indisponível para usuários logado com conta Google.",Toast.LENGTH_LONG)
                        .show();
            }
        } else if (id == R.id.nav_notificacoes) {
            Intent it = new Intent(getBaseContext(),notificacoes.class);
            startActivity(it);
        } else if (id == R.id.nav_sair) {
            //Registra que o usuário saiu
            SharedPreferences sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            editor.putBoolean("logado",false);
            editor.commit();

            // Firebase sign out
            mAuth.signOut();

            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

            Intent it = new Intent(getBaseContext(),login.class);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
