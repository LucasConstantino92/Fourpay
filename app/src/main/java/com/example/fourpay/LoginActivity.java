package com.example.fourpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText edt_email, edt_senha;
    android.widget.Button entrar;
    ImageView backBtn;
    TextView tv_redefinir;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        edt_email = findViewById(R.id.edt_email);
        edt_senha = findViewById(R.id.edt_password);
        entrar = findViewById(R.id.btn_registrar);
        backBtn = findViewById(R.id.backBtn);
        tv_redefinir = findViewById(R.id.tv_redefinir);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logarUsuario();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_redefinir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RedefinirSenhaActivity.class));
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void logarUsuario(){

        String email = edt_email.getText().toString().trim();
        String senha = edt_senha.getText().toString().trim();

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

        if(senha.isEmpty()){
            edt_senha.setError("Senha é obrigatório");
            edt_senha.requestFocus();
            return;
        }

        if(senha.length() < 6){
            edt_senha.setError("Senha precisa ter no mínimo 6 caracteres");
            edt_senha.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //LOGAR PRA HOME
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Usuário e/ou senha incorretos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}