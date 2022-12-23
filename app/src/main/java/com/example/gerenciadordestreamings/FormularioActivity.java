package com.example.gerenciadordestreamings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormularioActivity extends AppCompatActivity {

    private EditText etNome;
    private Spinner spCategoria;

    private Button btnSalvar;
    private String acao;
    private Produto produto;

    private RadioButton rbAssistido;
    private RadioButton rbnaoAssistido;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        etNome = findViewById(R.id.etNome);
        spCategoria = findViewById(R.id.spCategoria);
        btnSalvar = findViewById(R.id.btnSalvar);

        rbAssistido = findViewById(R.id.rbAssistido);
        rbnaoAssistido = findViewById(R.id.rbNaoAssistido);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvar();
            }
        });

        acao = getIntent().getStringExtra("acao");
        if(acao.equals("editar")){
            carregarFormulario();
        }

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser() == null){
                    finish();
                }
            }
        };
        auth.addAuthStateListener(authStateListener);
    }

    private void carregarFormulario(){
        String idProduto = getIntent().getStringExtra("idProduto");

        produto = new Produto();
        produto.setId(idProduto);
        produto.setNomeConteudo(getIntent().getStringExtra("nome"));
        produto.setCategoriaConteudo(getIntent().getStringExtra("categoria"));
        etNome.setText(produto.getNomeConteudo());

        String[] categoria = getResources().getStringArray(R.array.strCategoriasProduto);
        for(int i = 0; i < categoria.length; i++){
            if(produto.getCategoriaConteudo().equals(categoria[i])){
                spCategoria.setSelection(i);
                break;
            }
        }
    }


    private void salvar(){
        String nome = etNome.getText().toString();
        if(nome.isEmpty() || spCategoria.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Você deve preencher todos os campos. Tente novamente!", Toast.LENGTH_LONG).show();
        }
        else{
            if(acao.equals("inserir")){
                produto = new Produto();
            }
            produto.setNomeConteudo(nome);
            produto.setCategoriaConteudo(spCategoria.getSelectedItem().toString());
            produto.setIdUsuario(auth.getCurrentUser().getUid());

            if(rbAssistido.isChecked()){
                produto.setAssistido(rbAssistido.getText().toString());
            }else{
                produto.setAssistido("Não assistido");
            }

            if(acao.equals("inserir")){
                reference.child("produtos").push().setValue(produto);
                etNome.setText("");
                spCategoria.setSelection(0, true);
            }
            else{
                String idProduto = produto.getId();
                produto.setId(null);
                reference.child("produtos").child(idProduto).setValue(produto);
                finish();
            }
        }
    }

}