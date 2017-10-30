package com.example.vma.ufveventos.controller;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

import static java.lang.Math.abs;

public class detalhes_evento extends AppCompatActivity implements OnMapReadyCallback {
    private RetrofitAPI retrofit;
    GoogleMap mGoogleMap;
    boolean flag_scroll = false;
    int _yDelta;

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

        LinearLayout thirdPart = (LinearLayout) findViewById(R.id.thirdPartDetalhesEvento);
        thirdPart.setOnTouchListener(new ScrollFunction());

        if (googleServicesAvailable()){
            initMap();
        }

        //Cria objeto para acessar a API de dados Siseventos
        retrofit = new RetrofitAPI();
        final Api api = retrofit.retrofit().create(Api.class);

        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarDetalhesEvento);
        progressBar.setProgress(View.VISIBLE);

        //Intent it = getIntent();
        //int idEvento = Integer.parseInt(it.getStringExtra("idEvento"));
        int idEvento = 200;
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
    public void showHideFirstPart(View view){
        //TODO - Falta adaptar de pixel para DP

        /*Verifica se a terceira parte está aberta e previne que a primeira parte seja aberta*/
        View v = findViewById(R.id.thirdPartDetalhesEvento);
        FrameLayout.LayoutParams vParams = (FrameLayout.LayoutParams) v.getLayoutParams();
        if (convertPixelsToDp(vParams.topMargin,getBaseContext()) < 370) //Está aberto
            return;

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (view.getHeight() < 300) // Verifica se está recolhido
            params.height = view.getHeight()*4;
        else
            params.height = view.getHeight()/4;
        view.setLayoutParams(params);

        //Recolhe retangulo vermelho
        LinearLayout layout = (LinearLayout) findViewById(R.id.retanguloDetalhesEvento);
        params = (FrameLayout.LayoutParams)layout.getLayoutParams();
        if (params.height < 200) // Verifica se está recolhido
            params.height = params.height*4;
        else
            params.height = params.height/4;
        layout.setLayoutParams(params);

        //Aumenta ou reduz mapa
        View fragment = (View) findViewById(R.id.mapFragment);
        FrameLayout.LayoutParams fParams = (FrameLayout.LayoutParams)fragment.getLayoutParams();
        Log.i("mapa",""+fParams.height+" - "+fParams.topMargin);
        if (fParams.height > 480) {
            fParams.height = 480;
            fParams.topMargin = 480;
        }
        else {
            fParams.height = fParams.height + 390;
            fParams.topMargin = 90;
        }
        fragment.setLayoutParams(fParams);
    }

    private final class ScrollFunction implements View.OnTouchListener{
        public boolean onTouch(View view, MotionEvent event){
            final int x = (int) event.getRawX();
            final int y = (int) event.getRawY();
            ViewGroup.LayoutParams firstPartParams;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                    _yDelta = y-params.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    //Converte px para dp
                    float dp = convertPixelsToDp((float)y-_yDelta,getBaseContext());
                    if (convertPixelsToDp(lParams.topMargin,getBaseContext()) == 265){ //Está no topo
                        View r = findViewById(R.id.retanguloDetalhesEvento);
                        View m = findViewById(R.id.mapFragment);
                        View f = findViewById(R.id.firstPartDetalhesEvento);
                        if (convertPixelsToDp(f.getHeight(),getBaseContext()) != 67.5) // Verifica se está aberto
                            hideFirstPart(f,r,m,getBaseContext());
                    }
                    if (dp > 475) { //Atingiu a base
                        float px = convertDpToPixel((float)475,getBaseContext());
                        lParams.topMargin = Math.round(px);
                    } else
                        if(dp < 265){ //Atingiu o topo
                            float px = convertDpToPixel((float)265,getBaseContext());
                            lParams.topMargin = Math.round(px);
                        }
                        else {
                            //Move terceira parte
                            lParams.topMargin = y - _yDelta;
                        }
                    view.setLayoutParams(lParams);
                    break;
            }
            return true;
        }
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
     public static void hideFirstPart(View view,View retangulo, View map,Context context){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = view.getHeight()/4;
        view.setLayoutParams(params);

        //Recolhe retangulo vermelho
        LinearLayout layout = (LinearLayout) retangulo;
        params = (FrameLayout.LayoutParams)layout.getLayoutParams();
        params.height = params.height/4;
        layout.setLayoutParams(params);

        //Aumenta ou reduz mapa
        FrameLayout.LayoutParams fParams = (FrameLayout.LayoutParams)map.getLayoutParams();
        fParams.height = fParams.height + (int)convertDpToPixel((float)195,context);
        fParams.topMargin = (int)convertDpToPixel((float)45,context);
        map.setLayoutParams(fParams);
    }

}
