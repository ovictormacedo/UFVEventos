package com.example.vma.ufveventos.controller;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.util.RetrofitAPI;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class esqueci_a_senha extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_asenha);

        //Google Analytics
        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("esqueci_a_senha");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        progressBar = (ProgressBar) findViewById(R.id.progressBarEsqueciSenha);
        progressBar.setVisibility(View.GONE);
    }

    public void esqueciASenha(View view){
        ((TextView) findViewById(R.id.emailEsqueciSenhaErro)).setText("");
        ((EditText) findViewById(R.id.emailEsqueciSenha))
                .getBackground().mutate().setColorFilter(getResources().getColor(R.color.EditText), PorterDuff.Mode.SRC_ATOP);
        progressBar.setVisibility(View.VISIBLE);
        String email = ((TextView) findViewById(R.id.emailEsqueciSenha)).getText().toString();
        //Requisita nova senha
        JSONObject json = new JSONObject();
        try {
            json.put("email",email);
        }catch(Exception e){Log.e("Erro json:",e.getMessage());}

        RetrofitAPI retrofit = new RetrofitAPI();
        final Api api = retrofit.retrofit2().create(Api.class);
        Observable<Void> observable = api.recuperaSenha(json);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        ((TextView) findViewById(R.id.emailEsqueciSenhaErro)).setText("Email inválido");
                        //Muda a cor do campo para vermelho
                        ((EditText) findViewById(R.id.emailEsqueciSenha))
                                .getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);

                        Log.e("Erro cadastro:",e.getMessage());
                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Void response) {
                        //Encerra barra de carregamento
                        progressBar.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.emailEsqueciSenhaErro)).setText("");
                        ((EditText) findViewById(R.id.emailEsqueciSenha))
                                .getBackground().mutate().setColorFilter(getResources().getColor(R.color.EditText), PorterDuff.Mode.SRC_ATOP);

                        Toast.makeText(getBaseContext(),"Por favor verifique o seu e-mail.",Toast.LENGTH_LONG).show();

                        //Dispara intent para a tela inicial
                        Intent it = new Intent(getBaseContext(),login.class);
                        startActivity(it);
                    }
                });

    }
}
