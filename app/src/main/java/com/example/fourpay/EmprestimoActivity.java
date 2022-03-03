package com.example.fourpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

public class EmprestimoActivity extends AppCompatActivity {

    ImageView back;
    TextInputEditText edt_valoremprestimo;
    android.widget.Button btn_prosseguir;
    TextView valor_emprestimo;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emprestimo);

        back = findViewById(R.id.backBtn);
        edt_valoremprestimo = findViewById(R.id.edt_valoremprestimo);
        btn_prosseguir = findViewById(R.id.btn_registrar);
        valor_emprestimo = findViewById(R.id.txt_saldo);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        mascaraRenda();

        btn_prosseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valor = edt_valoremprestimo.getText().toString();
                valor = valor.replaceAll("\\D+","");
                StringBuilder renda2 = new StringBuilder(valor);
                renda2.insert(valor.length()-2, ".");
                if(Double.parseDouble(String.valueOf(renda2)) > 5000){
                    Toast.makeText(EmprestimoActivity.this, "Valor indisponível", Toast.LENGTH_SHORT).show();

                } else {
                    valor_emprestimo.setTextColor(Color.parseColor("#1c1c1c"));
                    solicitarEmprestimo(Double.parseDouble(String.valueOf(renda2)));
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edt_valoremprestimo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String valor = edt_valoremprestimo.getText().toString();
                valor = valor.replaceAll("\\D+","");
                StringBuilder renda2 = new StringBuilder(valor);
                renda2.insert(valor.length()-2, ".");
                if(Double.parseDouble(String.valueOf(renda2)) > 5000){
                    valor_emprestimo.setTextColor(Color.parseColor("#EB443F"));
                    //Toast.makeText(EmprestimoActivity.this, "Valor indisponível", Toast.LENGTH_SHORT).show();
                } else {
                    valor_emprestimo.setTextColor(Color.parseColor("#1c1c1c"));
                }
                double valorRestante = 5000 - Double.parseDouble(String.valueOf(renda2));

                if(valorRestante > 0){
                    valor_emprestimo.setText("R$"+String.valueOf(valorRestante));
                }
                else {
                    valorRestante = 0;
                    valor_emprestimo.setText("R$"+String.valueOf(valorRestante));
                }
            }
        });
    }

    private void mascaraRenda(){
        Locale mLocale = new Locale("pt", "BR");
        edt_valoremprestimo.addTextChangedListener(new MascaraMonetaria(edt_valoremprestimo, mLocale));
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void solicitarEmprestimo(double valoremprestimo) {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                if(userData != null){
                    String nome = userData.nome;
                    double renda = userData.renda;
                    double saldoAt = renda + valoremprestimo;
                    saldoAt = round(saldoAt,2);
                    reference.child(userID).child("renda").setValue(saldoAt);
                    startActivity(new Intent(EmprestimoActivity.this, HomeActivity.class));
                    Toast.makeText(EmprestimoActivity.this, "Você recebeu um empréstimo de R$"+valoremprestimo, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmprestimoActivity.this, "Oops! Algo deu errado", Toast.LENGTH_SHORT).show();
            }
        });
    }

}