package com.example.vma.ufveventos.controller;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Usuario;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
            Api api = retrofit.retrofit().create(Api.class);

            //Chama método
            JSONObject json = new JSONObject();
            String usuario = "1";
            String senha = "1234";
            try {
                json.put("usuario", usuario);
                json.put("senha", senha);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Observable<Usuario> observable = api.getUsuario2(json);

            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Usuario>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("Retrofit error", "Erro:" + e.getMessage());
                            Toast.makeText(getBaseContext(), "Erro:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(Usuario response) {
                            Log.i("Retrofit error", response.getNome());
                            Toast.makeText(getBaseContext(), response.getNome(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        Toast.makeText(getBaseContext(),"Clicou",Toast.LENGTH_SHORT).show();
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
            TextView aux2 = ((TextView) findViewById(texto));
            aux2.setText(msg);

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
}