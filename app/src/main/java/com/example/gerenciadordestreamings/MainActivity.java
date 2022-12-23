package com.example.gerenciadordestreamings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvProdutos;
    private List<Produto> listaProdutos;
    private ArrayAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ChildEventListener childEventListener;
    private Query query;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fabInserir);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                intent.putExtra("acao", "inserir");
                startActivity(intent);
            }
        });

        lvProdutos = findViewById(R.id.lvProdutos);

        listaProdutos = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaProdutos);
        lvProdutos.setAdapter(adapter);

        lvProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                intent.putExtra("acao", "editar");
                Produto produtoSelecionadoEdicao = listaProdutos.get(position);
                intent.putExtra("idProduto", produtoSelecionadoEdicao.getId());
                intent.putExtra("nome", produtoSelecionadoEdicao.getNomeConteudo());
                intent.putExtra("categoria", produtoSelecionadoEdicao.getCategoriaConteudo());

                startActivity(intent);
            }
        });

        lvProdutos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                excluir(position);
                return true;
            }
        });

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser() == null){
                    finish();
                }
            }
        };

        auth.addAuthStateListener((authStateListener));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menuSair) {
            auth.signOut();
        }
        if(id == R.id.menuAddProduto) {
            Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
            intent.putExtra("acao", "inserir");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        super.onStart();
        listaProdutos.clear();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        query = reference.child("produtos").orderByChild("nome");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    String idUserProduto = snapshot.child("idUsuario").getValue(String.class);
                    if(idUserProduto.equals(auth.getCurrentUser().getUid())){
                        Produto produto = new Produto();
                        produto.setId(snapshot.getKey());
                        produto.setNomeConteudo(snapshot.child("nome").getValue(String.class));
                        produto.setCategoriaConteudo(snapshot.child("categoria").getValue(String.class));

                        produto.setIdUsuario(idUserProduto);

                        listaProdutos.add(produto);
                        adapter.notifyDataSetChanged();
                    }
                }catch(Exception e){

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(Produto p : listaProdutos){
                    if(p.getId().equals(snapshot.getKey())){
                        p.setNomeConteudo(snapshot.child("nome").getValue(String.class));
                        p.setCategoriaConteudo(snapshot.child("categoria").getValue(String.class));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for(Produto p : listaProdutos){
                    if(p.getId().equals(snapshot.getKey())){
                        listaProdutos.remove(p);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        query.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        query.removeEventListener(childEventListener);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        carregarLista();
    }

    private void excluir(int posicao){

        Produto produtoSelecionadoExcluir = listaProdutos.get(posicao);
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Deseja excluir o item selecionado?");
        alerta.setIcon(android.R.drawable.ic_delete);
        alerta.setNeutralButton("Cancelar", null);
        alerta.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                reference.child("produtos").child(produtoSelecionadoExcluir.getId()).removeValue();
            }
        });

        alerta.show();

    }

    private void carregarLista(){
        if(listaProdutos.size() == 0){
            Produto listaVazia = new Produto("Lista vazia", "");
            listaProdutos.add(listaVazia);
            lvProdutos.setEnabled(false);
        }
        else{
            lvProdutos.setEnabled(true);
        }
    }

}