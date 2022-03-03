package com.example.fourpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RedefinirSenhaActivity extends AppCompatActivity {

    ImageView back;
    TextInputEditText edt_email;
    android.widget.Button btn_enviar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redefinir_senha);

        back = findViewById(R.id.backBtn);
        edt_email = findViewById(R.id.edt_email);
        btn_enviar = findViewById(R.id.btn_enviar);

        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperarSenha();
            }
        });

    }

    private void recuperarSenha(){

        String email = edt_email.getText().toString().trim();

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

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RedefinirSenhaActivity.this, "Verifique seu email para recuperar sua senha", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RedefinirSenhaActivity.this, "Algo deu errado, tente novamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}