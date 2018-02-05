package com.example.vma.ufveventos.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Usuario;
import com.example.vma.ufveventos.model.UsuarioSingleton;
import com.example.vma.ufveventos.util.RetrofitAPI;
import com.example.vma.ufveventos.util.Seguranca;
import com.example.vma.ufveventos.util.UsuarioNavigationDrawer;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.Calendar;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class editar_perfil extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    UsuarioSingleton usuario = UsuarioSingleton.getInstance();
    SharedPreferences sharedPref;

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

        //Google Analytics
        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("editar_perfil");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //Seta dados do usuário no navigation drawer
        UsuarioNavigationDrawer und = new UsuarioNavigationDrawer();
        und.setNomeUsuario(navigationView,usuario.getNome());
        und.setUsuarioImagem(navigationView, usuario.getFoto());

        //Inicia barra de carregamento
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarEditarPerfil);
        progressBar.setVisibility(View.GONE);

        //Seta campos com os dados do usuário logado
        UsuarioSingleton usuario = UsuarioSingleton.getInstance();
        ((EditText) findViewById(R.id.nomeEditarPerfil)).setText(usuario.getNome());
        ((EditText) findViewById(R.id.emailEditarPerfil)).setText(usuario.getEmail());
        //((EditText) findViewById(R.id.senhaEditarPerfil)).setText(usuario.getSenha());
        if (!usuario.getNascimento().isEmpty()) {
            String data = usuario.getNascimento().substring(8, 10) + "/" + usuario.getNascimento().substring(5,7)
                    +"/"+usuario.getNascimento().substring(0,4);
            ((EditText) findViewById(R.id.nascimentoEditarPefil)).setText(data);
        }

        int sexo = getResources().getIdentifier(usuario.getSexo()+"EditarPerfil", "id",
                this.getBaseContext().getPackageName());
        ((RadioButton) findViewById(sexo)).setChecked(true);
    }

    public void alterar_cadastro(View view){
        //Valida campos
        boolean valido1 = validaEditText("nomeErroEditarPerfil","nomeEditarPerfil","O campo não pode estar vazio.");
        boolean valido2 = validaEditText("emailErroEditarPerfil","emailEditarPerfil","O campo não pode estar vazio.");
        boolean valido3 = validaEditText("senhaErroEditarPerfil","senhaEditarPerfil","O campo não pode estar vazio.");
        boolean valido4 = validaRadioGroup("sexoErroEditarPerfil","mEditarPerfil","fEditarPerfil","oEditarPerfil","O campo não pode estar vazio.");
        boolean valido5 = validaSenha("confirmaSenhaErroEditarPerfil","confirmaSenhaEditarPerfil",
                "senhaErroEditarPerfil","senhaEditarPerfil","Este campo precisa ser igual à senha.");

        //Se os dados digitados estão corretos envia ao servidor
        if (valido1 && valido2 && valido3 && valido4 && valido5){
            //Mostra barra de carregamento
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarEditarPerfil);
            progressBar.setVisibility(View.VISIBLE);

            //Recupera dados do formulário
            String nome = ((EditText) findViewById(R.id.nomeEditarPerfil)).getText().toString();
            final String email = ((EditText) findViewById(R.id.emailEditarPerfil)).getText().toString();
            String senhaTemp = ((EditText) findViewById(R.id.senhaEditarPerfil)).getText().toString();
            Seguranca s = new Seguranca();
            final String senha = s.duploMd5(senhaTemp);
            String nascimento = ((EditText) findViewById(R.id.nascimentoEditarPefil)).getText().toString();
            if (!nascimento.isEmpty())
                nascimento = nascimento.substring(6,10)+"-"+nascimento.substring(3,5)+"-"+nascimento.substring(0,2);

            //Recupera referências aos radio buttons contendo as opções de sexo
            RadioButton masculino = ((RadioButton) findViewById(R.id.mEditarPerfil));
            RadioButton feminino = ((RadioButton) findViewById(R.id.fEditarPerfil));
            RadioButton outro = ((RadioButton) findViewById(R.id.oEditarPerfil));

            //Verifica qual o sexo selecionado
            String sexo = "";
            if (sexo.isEmpty())
                sexo = (masculino.isChecked()) ? "m" : "";
            if (sexo.isEmpty())
                sexo = (feminino.isChecked()) ? "f" : "";
            if (sexo.isEmpty())
                sexo = (outro.isChecked()) ? "o" : "";

            //Atualiza singleton usuario
            usuario.setSexo(sexo);
            usuario.setSenha(senha);
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setNascimento(nascimento);

            //Atualiza shared preferences
            sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("email");
            editor.putString("email", email);
            editor.remove("nascimento");
            editor.putString("nascimento", nascimento);
            editor.remove("nome");
            editor.putString("nome", nome);
            editor.remove("senha");
            editor.putString("senha", senha);
            editor.remove("sexo");
            editor.putString("sexo", sexo);
            editor.commit();

            //Cria json object
            JSONObject json = new JSONObject();
            try {
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
            Observable<Void> observable =  api.updateUsuario(json,usuario.getId());

            //Intercepta a resposta da requisição
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Void>(){
                        @Override
                        public void onCompleted(){}

                        @Override
                        public void onError(Throwable e){
                            //Esconde barra de carregamento
                            progressBar.setVisibility(View.GONE);
                            Log.i("Login error",e.getMessage());
                            Toast.makeText(getBaseContext(),"Não foi possível atualizar o cadastro, " +
                                            "tente novamente em instantes.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(Void response){
                            //Esconde barra de carregamento
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(),"Dados atualizados!",Toast.LENGTH_SHORT).show();
                            finish();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            Intent it = new Intent(getBaseContext(),inicial.class);
            startActivity(it);
        } else if (id == R.id.nav_editar_perfil) {
            Intent it;
            //Se não é um usuário logado com a conta Google pode editar o perfil
            if (usuario.getGoogleId().equals("default") || usuario.getGoogleId().equals("") ){
                it = new Intent(getBaseContext(), editar_perfil.class);
                startActivity(it);
            }
            else{
                Toast.makeText(getBaseContext(),"Funcionalidade indisponível para usuários logado com conta Google.",Toast.LENGTH_LONG)
                        .show();
            }
        } else if (id == R.id.nav_notificacoes) {
            Intent it = new Intent(getBaseContext(),notificacoes.class);
            startActivity(it);
        } else if (id == R.id.nav_sair) {
            //Registra que o usuário saiu
            SharedPreferences sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            editor.putBoolean("logado",false);
            editor.commit();

            Intent it = new Intent(getBaseContext(),login.class);
            startActivity(it);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            EditText aux = (EditText) getActivity().findViewById(R.id.nascimentoEditarPefil);
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
