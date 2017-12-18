package com.example.vma.ufveventos.controller;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Usuario;
import com.example.vma.ufveventos.model.UsuarioSingleton;
import com.example.vma.ufveventos.util.RetrofitAPI;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class login extends AppCompatActivity {
    SharedPreferences sharedPref;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
        progressBar.setVisibility(View.GONE);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0",Context.MODE_PRIVATE);
        Log.i("SHARED PREFERENCE: ", ""+sharedPref.getBoolean("logado",false));
        //Verifica se o usuário está logado
        if (sharedPref.getBoolean("logado",false)) {
            //Popula o singleton do usuário logado com os dados
            UsuarioSingleton usuario = UsuarioSingleton.getInstance();
            usuario.setId(sharedPref.getInt("id",1));
            usuario.setEmail(sharedPref.getString("email","default"));
            usuario.setMatricula(sharedPref.getString("matricula","default"));
            usuario.setNascimento(sharedPref.getString("nascimento","default"));
            usuario.setNome(sharedPref.getString("nome","default"));
            usuario.setSenha(sharedPref.getString("senha","default"));
            usuario.setSexo(sharedPref.getString("sexo","default"));

            //Dispara intent para a tela inicial
            Intent it = new Intent(getBaseContext(),inicial.class);
            startActivity(it);
        }
    }

    public void cadastrar(View view){
        Intent it = new Intent(this,cadastrar.class);
        startActivity(it);
    }

    public void entrar(View view){
        //Valida dados de login
        boolean valido = true;
        valido = validaEditText("emailmatriculaErroLogin","emailmatriculaLogin","O campo não pode ficar vazio.");
        valido = validaEditText("senhaErroLogin","senhaLogin","O campo não pode ficar vazio.");

        if (valido) {
            //Cria objeto para acessar a API de dados Siseventos
            RetrofitAPI retrofit = new RetrofitAPI();
            final Api api = retrofit.retrofit().create(Api.class);

            //Inicia barra de carregamento
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
            progressBar.setVisibility(View.VISIBLE);

            JSONObject json = new JSONObject();
            String usuario = ((EditText) findViewById(R.id.emailmatriculaLogin)).getText().toString();
            String senha = ((EditText) findViewById(R.id.senhaLogin)).getText().toString();
            try {
                json.put("usuario", usuario);
                json.put("senha", senha);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Observable<Usuario> observable = api.authUsuario(json);
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Usuario>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            //Encerra barra de carregamento
                            if (e instanceof HttpException) {
                                ResponseBody aux = ((HttpException) e).response().errorBody();
                                try {
                                    JSONObject json = new JSONObject(aux.string());
                                    if (json.has("usuario")) {
                                        ((EditText) findViewById(R.id.emailmatriculaLogin))
                                                .getBackground().mutate().setColorFilter(getResources().
                                                getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                                        ((TextView) findViewById(R.id.emailmatriculaErroLogin)).setText(json.getString("usuario"));
                                    }
                                    else{
                                        ((EditText) findViewById(R.id.emailmatriculaLogin))
                                                .getBackground().mutate().setColorFilter(getResources().
                                                getColor(R.color.EditText), PorterDuff.Mode.SRC_ATOP);
                                        ((TextView) findViewById(R.id.emailmatriculaErroLogin)).setText("");
                                    }

                                    if (json.has("senha")) {
                                        ((EditText) findViewById(R.id.senhaLogin))
                                                .getBackground().mutate().setColorFilter(getResources().
                                                getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                                        ((TextView) findViewById(R.id.senhaErroLogin)).setText(json.getString("senha"));
                                    }
                                    else{
                                        ((EditText) findViewById(R.id.senhaLogin))
                                                .getBackground().mutate().setColorFilter(getResources().
                                                getColor(R.color.EditText), PorterDuff.Mode.SRC_ATOP);
                                        ((TextView) findViewById(R.id.senhaErroLogin)).setText("");
                                    }
                                    Log.i("Retrofit error", "Erro:" + e.getMessage());
                                }catch(Exception t){}
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNext(Usuario response) {
                            //Registra login do usuário
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("logado", true);
                            editor.putInt("id", response.getId());
                            editor.putString("email", response.getEmail());
                            editor.putString("matricula", response.getMatricula());
                            editor.putString("nascimento", response.getNascimento());
                            editor.putString("nome", response.getNome());
                            editor.putString("senha", response.getSenha());
                            editor.putString("sexo", response.getSexo());
                            editor.commit();

                            //Popula o singleton do usuário logado com os dados
                            UsuarioSingleton usuario = UsuarioSingleton.getInstance();
                            usuario.setId(response.getId());
                            usuario.setEmail(response.getEmail());
                            usuario.setMatricula(response.getMatricula());
                            usuario.setNascimento(response.getNascimento());
                            usuario.setNome(response.getNome());
                            usuario.setSenha(response.getSenha());
                            usuario.setSexo(response.getSexo());

                            //Verifica se o usuário possui um token para este dispositivo
                            sharedPref = getBaseContext().
                                    getSharedPreferences("UFVEVENTOS" + response.getEmail(), Context.MODE_PRIVATE);
                            String token = sharedPref.getString("token", "falso");
                            if (token.equals("falso")){
                                //Requisita token FCM
                                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                usuario.setToken(refreshedToken);
                                editor = sharedPref.edit();
                                editor.putString("token",refreshedToken);
                                editor.commit();
                                Log.i("TOKEN 1:",refreshedToken);
                                //Cadastra novo dispositivo do usuário
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("usuario", response.getId());
                                    json.put("token", usuario.getToken());
                                }catch(Exception e){Log.e("Erro json:",e.getMessage());}

                                //Envia ao servidor
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

                                            @Override
                                            public void onNext(Void response) {
                                                //Encerra barra de carregamento
                                                progressBar.setVisibility(View.GONE);

                                                //Dispara intent para a tela inicial
                                                Intent it = new Intent(getBaseContext(),inicial.class);
                                                startActivity(it);
                                            }
                                        });
                            }else{
                                Log.i("TOKEN 2:",token);
                                usuario.setToken(token);
                                //Encerra barra de carregamento
                                progressBar.setVisibility(View.GONE);

                                //Dispara intent para a tela inicial
                                Intent it = new Intent(getBaseContext(),inicial.class);
                                startActivity(it);
                            }
                        }
                    });
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
}