package com.example.vma.ufveventos.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Dispositivo;
import com.example.vma.ufveventos.model.Usuario;
import com.example.vma.ufveventos.model.UsuarioSingleton;
import com.example.vma.ufveventos.util.RetrofitAPI;
import com.example.vma.ufveventos.util.Seguranca;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.HTTP;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class cadastrar extends AppCompatActivity {
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCadastro);
        progressBar.setVisibility(View.GONE);

        //Google Analytics
        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("cadastrar");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void faca_login(View view) {
        finish();
    }

    public void cadastrar(View view){
        //Valida campos
        boolean valido1 = validaEditText("nomeErroCadastro","nomeCadastro","O campo não pode estar vazio.");
        boolean valido2 = validaEditText("emailErroCadastro","emailCadastro","O campo não pode estar vazio.");
        boolean valido3 = validaEditText("senhaErroCadastro","senhaCadastro","O campo não pode estar vazio.");
        boolean valido4 = validaRadioGroup("sexoErroCadastro","mCadastro","fCadastro","oCadastro",
                "O campo não pode estar vazio.");
        boolean valido5 = validaSenha("confirmaSenhaErroCadastro","confirmaSenhaCadastro",
                "senhaErroCadastro","senhaCadastro","Este campo precisa ser igual à senha.");

        //Se os dados digitados estão corretos envia ao servidor
        if (valido1 && valido2 && valido3 && valido4 && valido5){
            //Mostra barra de carregamento
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCadastro);
            progressBar.setVisibility(View.VISIBLE);

            //Recupera dados do formulário
            String nome = ((EditText) findViewById(R.id.nomeCadastro)).getText().toString();
            final String email = ((EditText) findViewById(R.id.emailCadastro)).getText().toString();
            String senhaTemp = ((EditText) findViewById(R.id.senhaCadastro)).getText().toString();
            Seguranca s = new Seguranca();
            final String senha = s.duploMd5(senhaTemp);
            String nascimento = ((EditText) findViewById(R.id.nascimentoCadastro)).getText().toString();
            if (!nascimento.isEmpty())
                nascimento = nascimento.substring(6,10)+"-"+nascimento.substring(3,5)+"-"+nascimento.substring(0,2);

            //Recupera referências aos radio buttons contendo as opções de sexo
            RadioButton masculino = ((RadioButton) findViewById(R.id.mCadastro));
            RadioButton feminino = ((RadioButton) findViewById(R.id.fCadastro));
            RadioButton outro = ((RadioButton) findViewById(R.id.oCadastro));

            //Verifica qual o sexo selecionado
            String sexo = "";
            if (sexo.isEmpty())
                sexo = (masculino.isChecked()) ? "m" : "";
            if (sexo.isEmpty())
                sexo = (feminino.isChecked()) ? "f" : "";
            if (sexo.isEmpty())
                sexo = (outro.isChecked()) ? "o" : "";

            //Cria json object
            JSONObject json = new JSONObject();
            try {
                json.put("nome", nome);
                json.put("email", email);
                json.put("senha", senha);
                json.put("nascimento",nascimento);
                json.put("sexo", sexo);
            }catch(Exception e){Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_SHORT).show();};

            //Cria objeto para acessar a API de dados Siseventos
            RetrofitAPI retrofit = new RetrofitAPI();
            final Api api = retrofit.retrofit().create(Api.class);

            //Faz requisição ao servidor
            Observable<Integer> observable =  api.setUsuario(json);
            //Intercepta a resposta da requisição
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Integer>(){
                        @Override
                        public void onCompleted(){}

                        @Override
                        public void onError(Throwable e){
                            if (e instanceof HttpException)
                                Toast.makeText(getBaseContext(),"Já existe uma conta com este e-mail cadastrado",Toast.LENGTH_LONG).show();
                            else {
                                Toast.makeText(getBaseContext(), "Não foi possível realizar o cadastro, " +
                                        "tente novamente em instantes.", Toast.LENGTH_LONG).show();
                            }
                            //Esconde barra de carregamento
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNext(Integer response){
                            //Cria json com os dados de login
                            JSONObject json = new JSONObject();
                            try {
                                json.put("usuario", email);
                                json.put("senha", senha);
                            }catch (Exception e){Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();}

                            //Cria objeto para acessar a API de dados Siseventos
                            RetrofitAPI retrofit = new RetrofitAPI();
                            final Api api = retrofit.retrofit().create(Api.class);

                            //Faz requisição ao servidor para buscar id do usuário
                            Observable<Usuario> observable2 =  api.authUsuario(json);
                            //Intercepta a resposta da requisição
                            observable2.subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Usuario>(){
                                        @Override
                                        public void onCompleted(){}

                                        @Override
                                        public void onError(Throwable e){
                                            //Esconde barra de carregamento
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getBaseContext(),"Para entrar na sua conta, " +
                                                    "vá à tela de login.", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onNext(Usuario response){
                                            //Popula o singleton do usuário logado com os dados
                                            UsuarioSingleton usuario = UsuarioSingleton.getInstance();
                                            usuario.setId(response.getId());
                                            usuario.setEmail(response.getEmail());
                                            usuario.setMatricula(response.getMatricula());
                                            usuario.setNascimento(response.getNascimento());
                                            usuario.setNome(response.getNome());
                                            usuario.setSenha(response.getSenha());
                                            usuario.setFoto(response.getFoto());

                                            //Atualiza shared preferences
                                            sharedPref = getBaseContext().
                                                    getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.remove("logado");
                                            editor.putBoolean("logado", true);
                                            editor.remove("id");
                                            editor.putString("id",response.getId());
                                            editor.remove("email");
                                            editor.putString("email", response.getEmail());
                                            editor.remove("nascimento");
                                            editor.putString("nascimento", response.getNascimento());
                                            editor.remove("nome");
                                            editor.putString("nome", response.getNome());
                                            editor.remove("senha");
                                            editor.putString("senha", response.getSenha());
                                            editor.remove("sexo");
                                            editor.putString("sexo", response.getSexo());
                                            editor.remove("foto");
                                            editor.putString("foto", response.getFoto());
                                            editor.commit();

                                            //Cadastra token do dispositivo (se necessário) para receber notificações
                                            //Verifica se o usuário possui um token para este dispositivo
                                            sharedPref = getBaseContext().
                                                    getSharedPreferences("UFVEVENTOS" + response.getId(), Context.MODE_PRIVATE);
                                            String token = sharedPref.getString("firebasetoken", "falso");
                                            if (token.equals("falso")){
                                                //Requisita token FCM
                                                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                                usuario.setToken(refreshedToken);
                                                editor = sharedPref.edit();
                                                editor.putString("firebasetoken",refreshedToken);
                                                editor.commit();

                                                //Cadastra novo dispositivo do usuário
                                                JSONObject json = new JSONObject();
                                                try {
                                                    json.put("usuario", response.getId());
                                                    json.put("token", usuario.getToken());
                                                }catch(Exception e){Log.e("Erro json:",e.getMessage());}

                                                RetrofitAPI retrofit = new RetrofitAPI();
                                                final Api api = retrofit.retrofit().create(Api.class);
                                                Observable<Void> observable = api.setDispositivo(json);
                                                observable.subscribeOn(Schedulers.newThread())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new Observer<Void>() {
                                                            @Override
                                                            public void onCompleted() {
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                Log.e("Erro cadastro:",e.getMessage());
                                                                //Encerra barra de carregamento
                                                                progressBar.setVisibility(View.GONE);

                                                                //Dispara intent para a tela inicial
                                                                Intent it = new Intent(getBaseContext(),inicial.class);
                                                                startActivity(it);
                                                            }

                                                            public void onNext(Void response) {
                                                                //Recupera informações da agenda e notificações
                                                                final UsuarioSingleton usuario = UsuarioSingleton.getInstance();
                                                                sharedPref = getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0",
                                                                        Context.MODE_PRIVATE);
                                                                String agenda = sharedPref.getString("agenda", "default");
                                                                String notificacoes = sharedPref.getString("notificacoes", "default");
                                                                if (agenda.equals("default")){
                                                                    //Requisita informações de agenda e notificações
                                                                    JSONObject json = new JSONObject();
                                                                    try {
                                                                        json.put("usuario", usuario.getId());
                                                                        json.put("token", usuario.getToken());
                                                                    }catch(Exception e){Log.e("Erro json:",e.getMessage());}

                                                                    RetrofitAPI retrofit = new RetrofitAPI();
                                                                    final Api api = retrofit.retrofit().create(Api.class);
                                                                    Observable<Dispositivo> observable = api.getAgendaNotificacoes(json);
                                                                    observable.subscribeOn(Schedulers.newThread())
                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe(new Observer<Dispositivo>() {
                                                                                @Override
                                                                                public void onCompleted() {}

                                                                                @Override
                                                                                public void onError(Throwable e) {
                                                                                    Log.e("Erro cadastro:",e.getMessage());
                                                                                    //Encerra barra de carregamento
                                                                                    progressBar.setVisibility(View.GONE);

                                                                                    //Dispara intent para a tela inicial
                                                                                    Intent it = new Intent(getBaseContext(),inicial.class);
                                                                                    startActivity(it);
                                                                                }

                                                                                @Override
                                                                                public void onNext(Dispositivo response) {
                                                                                    try {
                                                                                        usuario.setAgenda(response.getAgenda());
                                                                                        usuario.setNotificacoes(response.getNotificacoes());
                                                                                        sharedPref = getBaseContext().
                                                                                                getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
                                                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                                                        editor.putString("agenda",usuario.getAgenda());
                                                                                        editor.putString("notificacoes",usuario.getNotificacoes());
                                                                                        editor.commit();
                                                                                    }catch (Exception e){Log.e("ERRO JSON",e.getMessage());}

                                                                                    //Encerra barra de carregamento
                                                                                    progressBar.setVisibility(View.GONE);

                                                                                    //Dispara intent para a tela inicial
                                                                                    Intent it = new Intent(getBaseContext(),inicial.class);
                                                                                    startActivity(it);
                                                                                }
                                                                            });
                                                                }else{
                                                                    usuario.setAgenda(agenda);
                                                                    usuario.setNotificacoes(notificacoes);
                                                                    //Encerra barra de carregamento
                                                                    progressBar.setVisibility(View.GONE);

                                                                    //Dispara intent para a tela inicial
                                                                    Intent it = new Intent(getBaseContext(),inicial.class);
                                                                    startActivity(it);
                                                                }
                                                            }
                                                        });
                                            }else{
                                                usuario.setToken(token);

                                                //Recupera informações da agenda e notificações
                                                sharedPref = getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0",
                                                        Context.MODE_PRIVATE);
                                                String agenda = sharedPref.getString("agenda", "default");
                                                String notificacoes = sharedPref.getString("notificacoes", "default");
                                                if (agenda.equals("default")){
                                                    //Requisita informações de agenda e notificações
                                                    JSONObject json = new JSONObject();
                                                    try {
                                                        json.put("usuario", usuario.getId());
                                                        json.put("token", usuario.getToken());
                                                    }catch(Exception e){Log.e("Erro json:",e.getMessage());}

                                                    RetrofitAPI retrofit = new RetrofitAPI();
                                                    final Api api = retrofit.retrofit().create(Api.class);
                                                    Observable<Dispositivo> observable = api.getAgendaNotificacoes(json);
                                                    observable.subscribeOn(Schedulers.newThread())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new Observer<Dispositivo>() {
                                                                @Override
                                                                public void onCompleted() {}

                                                                @Override
                                                                public void onError(Throwable e) {
                                                                    Log.e("Erro cadastro:",e.getMessage());
                                                                    //Encerra barra de carregamento
                                                                    progressBar.setVisibility(View.GONE);

                                                                    //Dispara intent para a tela inicial
                                                                    Intent it = new Intent(getBaseContext(),inicial.class);
                                                                    startActivity(it);
                                                                }

                                                                @Override
                                                                public void onNext(Dispositivo response) {
                                                                    UsuarioSingleton usuario = UsuarioSingleton.getInstance();
                                                                    try {
                                                                        usuario.setAgenda(response.getAgenda());
                                                                        usuario.setNotificacoes(response.getNotificacoes());
                                                                        sharedPref = getBaseContext().
                                                                                getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                                        editor.putString("agenda",usuario.getAgenda());
                                                                        editor.putString("notificacoes",usuario.getNotificacoes());
                                                                        editor.commit();
                                                                    }catch (Exception e){Log.e("ERRO JSON",e.getMessage());}

                                                                    //Encerra barra de carregamento
                                                                    progressBar.setVisibility(View.GONE);
                                                                    //Dispara intent para a tela inicial
                                                                    Intent it = new Intent(getBaseContext(),inicial.class);
                                                                    startActivity(it);
                                                                }
                                                            });
                                                }else{
                                                    usuario.setAgenda(agenda);
                                                    usuario.setNotificacoes(notificacoes);
                                                    //Encerra barra de carregamento
                                                    progressBar.setVisibility(View.GONE);
                                                    //Dispara intent para a tela inicial
                                                    Intent it = new Intent(getBaseContext(),inicial.class);
                                                    startActivity(it);
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    public boolean validaSenha(String idErroConfirma, String idCampoConfirma, String idErro, String idCampo, String msg){
        //Busca referência do campo
        int texto1 = getResources().getIdentifier(idCampo, "id",
                this.getBaseContext().getPackageName());

        int texto2 = getResources().getIdentifier(idCampoConfirma, "id",
                this.getBaseContext().getPackageName());

        //Escrever texto embaixo do campo
        String aux1 = ((EditText) findViewById(texto1)).getText().toString();
        String aux2 = ((EditText) findViewById(texto2)).getText().toString();
        if (!aux1.equals(aux2)) {
            //Muda a cor do campo para vermelho
            ((EditText) findViewById(texto2))
                    .getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);

            //Busca referência do campo
            texto2 = getResources().getIdentifier(idErroConfirma, "id",
                    this.getBaseContext().getPackageName());
            //Escrever texto embaixo do campo
            TextView senhaErro = ((TextView) findViewById(texto2));
            senhaErro.setText(msg);

            return false;
        }else{
            //Volta ao normal a cor do campo
            ((EditText) findViewById(texto2))
                    .getBackground().mutate().setColorFilter(getResources().getColor(R.color.EditText), PorterDuff.Mode.SRC_ATOP);
            //Busca referência do campo
            texto2 = getResources().getIdentifier(idErroConfirma, "id",
                    this.getBaseContext().getPackageName());
            //Remove texto de erro
            TextView emailmatriculaErro = ((TextView) findViewById(texto2));
            emailmatriculaErro.setText("");
            return true;
        }
    }
    public boolean validaEditText(String idErro, String idCampo, String msg){
        //Busca referência do campo
        int texto = getResources().getIdentifier(idCampo, "id",
                this.getBaseContext().getPackageName());

        //Escrever texto embaixo do campo
        String aux = ((EditText) findViewById(texto)).getText().toString();
        if (aux.isEmpty()) {
            //Muda a cor do campo para vermelho
            ((EditText) findViewById(texto))
                    .getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);

            //Busca referência do campo
            texto = getResources().getIdentifier(idErro, "id",
                    this.getBaseContext().getPackageName());
            //Escrever texto embaixo do campo
            TextView senhaErro = ((TextView) findViewById(texto));
            senhaErro.setText(msg);

            return false;
        }else{
            //Volta ao normal a cor do campo
            ((EditText) findViewById(texto))
                    .getBackground().mutate().setColorFilter(getResources().getColor(R.color.EditText), PorterDuff.Mode.SRC_ATOP);
            //Busca referência do campo
            texto = getResources().getIdentifier(idErro, "id",
                    this.getBaseContext().getPackageName());
            //Remove texto de erro
            TextView emailmatriculaErro = ((TextView) findViewById(texto));
            emailmatriculaErro.setText("");
            return true;
        }
    }
    public boolean validaRadioGroup(String idErro,String id1,String id2,String id3,String msg){
        //Busca referência do campo
        int erro = getResources().getIdentifier(idErro, "id",
                this.getBaseContext().getPackageName());

        int r1 = getResources().getIdentifier(id1, "id",
                this.getBaseContext().getPackageName());

        int r2 = getResources().getIdentifier(id2, "id",
                this.getBaseContext().getPackageName());

        int r3 = getResources().getIdentifier(id3, "id",
                this.getBaseContext().getPackageName());

        RadioButton rb1 = ((RadioButton) findViewById(r1));
        RadioButton rb2 = ((RadioButton) findViewById(r2));
        RadioButton rb3 = ((RadioButton) findViewById(r3));
        TextView er = ((TextView) findViewById(erro));

        boolean valida = false;
        if (rb1 != null)
            if(rb1.isChecked())
                valida = true;

        if (rb2 != null)
            if(rb2.isChecked())
                valida = true;

        if (rb3 != null)
            if(rb3.isChecked())
                valida = true;

        //Caso nenhum sexo tenha sido selecionado informado msg de erro
        if (!valida){
            er.setText(msg);
        }else{
            er.setText("");
        }

        return valida;
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            //Seta data no input de data de nascimento
            EditText aux = (EditText) getActivity().findViewById(R.id.nascimentoCadastro);
            //Formata a data
            String dia = ""+day;
            if (day < 10)
                dia = "0"+day;
            String mes = ""+month;
            if (month < 10)
                mes = "0"+month;
            aux.setText(dia+"/"+mes+"/"+year);
        }
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}