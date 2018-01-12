package com.example.vma.ufveventos.controller;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class login extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sharedPref;
    GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
        progressBar.setVisibility(View.GONE);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("224128381554-g15qnhlokg544p5746fv9q5tg1b0c1aa.apps.googleusercontent.com")
                .requestProfile()
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Start initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
        //Verifica se o usuário está logado
        if (sharedPref.getBoolean("logado",false)) {
            //Popula o singleton do usuário logado com os dados
            UsuarioSingleton usuario = UsuarioSingleton.getInstance();
            usuario.setId(sharedPref.getString("id","default"));
            usuario.setGoogleId(sharedPref.getString("googleId","default"));
            usuario.setEmail(sharedPref.getString("email","default"));
            usuario.setMatricula(sharedPref.getString("matricula","default"));
            usuario.setNascimento(sharedPref.getString("nascimento","default"));
            usuario.setNome(sharedPref.getString("nome","default"));
            usuario.setSenha(sharedPref.getString("senha","default"));
            usuario.setSexo(sharedPref.getString("sexo","default"));
            usuario.setFoto(sharedPref.getString("foto","default"));
            SharedPreferences sharedPref2 = getBaseContext().
                    getSharedPreferences("UFVEVENTOS"+usuario.getId(), Context.MODE_PRIVATE);
            usuario.setToken(sharedPref2.getString("token","default"));

            //Dispara intent para a tela inicial
            Intent it = new Intent(getBaseContext(),inicial.class);
            startActivity(it);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Atualiza singleton do usuário
        UsuarioSingleton usuario = UsuarioSingleton.getInstance();
        updateUsuario(currentUser,usuario.getId());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        //Mostra barra de carregamento
        progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            UsuarioSingleton usuario = UsuarioSingleton.getInstance();

                            /*Cadastra novo usuário. O servidor apenas realiza novo cadastro
                            se não houver nenhum cadastro atribuído ao presente googleId*/

                            //Cria objeto para acessar a API de dados Siseventos
                            RetrofitAPI retrofit = new RetrofitAPI();
                            final Api api = retrofit.retrofit().create(Api.class);

                            //Cria json object
                            JSONObject json = new JSONObject();
                            try {
                                json.put("nome", user.getDisplayName());
                                json.put("email", user.getEmail());
                                json.put("googleId", user.getUid());
                            }catch(Exception e){Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_SHORT).show();};

                            //Faz requisição ao servidor
                            Observable<Void> observable =  api.setUsuarioGoogle(json);
                            //Intercepta a resposta da requisição
                            observable.subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Void>(){
                                        @Override
                                        public void onCompleted(){}

                                        @Override
                                        public void onError(Throwable e){
                                            ResponseBody aux = ((HttpException) e).response().errorBody();
                                            try {
                                                Log.i("Login error", aux.string());
                                            }catch (Exception ex){}
                                        }

                                        @Override
                                        public void onNext(Void response){
                                            //Cria json com os dados de login
                                            JSONObject json = new JSONObject();
                                            try {
                                                json.put("googleId", user.getUid());
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
                                                            Log.i("Login error",e.getMessage()+e.getCause());
                                                            Toast.makeText(getBaseContext(),"Não foi possível logar em sua conta.",
                                                                    Toast.LENGTH_LONG).show();
                                                        }

                                                        @Override
                                                        public void onNext(Usuario response){
                                                            updateUsuario(user,response.getId());
                                                            //Cadastra token para o dispositivo receber notificações
                                                            cadastraToken(response);
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            //Esconde barra de carregamento
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_activity), "Autenticação falhou.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUsuario(FirebaseUser currentUser, String id){
        if (currentUser != null) {
            sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("logado", true);
            editor.putString("id", id);
            editor.putString("googleId", currentUser.getUid());
            editor.putString("email", currentUser.getEmail());
            editor.putString("nome", currentUser.getDisplayName());
            editor.putString("foto", currentUser.getPhotoUrl().toString());
            editor.commit();
            Log.i("SHARED PREFERENCE: ","Atualizando usuário singleton");
            Log.i("SHARED PREFERENCE: ",currentUser.getDisplayName());
            Log.i("SHARED PREFERENCE: ",currentUser.getUid());

            UsuarioSingleton usuario = UsuarioSingleton.getInstance();
            usuario.setId(sharedPref.getString("id", id));
            usuario.setGoogleId(sharedPref.getString("googleId", currentUser.getUid()));
            usuario.setEmail(sharedPref.getString("email", currentUser.getEmail()));
            usuario.setNome(sharedPref.getString("nome", currentUser.getDisplayName()));
            usuario.setFoto(sharedPref.getString("foto", currentUser.getPhotoUrl().toString()));
        }
    }

    private void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    @Override
    public void onClick(View view){
        int i = view.getId();
        if (i == R.id.sign_in_button) {
            signIn(view);
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
        Log.i("Login","Botão de login clicado");
        if (valido) {
            Log.i("Login","Botão de login clicado - campos válidos");
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
            Log.i("Login","Iniciando autenticação de "+usuario+" - "+senha);
            Observable<Usuario> observable = api.authUsuario(json);
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Usuario>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("Login",e.getMessage());
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
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNext(Usuario response) {
                            Log.i("Login","Usuário autenticado");
                            //Registra login do usuário
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("logado", true);
                            editor.putString("id", response.getId());
                            editor.putString("email", response.getEmail());
                            editor.putString("matricula", response.getMatricula());
                            editor.putString("nascimento", response.getNascimento());
                            editor.putString("nome", response.getNome());
                            editor.putString("senha", response.getSenha());
                            editor.putString("sexo", response.getSexo());
                            editor.putString("foto", response.getFoto());
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
                            usuario.setFoto(response.getFoto());

                            //Cadastra token do dispositivo para receber notificações
                            cadastraToken(response);
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
    private void cadastraToken(Usuario response){
        //Cria objeto para acessar a API de dados Siseventos
        RetrofitAPI retrofit = new RetrofitAPI();
        final Api api = retrofit.retrofit().create(Api.class);

        UsuarioSingleton usuario = UsuarioSingleton.getInstance();

        //Verifica se o usuário possui um token para este dispositivo
        sharedPref = getBaseContext().
                getSharedPreferences("UFVEVENTOS" + response.getId(), Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "falso");
        if (token.equals("falso")){
            //Requisita token FCM
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            usuario.setToken(refreshedToken);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token",refreshedToken);
            editor.commit();

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
            usuario.setToken(token);
            //Encerra barra de carregamento
            progressBar.setVisibility(View.GONE);

            //Dispara intent para a tela inicial
            Intent it = new Intent(getBaseContext(),inicial.class);
            startActivity(it);
        }
    }
}