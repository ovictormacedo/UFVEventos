package com.example.vma.ufveventos.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Usuario;
import com.example.vma.ufveventos.model.UsuarioSingleton;
import com.example.vma.ufveventos.util.RetrofitAPI;

import org.json.JSONObject;

import java.util.Calendar;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class cadastrar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
    }

    public void faca_login(View view) {
        finish();
    }

    public void cadastrar(View view){
        //Valida campos
        boolean valido = true;
        valido = validaEditText("nomeErroCadastro","nomeCadastro","O campo não pode estar vazio.");
        valido = validaEditText("emailErroCadastro","emailCadastro","O campo não pode estar vazio.");
        valido = validaEditText("senhaErroCadastro","senhaCadastro","O campo não pode estar vazio.");
        valido = validaRadioGroup("sexoErroEditarPerfil","mEditarPerfil","fEditarPerfil","oEditarPerfil",
                "O campo não pode estar vazio.");

        //Se os dados digitados estão corretos envia ao servidor
        if (valido){
            //Recupera dados do formulário
            String nome = ((EditText) findViewById(R.id.nomeCadastro)).getText().toString();
            final String email = ((EditText) findViewById(R.id.emailCadastro)).getText().toString();
            final String senha = ((EditText) findViewById(R.id.senhaCadastro)).getText().toString();
            String nascimento = ((EditText) findViewById(R.id.nascimentoCadastro)).getText().toString();

            //Recupera referências aos radio buttons contendo as opções de sexo
            RadioButton masculino = ((RadioButton) findViewById(R.id.mCadastro));
            RadioButton feminino = ((RadioButton) findViewById(R.id.fCadastro));
            RadioButton outro = ((RadioButton) findViewById(R.id.oCadastro));

            //Verifica qual o sexo selecionado
            String sexo = "";
            if (masculino != null)
                sexo = (masculino.isChecked()) ? "m" : "";
            else if (feminino != null)
                sexo = (feminino.isChecked()) ? "f" : "";
            else
                sexo = "o";

            //Toast.makeText(getBaseContext(),nascimento,Toast.LENGTH_LONG).show();
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
            Api api = retrofit.retrofit().create(Api.class);

            //Faz requisição ao servidor
            Observable<Void> observable =  api.setUsuario(json);

            //Intercepta a resposta da requisição
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Void>(){
                        @Override
                        public void onCompleted(){}

                        @Override
                        public void onError(Throwable e){
                            Log.i("Login error",e.getMessage());
                            Toast.makeText(getBaseContext(),"Não foi possível realizar o cadastro, " +
                                            "tente novamente em instantes.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(Void response){
                            //Cria json com os dados de login
                            JSONObject json = new JSONObject();
                            try {
                                json.put("usuario", email);
                                json.put("senha", senha);
                            }catch (Exception e){Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();}

                            //Cria objeto para acessar a API de dados Siseventos
                            RetrofitAPI retrofit = new RetrofitAPI();
                            Api api = retrofit.retrofit().create(Api.class);

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
                                            Log.i("Login error",e.getMessage());
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

                                            //Dispara intent para a tela inicial
                                        }
                                    });
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