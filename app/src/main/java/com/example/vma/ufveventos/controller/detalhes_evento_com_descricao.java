package com.example.vma.ufveventos.controller;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Categoria;
import com.example.vma.ufveventos.model.Evento;
import com.example.vma.ufveventos.model.Local;
import com.example.vma.ufveventos.model.Servico;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.internal.framed.Header;

public class detalhes_evento_com_descricao extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    GoogleMap mGoogleMap;
    private LocationManager mLocationManager = null;
    private String provider = null;
    private Marker mCurrentPosition = null;
    private ArrayList<LatLng> traceOfMe = null;
    private Polyline mPolyline = null;
    private LatLng mSourceLatLng = null;
    private LatLng mDestinationLatLng;
    int _yDelta;

    private void initMap(){
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //mGoogleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);

        if (isProviderAvailable() && (provider != null)) {
            locateCurrentPosition();
        }

        traceMe(mSourceLatLng,mDestinationLatLng);
    }
    private void locateCurrentPosition() {
        int status = getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                getPackageName());

        if (status == PackageManager.PERMISSION_GRANTED) {
            Location location = mLocationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);
            //mLocationManager.addGpsStatusListener(this);
            long minTime = 5000;// ms
            float minDist = 5.0f;// meter
            mLocationManager.requestLocationUpdates(provider, minTime, minDist, this);
        }
    }
    private boolean isProviderAvailable() {
        mLocationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = mLocationManager.getBestProvider(criteria, true);
        if (mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;

            return true;
        }

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }

        if (provider != null) {
            return true;
        }
        return false;
    }

    private void updateWithNewLocation(Location location) {
        if (location != null && provider != null) {
            double lng = location.getLongitude();
            double lat = location.getLatitude();

            mSourceLatLng = new LatLng(lat, lng);

            CameraPosition camPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng)).zoom(14f).build();

            if (mGoogleMap != null)
                mGoogleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(camPosition));
        } else {
            Log.d("Location error", "Something went wrong");
        }
    }
    private void addBoundaryToCurrentPosition(double lat, double lang) {
        MarkerOptions mMarkerOptions = new MarkerOptions();
        mMarkerOptions.position(new LatLng(lat, lang));
        //mMarkerOptions.icon(BitmapDescriptorFactory
          //      .fromResource(R.drawable.marker_current));
        mMarkerOptions.anchor(0.5f, 0.5f);

        /*CircleOptions mOptions = new CircleOptions()
            .center(new LatLng(lat, lang)).radius(10000)
                .strokeColor(0x110000FF).strokeWidth(1).fillColor(0x110000FF);
        mGoogleMap.addCircle(mOptions);
        if (mCurrentPosition != null)
            mCurrentPosition.remove();*/
        mCurrentPosition = mGoogleMap.addMarker(mMarkerOptions);
    }
    private void addMarker(double lat, double lng, String text) {
        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lng))
                .title(text)
                .flat(true));
    }

    @Override
    public void onLocationChanged(Location location) {
        updateWithNewLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {

        updateWithNewLocation(null);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            case LocationProvider.AVAILABLE:
                break;
        }
    }

    private void traceMe(LatLng srcLatLng, LatLng destLatLng) {
        String srcParam = srcLatLng.latitude + "," + srcLatLng.longitude;
        String destParam = destLatLng.latitude + "," + destLatLng.longitude;
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+srcParam+"&destination="
                + destParam + "&sensor=false&units=metric&mode=driving&key=AIzaSyCYMR04JVUMSJMs0BtLxl6rsAVY-xwTLqk";
        Log.i("URL",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MapDirectionsParser parser = new MapDirectionsParser();
                        List<List<HashMap<String, String>>> routes = parser.parse(response);
                        ArrayList<LatLng> points = null;

                        for (int i = 0; i < routes.size(); i++) {
                            points = new ArrayList<LatLng>();

                            // Fetching i-th route
                            List<HashMap<String, String>> path = routes.get(i);

                            //Limpa mapa
                            mGoogleMap.clear();

                            //Adiciona marcador à posição inicial
                            HashMap<String, String> pointAux = path.get(0);
                            double latAux = Double.parseDouble(pointAux.get("lat"));
                            double lngAux = Double.parseDouble(pointAux.get("lng"));
                            addMarker(latAux,lngAux,"Seu local");

                            CircleOptions mOptions = new CircleOptions()
                                    .center(new LatLng(latAux, lngAux)).radius(200)
                                    .strokeColor(0x110000FF).strokeWidth(5).fillColor(0x110000FF);
                            mGoogleMap.addCircle(mOptions);

                            //Adiciona marcador à posição final
                            pointAux = path.get(path.size()-1);
                            latAux = Double.parseDouble(pointAux.get("lat"));
                            lngAux = Double.parseDouble(pointAux.get("lng"));
                            addMarker(latAux,lngAux,"Destino");

                            // Fetching all the points in i-th route
                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);

                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);

                                points.add(position);
                            }
                        }
                        drawPoints(points, mGoogleMap);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERRO",error.getMessage());
                    }
                });

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);
        addBoundaryToCurrentPosition(destLatLng.latitude,destLatLng.longitude);
    }


    private void drawPoints(ArrayList<LatLng> points, GoogleMap mMaps) {
        if (points == null) {
            return;
        }
        traceOfMe = points;
        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : traceOfMe) {
            polylineOpt.add(latlng);
        }
        polylineOpt.color(Color.BLUE);
        if (mPolyline != null) {
            mPolyline.remove();
            mPolyline = null;
        }
        if (mGoogleMap != null) {
            mPolyline = mGoogleMap.addPolyline(polylineOpt);

        } else {

        }
        if (mPolyline != null)
            mPolyline.setWidth(10);
    }


    public void getDirection(View view) {
        if (mSourceLatLng != null && mDestinationLatLng != null) {
            traceMe(mSourceLatLng, mDestinationLatLng);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Captura evento solicitado
        String eventoJson = getIntent().getStringExtra("evento");
        Gson gson = new Gson();
        Evento evento = gson.fromJson(eventoJson, Evento.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_evento_com_descricao);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        LinearLayout thirdPart = (LinearLayout) findViewById(R.id.thirdPartDetalhesEvento);
        thirdPart.setOnTouchListener(new ScrollFunction());

        //Encerra barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarDetalhesEvento);
        progressBar.setVisibility(View.GONE);

        //Traça rota
        mDestinationLatLng = new LatLng(-20.763757, -42.881494);
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

        //Seta descrição do evento
        if (evento.getDescricao_evento() != null){
            ((TextView) findViewById(R.id.descricaoEvento)).
                    setText(evento.getDescricao_evento());
        }

        //Seta programação do evento
        if (evento.getProgramacao_evento() != null){
            ((TextView) findViewById(R.id.programacaoEvento)).
                    setText(evento.getProgramacao_evento());
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
}
