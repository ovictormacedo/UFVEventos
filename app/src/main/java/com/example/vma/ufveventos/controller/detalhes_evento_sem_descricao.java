package com.example.vma.ufveventos.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Categoria;
import com.example.vma.ufveventos.model.Evento;
import com.example.vma.ufveventos.model.Local;
import com.example.vma.ufveventos.model.Servico;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import java.util.List;

public class detalhes_evento_sem_descricao extends AppCompatActivity implements OnMapReadyCallback {
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
        //Captura evento solicitado
        String eventoJson = getIntent().getStringExtra("evento");
        Gson gson = new Gson();
        Evento evento = gson.fromJson(eventoJson, Evento.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_evento_sem_descricao);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Encerra barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarDetalhesEvento);
        progressBar.setVisibility(View.GONE);

        if (googleServicesAvailable()){
            initMap();
        }
        //Seta denominação do evento
        if (evento.getDenominacao() != null){
            ((TextView) findViewById(R.id.tituloEvento)).
                    setText(evento.getDenominacao());
        }

        //Seta hora de início e fim do evento
        if (evento.getHoraInicio() != null && evento.getHoraFim() != null) {
            String horaInicio = evento.getHoraInicio().substring(0, 5);
            String horaFim = evento.getHoraFim().substring(0, 5);
            findViewById(R.id.horarioLabelEvento).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.horarioEvento)).
                    setText(horaInicio+" - "+horaFim);
        }

        //Seta data do evento
        if (evento.getDataInicio() != null && evento.getDataFim() != null) {
            String aux = evento.getDataInicio();
            String dataInicio = aux.substring(8, 10) + "/" + aux.substring(5, 7) + "/" + aux.substring(0, 4);
            aux = evento.getDataFim();
            String dataFim = aux.substring(8, 10) + "/" + aux.substring(5, 7) + "/" + aux.substring(0, 4);
            findViewById(R.id.dataLabelEvento).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.dataEvento)).
                    setText(dataInicio + " à " + dataFim);
        }

        //Seta local do evento
        if (evento.getLocais().size() > 0) {
            List<Local> locais = evento.getLocais();
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
        if (evento.getNumeroParticipantes() > 0) {
            findViewById(R.id.participantesLabelEvento).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.participantesEvento)).
                    setText(String.valueOf(evento.getNumeroParticipantes()));
        }

        if (evento.getPublicoAlvo() != null) {
            //Seta público alvo do evento
            findViewById(R.id.publicoAlvoLabelEvento).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.publicoAlvoEvento)).
                    setText(evento.getPublicoAlvo());
        }

        //Seta serviços do evento
        if (evento.getServicos().size() > 0) {
            List<Servico> servicos = evento.getServicos();
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
        if (evento.getCategorias().size() > 0) {
            List<Categoria> categorias = evento.getCategorias();
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
        //Aumenta ou reduz quadro com informações gerais
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (view.getHeight() < convertDpToPixel((float) 270, getBaseContext())){ // Verifica se está recolhido
            params.height = Math.round(convertDpToPixel((float)270, getBaseContext())); //Abre
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
        if (fParams.height > convertDpToPixel((float)295, getBaseContext())) {
            fParams.height = Math.round(convertDpToPixel((float)295, getBaseContext()));
            fParams.topMargin = Math.round(convertDpToPixel((float)240, getBaseContext()));
        }
        else {
            fParams.height = fParams.height + Math.round(convertDpToPixel((float)195, getBaseContext()));
            fParams.topMargin = Math.round(convertDpToPixel((float)45, getBaseContext()));
        }
        fragment.setLayoutParams(fParams);
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
}
