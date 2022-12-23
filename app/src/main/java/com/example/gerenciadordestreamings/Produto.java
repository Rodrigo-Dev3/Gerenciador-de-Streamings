package com.example.gerenciadordestreamings;

public class Produto {

    private String id;
    public String nome, categoria, idUsuario, assistido;

    public Produto(){

    }

    public Produto(String nome, String categoria){
        this.nome = nome;
        this.categoria = categoria;
    }

    public Produto(String id, String nome, String categoria){
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
    }

    public Produto(String id, String nome, String categoria, String idUsuario){
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString(){

        return "Nome: " + nome + " | Plataforma: " + categoria;
    }

    public String getIdUsuario(){

        return idUsuario;
    }

    public void setIdUsuario(String idUsuario){

        this.idUsuario = idUsuario;
    }

    public String getId(){

        return id;
    }

    public void setId(String id){

        this.id = id;
    }

    public String getNomeConteudo(){

        return nome;
    }

    public void setNomeConteudo(String nome){

        this.nome = nome;
    }

    public String getCategoriaConteudo(){

        return categoria;
    }

    public void setCategoriaConteudo(String categoria){

        this.categoria = categoria;
    }

    public String getAssistido(){

        return assistido;
    }

    public void setAssistido(String assistido){

        this.assistido = assistido;
    }

}
