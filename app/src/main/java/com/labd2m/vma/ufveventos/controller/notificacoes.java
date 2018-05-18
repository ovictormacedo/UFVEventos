package com.labd2m.vma.ufveventos.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.labd2m.vma.ufveventos.R;
import com.labd2m.vma.ufveventos.model.Categoria;
import com.labd2m.vma.ufveventos.model.UsuarioSingleton;
import com.labd2m.vma.ufveventos.util.Permission;
import com.labd2m.vma.ufveventos.util.RetrofitAPI;
import com.labd2m.vma.ufveventos.util.UsuarioNavigationDrawer;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class notificacoes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView myRecyclerView;
    private RecyclerViewCategoriasAdapter adapter;
    private List<Categoria> categorias;
    UsuarioSingleton usuario = UsuarioSingleton.getInstance();
    GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private List<String> categorias_preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Google Analytics
        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("notificacoes");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        usuario = UsuarioSingleton.getInstance();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        categorias_preferencias = new ArrayList();

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

        //Seta dados do usuário no navigation drawer
        UsuarioNavigationDrawer und = new UsuarioNavigationDrawer();
        und.setNomeUsuario(navigationView,usuario.getNome());
        und.setUsuarioImagem(navigationView, usuario.getFoto());

        //Ajusta switchers
        boolean checkAgenda = (usuario.getAgenda().equals("1"))? true:false;
        boolean checkNotificacoes = (usuario.getNotificacoes().equals("0"))? false:true;
        ((Switch) findViewById(R.id.addAgenda)).setChecked(checkAgenda);
        ((Switch) findViewById(R.id.habilitarNotificacoes)).setChecked(checkNotificacoes);

        //Adiciona listener ao switcher de adicionar eventos à agenda
        Switch switch_button = (Switch) findViewById(R.id.addAgenda);
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getBaseContext().
                        getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if(isChecked) {
                    //Requsita permissão para utilizar a agenda
                    Permission permission = new Permission();
                    permission.requestPermissionCalendar(notificacoes.this,getBaseContext());

                    //Ajusta recebimento de notificações
                    ((Switch) findViewById(R.id.habilitarNotificacoes)).setChecked(true);

                    editor.putString("agenda", "1");
                    usuario.setAgenda("1");
                }
                else {
                    editor.putString("agenda", "0");
                    usuario.setAgenda("0");
                }
                editor.commit();

                //Envia alteração ao servidor
                //Cria objeto para acessar a API de dados Siseventos
                RetrofitAPI retrofit = new RetrofitAPI();
                Api api = retrofit.retrofit().create(Api.class);

                String valor = sharedPref.getString("agenda","0");

                //Recupera o firebase token do dispositivo
                sharedPref = getBaseContext().
                        getSharedPreferences("UFVEVENTOS"+usuario.getId(), Context.MODE_PRIVATE);
                String token = sharedPref.getString("firebasetoken","default");

                //Cria json object
                JSONObject json = new JSONObject();
                try {
                    json.put("agenda",valor);
                }catch(Exception e){Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();};

                //Inicia barra de carregamento
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCategorias);
                progressBar.setVisibility(View.VISIBLE);

                //Faz requisição ao servidor
                Observable<Void> observable =  api.updateAgenda(json);
                //Intercepta a resposta da requisição
                observable.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Void>(){
                            @Override
                            public void onCompleted(){}

                            @Override
                            public void onError(Throwable e){
                                //Esconde barra de carregamento
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getBaseContext(),"Não foi possível atualizar o configurações, " +
                                        "tente novamente em instantes.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNext(Void response){
                                //Esconde barra de carregamento
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getBaseContext(),"Dado atualizado!",Toast.LENGTH_SHORT).show();
                                //finish();
                            }
                        });
            }
        });

        //Adiciona listener ao switcher de ativar notificações
        switch_button = (Switch) findViewById(R.id.habilitarNotificacoes);
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getBaseContext().
                        getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if(isChecked) {
                    editor.putString("notificacoes", "1");
                    usuario.setNotificacoes("1");
                }
                else {
                    //Ajusta recebimento de notificações
                    ((Switch) findViewById(R.id.addAgenda)).setChecked(false);

                    editor.putString("notificacoes", "0");
                    usuario.setNotificacoes("0");
                }
                editor.commit();

                //Envia alteração ao servidor
                //Cria objeto para acessar a API de dados Siseventos
                RetrofitAPI retrofit = new RetrofitAPI();
                Api api = retrofit.retrofit().create(Api.class);

                String valor = sharedPref.getString("notificacoes","1");

                //Recupera o firebase token do dispositivo
                sharedPref = getBaseContext().
                        getSharedPreferences("UFVEVENTOS"+usuario.getId(), Context.MODE_PRIVATE);
                String token = sharedPref.getString("firebasetoken","default");

                //Cria json object
                JSONObject json = new JSONObject();
                try {
                    json.put("notificacoes",valor);
                    json.put("token",token);
                    json.put("idUsuario",usuario.getId());
                }catch(Exception e){Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();};

                //Inicia barra de carregamento
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCategorias);
                progressBar.setVisibility(View.VISIBLE);

                Log.i("NOTIFICACOES",""+json+" - "+usuario.getId()+" - "+token);
                //Faz requisição ao servidor
                //Observable<Void> observable =  api.updateNotificacoes(json,usuario.getId(),token);
                Observable<Void> observable =  api.updateNotificacoes(json);
                //Intercepta a resposta da requisição
                observable.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Void>(){
                            @Override
                            public void onCompleted(){}

                            @Override
                            public void onError(Throwable e){
                                //Esconde barra de carregamento
                                Log.e("ERRO NOTIFICACOES",e.getMessage());
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getBaseContext(),"Não foi possível atualizar o configurações, " +
                                        "tente novamente em instantes.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNext(Void response){
                                //Esconde barra de carregamento
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getBaseContext(),"Dado atualizado!",Toast.LENGTH_SHORT).show();
                                //finish();
                            }
                        });
            }
        });

        categorias = new ArrayList<>();
        myRecyclerView = (RecyclerView) findViewById(R.id.lista_categorias);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewCategoriasAdapter(getBaseContext(),categorias, categorias_preferencias);
        myRecyclerView.setAdapter(adapter);
        adapter.setCategoriaClickListener(new OnCategoriaClickListener() {
            @Override
            public void onItemClick(Categoria item) {
                //Toast.makeText(notificacoes.this, item.getNome(), Toast.LENGTH_LONG).show();
            }
        });

        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCategorias);
        progressBar.setVisibility(View.VISIBLE);

        //Cria objeto para acessar a API de dados Siseventos
        RetrofitAPI retrofit = new RetrofitAPI();
        final Api api = retrofit.retrofit().create(Api.class);

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
                        //Copia resultados para a lista de categorias
                        for (int i = 0; i < response.size(); i++)
                            categorias.add(response.get(i));
                        Observable<List<Categoria>> observable = api.getPreferenciasDeNotificacoes(usuario.getId());
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
                                        Toast.makeText(getBaseContext(), "Por favor, verifique a sua conexão com a internet.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(final List<Categoria> response) {
                                        categorias_preferencias.clear();
                                        for (int j = 0; j < response.size(); j++)
                                            categorias_preferencias.add(""+response.get(j).getId());
                                        //Atualiza RecyclerView
                                        adapter.notifyDataSetChanged();
                                        //Encerra barra de carregamento
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
    }

    public void salvar_configuracoes(View view){
        JSONArray json = new JSONArray(categorias_preferencias);
        String aux = json.toString();
        String data = "{\"categorias\":"+aux+"}";
        Log.i("CAT PREF ",data);

        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCategorias);
        progressBar.setVisibility(View.VISIBLE);

        //Cria objeto para acessar a API de dados Siseventos
        RetrofitAPI retrofit = new RetrofitAPI();
        final Api api = retrofit.retrofit().create(Api.class);

        Observable<Void> observable = api.updatePreferenciasNotificacoes(data,usuario.getId());
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                        Log.i("Retrofit error", "Erro:" + e.getMessage());
                        Toast.makeText(getBaseContext(), "Não foi possível atualizar as preferências.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Void response) {
                        Toast.makeText(getBaseContext(), "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
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
        //getMenuInflater().inflate(R.menu.notificacoes, menu);
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
