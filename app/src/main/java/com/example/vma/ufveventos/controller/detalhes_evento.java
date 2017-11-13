package com.example.vma.ufveventos.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
        setContentView(R.layout.activity_detalhes_evento_com_descricao);

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

                        //Seta descrição do evento
                        if (response.getDescricao_evento() != null){
                            ((TextView) findViewById(R.id.descricaoEvento)).
                                    setText(response.getDescricao_evento());
                        }

                        //Seta programação do evento
                        if (response.getProgramacao_evento() != null){
                            ((TextView) findViewById(R.id.programacaoEvento)).
                                    setText(response.getProgramacao_evento());
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
        /*Verifica se a terceira parte está aberta*/
        View v = findViewById(R.id.thirdPartDetalhesEvento);
        FrameLayout.LayoutParams vParams = (FrameLayout.LayoutParams) v.getLayoutParams();
        boolean terceiraParteEstaAberta = false;
        if (convertPixelsToDp(vParams.topMargin,getBaseContext()) < 370) //Está aberto
            terceiraParteEstaAberta = true;

        //Aumenta ou reduz quadro com informações gerais
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (view.getHeight() < convertDpToPixel((float) 270, getBaseContext())){ // Verifica se está recolhido
            params.height = Math.round(convertDpToPixel((float)270, getBaseContext())); //Abre
            if (terceiraParteEstaAberta)
                vParams.topMargin = Math.round(convertDpToPixel((float) 475, getBaseContext()));
            ((ImageView) findViewById(R.id.abreFechaFirstPart)).setImageResource(R.drawable.fechar); //Seta imagem "fechar"
        }
        else {
            ((ImageView) findViewById(R.id.abreFechaFirstPart)).setImageResource(R.drawable.abrir); //Seta imagem "abrir"
            params.height = Math.round(convertDpToPixel((float) 67.5, getBaseContext())); //Fecha
        }
        view.setLayoutParams(params);

        //Recolhe retangulo vermelho
        LinearLayout layout = (LinearLayout) findViewById(R.id.retanguloDetalhesEvento);
        params = (FrameLayout.LayoutParams)layout.getLayoutParams();
        if (params.height < convertDpToPixel((float)250, getBaseContext())) // Verifica se está recolhido
            params.height = Math.round(convertDpToPixel((float)250, getBaseContext()));
        else
            params.height = Math.round(convertDpToPixel((float)62.5, getBaseContext()));
        layout.setLayoutParams(params);

        //Aumenta ou reduz mapa
        View fragment = (View) findViewById(R.id.mapFragment);
        FrameLayout.LayoutParams fParams = (FrameLayout.LayoutParams)fragment.getLayoutParams();
        Log.i("mapa",""+fParams.height+" - "+fParams.topMargin);
        if (fParams.height > convertDpToPixel((float)240, getBaseContext())) {
            fParams.height = Math.round(convertDpToPixel((float)240, getBaseContext()));
            fParams.topMargin = Math.round(convertDpToPixel((float)240, getBaseContext()));
        }
        else {
            fParams.height = fParams.height + Math.round(convertDpToPixel((float)195, getBaseContext()));
            fParams.topMargin = Math.round(convertDpToPixel((float)45, getBaseContext()));
        }
        fragment.setLayoutParams(fParams);
    }

    private final class ScrollFunction implements View.OnTouchListener{
        public boolean onTouch(View view, MotionEvent event){
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
                    ViewGroup.LayoutParams fParams = findViewById(R.id.firstPartDetalhesEvento).getLayoutParams();
                    FrameLayout.LayoutParams rParams = (FrameLayout.LayoutParams)findViewById(R.id.retanguloDetalhesEvento).getLayoutParams();
                    FrameLayout.LayoutParams mParams = (FrameLayout.LayoutParams)findViewById(R.id.mapFragment).getLayoutParams();
                    FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    //Converte px para dp
                    float dp = convertPixelsToDp((float)y-_yDelta,getBaseContext());
                    if (dp > 475) { //Atingiu a base
                        lParams.topMargin = Math.round(convertDpToPixel((float)475,getBaseContext()));
                    } else
                        if(dp < 265) { //Atingiu o topo
                            fParams.height = Math.round(convertDpToPixel((float) 67.5, getBaseContext()));
                            findViewById(R.id.firstPartDetalhesEvento).setLayoutParams(fParams);
                            mParams.height = Math.round(convertDpToPixel((float) 435, getBaseContext()));
                            mParams.topMargin = Math.round(convertDpToPixel((float) 52.5, getBaseContext()));
                            findViewById(R.id.mapFragment).setLayoutParams(mParams);
                            rParams.height = Math.round(convertDpToPixel((float) 62.5, getBaseContext()));
                            findViewById(R.id.retanguloDetalhesEvento).setLayoutParams(rParams);
                            lParams.topMargin = Math.round(convertDpToPixel((float)265,getBaseContext()));
                            //Seta imagem "abrir"
                            ((ImageView) findViewById(R.id.abreFechaFirstPart)).setImageResource(R.drawable.abrir);
                        }
                        else {
                            //Detecta direção do scroll
                            if (lParams.topMargin > (y - _yDelta)) // O scroll é para cima
                                if (fParams.height <= convertDpToPixel((float)67.5,getBaseContext())) { //Verifica se atingiu o limite
                                    fParams.height = Math.round(convertDpToPixel((float)67.5,getBaseContext()));
                                    mParams.height = Math.round(convertDpToPixel((float)435,getBaseContext()));
                                    mParams.topMargin = Math.round(convertDpToPixel((float)52.5,getBaseContext()));
                                    rParams.height = Math.round(convertDpToPixel((float)62.5,getBaseContext()));
                                    findViewById(R.id.firstPartDetalhesEvento).setLayoutParams(fParams);
                                    findViewById(R.id.mapFragment).setLayoutParams(mParams);
                                    findViewById(R.id.retanguloDetalhesEvento).setLayoutParams(rParams);
                                    //Seta imagem "abrir"
                                    ((ImageView) findViewById(R.id.abreFechaFirstPart)).setImageResource(R.drawable.abrir);
                                }else {
                                    int offsetScroll = 22;
                                    //Move a primeira parte
                                    fParams.height = fParams.height - offsetScroll;
                                    findViewById(R.id.firstPartDetalhesEvento).setLayoutParams(fParams);
                                    //Move retângulo
                                    rParams.height = rParams.height - offsetScroll;
                                    findViewById(R.id.retanguloDetalhesEvento).setLayoutParams(rParams);
                                    //Move Mapa
                                    mParams.height = mParams.height + offsetScroll;
                                    mParams.topMargin = mParams.topMargin - offsetScroll;
                                    findViewById(R.id.mapFragment).setLayoutParams(mParams);
                                }
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
        params.height = Math.round(convertDpToPixel((float)67.5, context));
        view.setLayoutParams(params);

        //Recolhe retangulo vermelho
        LinearLayout layout = (LinearLayout) retangulo;
        params = (FrameLayout.LayoutParams)layout.getLayoutParams();
        params.height = Math.round(convertDpToPixel((float)62.5, context));
        layout.setLayoutParams(params);

        //Aumenta ou reduz mapa
        FrameLayout.LayoutParams fParams = (FrameLayout.LayoutParams)map.getLayoutParams();
        fParams.height = fParams.height + (int)convertDpToPixel((float)195,context);
        fParams.topMargin = (int)convertDpToPixel((float)45,context);
        map.setLayoutParams(fParams);
    }
}
