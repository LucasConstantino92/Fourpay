package com.example.fourpay;

public class User {

    public String nome;
    public String cpf;
    public String rg;
    public String email;
    public String celular;
    public double renda;

    public User(){

    }

    public User(String nome, String cpf, String rg, String email, String celular, double renda){
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.email = email;
        this.celular = celular;
        this.renda = renda;
    }

}
