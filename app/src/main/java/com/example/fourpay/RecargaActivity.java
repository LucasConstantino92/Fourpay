package com.example.fourpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class RecargaActivity extends AppCompatActivity {

    ImageView backBtn;
    EditText edt_celular, edt_valorrecarga, forma;
    android.widget.Button btn_prosseguir;
    Spinner spin_operadoras;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recarga);

        backBtn = findViewById(R.id.backBtn);
        edt_celular = findViewById(R.id.edt_valoremprestimo);
        edt_valorrecarga = findViewById(R.id.edt_valorrecarga);
        forma = findViewById(R.id.edt4);
        btn_prosseguir = findViewById(R.id.btn_prosseguir);
        spin_operadoras = findViewById(R.id.spin_operadoras);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        mascaraCelular();
        mascaraRenda();


        btn_prosseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valor = edt_valorrecarga.getText().toString();
                valor = valor.replaceAll("\\D+","");
                StringBuilder renda2 = new StringBuilder(valor);
                renda2.insert(valor.length()-2, ".");
                recargaCelular(Double.parseDouble(String.valueOf(renda2)));
            }
        });

        forma.setEnabled(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void mascaraCelular(){
        SimpleMaskFormatter maskCPF = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(edt_celular, maskCPF);
        edt_celular.addTextChangedListener(mtw);
    }

    private void mascaraRenda(){
        Locale mLocale = new Locale("pt", "BR");
        edt_valorrecarga.addTextChangedListener(new MascaraMonetaria(edt_valorrecarga, mLocale));
    }

    public void recargaCelular(double valorrecarga) {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                if(userData != null){
                    String nome = userData.nome;
                    double renda = userData.renda;
                    if(valorrecarga > renda){
                        Toast.makeText(RecargaActivity.this, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double saldoAt = renda - valorrecarga;
                    saldoAt = round(saldoAt,2);
                    reference.child(userID).child("renda").setValue(saldoAt);
                    startActivity(new Intent(RecargaActivity.this, HomeActivity.class));
                    Toast.makeText(RecargaActivity.this, "Recarga de R$"+valorrecarga+" realizada com sucesso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecargaActivity.this, "Oops! Algo deu errado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}