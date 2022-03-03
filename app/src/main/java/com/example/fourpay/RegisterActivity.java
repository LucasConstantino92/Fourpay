package com.example.fourpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class RegisterActivity extends AppCompatActivity {

    EditText cep, edtRua, edtCidade, edtEstado, edtBairro;
    EditText edt_email, edt_cpf, edt_rg, edt_celular, edt_renda, edt_senha, edt_confirmaSenha, edt_nome, edt_data;
    TextView termos;
    android.widget.Button registrar;
    ScrollView sv_register;
    MaterialCheckBox checkbox;
    ImageView backBtn;
    private RequestQueue queue;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        //Para registro
        edt_email = findViewById(R.id.edt_email_register);
        edt_nome = findViewById(R.id.edt_nomeCompleto);
        edt_cpf = findViewById(R.id.edt_cpf);
        edt_rg = findViewById(R.id.edt_rg);
        edt_data = findViewById(R.id.edt_data);
        edt_celular = findViewById(R.id.edt_celular);
        checkbox = findViewById(R.id.checkbox);
        edt_renda = findViewById(R.id.edt_renda);
        edt_senha = findViewById(R.id.edt_senha);
        edt_confirmaSenha = findViewById(R.id.edt_confirmaSenha);
        sv_register = findViewById(R.id.sv_register);

        registrar = findViewById(R.id.btn_registrar);

        cep = findViewById(R.id.edt_cep);
        edtRua = findViewById(R.id.edt_rua);
        edtCidade = findViewById(R.id.edt_cidade);
        edtEstado = findViewById(R.id.edt_uf);
        edtBairro = findViewById(R.id.edt_bairro);

        backBtn = findViewById(R.id.backBtn);
        termos = findViewById(R.id.txtTermos);

        mascaraCelular();
        mascaraCep();
        mascaraCpf();
        mascaraData();
        mascaraRG();
        mascaraRenda();

        checkbox.setEnabled(false);

        queue = Volley.newRequestQueue(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        termos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, TermsActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });

        cep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String num = cep.getText().toString();
                consultarCEP(num);
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void consultarCEP(String cep) {
        String base = "https://viacep.com.br/ws/";
        String end = "/json";
        String url = base + cep + end;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String cep = response.getString("cep");
                            String rua = response.getString("logradouro");
                            String bairro = response.getString("bairro");
                            String cidade = response.getString("localidade");
                            String estado = response.getString("uf");
                            String ddd = response.getString("ddd");
                            edtRua.setText(rua);
                            edtCidade.setText(cidade);
                            edtBairro.setText(bairro);
                            edtEstado.setText(estado);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request);
    }

    private void registrarUsuario(){
        String nome = edt_nome.getText().toString().trim();
        String cpf = edt_cpf.getText().toString().trim();
        String rg = edt_rg.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String celular = edt_celular.getText().toString().trim();
        String renda = edt_renda.getText().toString().trim();
        String senha = edt_senha.getText().toString().trim();
        String confirma_senha = edt_confirmaSenha.getText().toString().trim();

        if(nome.isEmpty()){
            edt_nome.setError("Nome completo é obrigatório");
            edt_nome.requestFocus();
            return;
        }

        if(cpf.isEmpty()){
            edt_cpf.setError("CPF é obrigatório");
            edt_cpf.requestFocus();
            return;
        }

        if(rg.isEmpty()){
            edt_rg.setError("RG é obrigatório");
            edt_rg.requestFocus();
            return;
        }

        if(email.isEmpty()){
            edt_email.setError("Email é obrigatório");
            edt_email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edt_email.setError("Por favor insira um email válido");
            edt_email.requestFocus();
            return;
        }

        if(celular.isEmpty()){
            edt_celular.setError("Celular é obrigatório");
            edt_celular.requestFocus();
            return;
        }

        if(renda.isEmpty()){
            edt_renda.setError("Renda é obrigatório");
            edt_renda.requestFocus();
            return;
        }

        renda = renda.replaceAll("\\D+","");
        StringBuilder renda2 = new StringBuilder(renda);
        renda2.insert(renda.length()-2, ".");

        if(senha.isEmpty()){
            edt_senha.setError("Senha é obrigatório");
            edt_senha.requestFocus();
            return;
        }

        if(confirma_senha.isEmpty()){
            edt_confirmaSenha.setError("Confirmar senha é obrigatório");
            edt_confirmaSenha.requestFocus();
            return;
        }

        if(!senha.equals(confirma_senha)){
            edt_confirmaSenha.setError("As senhas são diferentes");
            edt_confirmaSenha.requestFocus();
            return;
        }

        if(senha.length() < 6){
            edt_senha.setError("Senha precisa ter no mínimo 6 caracteres");
            edt_senha.requestFocus();
            return;
        }

        if (!checkbox.isChecked()){
            Toast.makeText(this, "Você deve concordar com os Termos e Condições", Toast.LENGTH_SHORT).show();
            checkbox.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(nome, cpf, rg, email, celular, Double.parseDouble(String.valueOf(renda2)));
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Registrado com sucesso", Toast.LENGTH_SHORT).show();
                                        //REDIRECIONAR PARA O LOGIN
                                        startActivity(new Intent(RegisterActivity.this, FotoActivity.class));
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Falha ao registrar usuário", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }

    private void mascaraCpf(){
        SimpleMaskFormatter maskCPF = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(edt_cpf, maskCPF);
        edt_cpf.addTextChangedListener(mtw);
    }

    private void mascaraRG(){
        SimpleMaskFormatter maskCPF = new SimpleMaskFormatter("NN.NNN.NNN-N");
        MaskTextWatcher mtw = new MaskTextWatcher(edt_rg, maskCPF);
        edt_rg.addTextChangedListener(mtw);
    }

    private void mascaraCelular(){
        SimpleMaskFormatter maskCPF = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(edt_celular, maskCPF);
        edt_celular.addTextChangedListener(mtw);
    }

    private void mascaraData(){
        SimpleMaskFormatter maskCPF = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(edt_data, maskCPF);
        edt_data.addTextChangedListener(mtw);
    }

    private void mascaraCep(){
        SimpleMaskFormatter maskCPF = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher mtw = new MaskTextWatcher(cep, maskCPF);
        cep.addTextChangedListener(mtw);
    }

    private void mascaraRenda(){
        Locale mLocale = new Locale("pt", "BR");
        edt_renda.addTextChangedListener(new MascaraMonetaria(edt_renda, mLocale));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) {
            boolean check = data.getBooleanExtra("check", false);
            if(check){
                checkbox.setEnabled(true);
                checkbox.setChecked(true);
                sv_register.post(new Runnable() {
                    public void run() {
                        sv_register.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    }
}