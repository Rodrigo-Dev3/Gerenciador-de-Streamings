package com.example.gerenciadordestreamings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btnEntrar, btnCadastrar;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entrar();
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrar();
            }
        });

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                usuario = auth.getCurrentUser();
                if(usuario != null){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        auth.addAuthStateListener(authStateListener);
    }

    private void entrar(){
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Todos os campos devem ser preenchidos. Tente novamente!", Toast.LENGTH_LONG).show();
        }
        else{
            auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        usuario = auth.getCurrentUser();
                    }
                    else{
                        task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Algo deu errado. Tente novamente!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void cadastrar(){
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Todos os campos devem ser preenchidos. Tente novamente!", Toast.LENGTH_LONG).show();
        }
        else{
            auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        usuario = auth.getCurrentUser();
                    }
                    else{
                        task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Algo deu errado. Tente novamente!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}