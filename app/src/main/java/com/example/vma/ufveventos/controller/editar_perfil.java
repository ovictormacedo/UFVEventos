package com.example.vma.ufveventos.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class editar_perfil extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    UsuarioSingleton usuario = UsuarioSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void alterar_cadastro(View view){
        //Valida campos
        boolean valido = true;
        valido = validaEditText("nomeErroEditarPerfil","nomeEditarPerfil","O campo não pode estar vazio.");
        valido = validaEditText("emailErroEditarPerfil","emailEditarPerfil","O campo não pode estar vazio.");
        valido = validaEditText("senhaErroEditarPerfil","senhaEditarPerfil","O campo não pode estar vazio.");
        valido = validaRadioGroup("sexoErroCadastro","mCadastro","fCadastro","oCadastro","O campo não pode estar vazio.");

        //Se os formulários estiverem preenchidos corretamente
        if (valido){
            //Recupera dados do formulário
            String nome = ((EditText) findViewById(R.id.nomeEditarPerfil)).getText().toString();
            final String email = ((EditText) findViewById(R.id.emailEditarPerfil)).getText().toString();
            final String senha = ((EditText) findViewById(R.id.senhaEditarPerfil)).getText().toString();
            String nascimento = ((EditText) findViewById(R.id.nascimentoEditarPefil)).getText().toString();

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

            //Cria json object
            JSONObject json = new JSONObject();
            try {
                json.put("id",""+usuario.getId());
                json.put("nome", nome);
                json.put("email", email);
                json.put("senha", senha);
                json.put("nascimento",nascimento);
                json.put("sexo", sexo);
            }catch(Exception e){Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();};

            //Cria objeto para acessar a API de dados Siseventos
            RetrofitAPI retrofit = new RetrofitAPI();
            Api api = retrofit.retrofit().create(Api.class);

            //Faz requisição ao servidor
            Observable<Void> observable =  api.updateUsuario(json);

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

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.editar_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {

        } else if (id == R.id.nav_editar_perfil) {

        } else if (id == R.id.nav_notificacoes) {

        } else if (id == R.id.nav_sair) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
