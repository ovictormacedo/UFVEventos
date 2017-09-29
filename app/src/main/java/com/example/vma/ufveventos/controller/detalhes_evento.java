package com.example.vma.ufveventos.controller;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Categoria;
import com.example.vma.ufveventos.model.Evento;
import com.example.vma.ufveventos.model.Local;
import com.example.vma.ufveventos.model.Servico;
import com.example.vma.ufveventos.util.RetrofitAPI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class detalhes_evento extends AppCompatActivity implements OnMapReadyCallback {
    private RetrofitAPI retrofit;
    GoogleMap mGoogleMap;

    private void initMap(){
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mGoogleMap = googleMap;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_evento);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (googleServicesAvailable()){
            initMap();
        }

        //Cria objeto para acessar a API de dados Siseventos
        retrofit = new RetrofitAPI();
        final Api api = retrofit.retrofit().create(Api.class);

        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarDetalhesEvento);
        progressBar.setProgress(View.VISIBLE);

        Intent it = getIntent();
        int idEvento = Integer.parseInt(it.getStringExtra("idEvento"));

        Observable<Evento> observable = api.getEvento(idEvento);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Evento>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                        Log.i("Retrofit error", "Erro:" + e.getMessage());
                        Toast.makeText(getBaseContext(), "Não foi possível carregar o evento.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Evento response) {
                        //Seta interface com o resultado
                        //Seta denominação do evento
                        if (response.getDenominacao() != null){
                            ((TextView) findViewById(R.id.tituloEvento)).
                                    setText(response.getDenominacao());
                        }

                        //Seta hora de início e fim do evento
                        if (response.getHoraInicio() != null && response.getHoraFim() != null) {
                            String horaInicio = response.getHoraInicio().substring(0, 5);
                            String horaFim = response.getHoraFim().substring(0, 5);
                            findViewById(R.id.horarioLabelEvento).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.horarioEvento)).
                                    setText(horaInicio+" - "+horaFim);
                        }

                        //Seta data do evento
                        if (response.getDataInicio() != null && response.getDataFim() != null) {
                            String aux = response.getDataInicio();
                            String dataInicio = aux.substring(8, 10) + "/" + aux.substring(5, 7) + "/" + aux.substring(0, 4);
                            aux = response.getDataFim();
                            String dataFim = aux.substring(8, 10) + "/" + aux.substring(5, 7) + "/" + aux.substring(0, 4);
                            findViewById(R.id.dataLabelEvento).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.dataEvento)).
                                    setText(dataInicio + " à " + dataFim);
                        }

                        //Seta local do evento
                        if (response.getLocais().size() > 0) {
                            List<Local> locais = response.getLocais();
                            String local = "";
                            for (int i = 0; i < locais.size(); i++) {
                                local = local + locais.get(i).getDescricao();
                                if (i != locais.size() - 1)
                                    local = local + ", ";
                            }
                            findViewById(R.id.localLabelEvento).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.localEvento)).
                                    setText(local);
                        }

                        //Seta número de participantes do evento
                        if (response.getNumeroParticipantes() > 0) {
                            findViewById(R.id.participantesLabelEvento).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.participantesEvento)).
                                    setText(String.valueOf(response.getNumeroParticipantes()));
                        }

                        if (response.getPublicoAlvo() != null) {
                            //Seta público alvo do evento
                            findViewById(R.id.publicoAlvoLabelEvento).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.publicoAlvoEvento)).
                                    setText(response.getPublicoAlvo());
                        }

                        //Seta serviços do evento
                        if (response.getServicos().size() > 0) {
                            List<Servico> servicos = response.getServicos();
                            String servico = "";
                            for (int i = 0; i < servicos.size(); i++) {
                                servico = servico + servicos.get(i).getNome();
                                if (i != servicos.size() - 1)
                                    servico = servico + ", ";
                            }
                            findViewById(R.id.servicosLabelEvento).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.servicosEvento)).
                                    setText(servico);
                        }
                        //Seta categorias do evento
                        if (response.getCategorias().size() > 0) {
                            List<Categoria> categorias = response.getCategorias();
                            String categoria = "";
                            for (int i = 0; i < categorias.size(); i++) {
                                categoria = categoria + categorias.get(i).getNome();
                                if (i != categorias.size() - 1)
                                    categoria = categoria + ", ";
                            }
                            findViewById(R.id.categoriaLabelEvento).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.categoriaEvento)).
                                    setText(categoria);
                        }

                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if (api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }else{
            Toast.makeText(getBaseContext(),"Não foi possível conectar.",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
