package com.example.fourpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class ChavePixActivity extends AppCompatActivity {

    ImageView back;
    android.widget.Button btn_prosseguir, btn_cancelar;
    TextView label1, label2;
    TextInputEditText edt_valor, edt_chave;
    TextInputLayout layout_chave, layout_valor;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chave_pix);

        back = findViewById(R.id.backBtn);
        edt_chave = findViewById(R.id.edt_chave);
        edt_valor = findViewById(R.id.edt_valorpix);
        label1 = findViewById(R.id.textView4);
        label2 = findViewById(R.id.textView5);
        edt_valor = findViewById(R.id.edt_valorpix);
        back = findViewById(R.id.backBtn);
        btn_prosseguir = findViewById(R.id.btn_prosseguir);
        btn_cancelar = findViewById(R.id.btn_cancelar);
        layout_chave = findViewById(R.id.layout_chave);
        layout_valor = findViewById(R.id.layout_valor);

        mascaraRenda();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_prosseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valor = edt_valor.getText().toString();
                valor = valor.replaceAll("\\D+","");
                StringBuilder renda2 = new StringBuilder(valor);
                renda2.insert(valor.length()-2, ".");

                Toast.makeText(ChavePixActivity.this, "Confirme os dados", Toast.LENGTH_SHORT).show();
                label1.setText("Chave Pix");
                label2.setText("Valor do Pix");
                //edt_chave.setEnabled(false);
                //edt_valor.setEnabled(false);
                btn_cancelar.setVisibility(View.VISIBLE);
                btn_prosseguir.setText("Confirmar transferência");

                btn_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

                btn_prosseguir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fazerPix(Double.parseDouble(String.valueOf(renda2)));
                    }
                });
            }
        });

    }

    private void mascaraRenda(){
        Locale mLocale = new Locale("pt", "BR");
        edt_valor.addTextChangedListener(new MascaraMonetaria(edt_valor, mLocale));
    }

    public void fazerPix(double valor) {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                if(userData != null){
                    String nome = userData.nome;
                    double renda = userData.renda;
                    if(valor > renda){
                        Toast.makeText(ChavePixActivity.this, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double saldoAt = renda - valor;
                    saldoAt = round(saldoAt,2);
                    reference.child(userID).child("renda").setValue(saldoAt);
                    startActivity(new Intent(ChavePixActivity.this, HomeActivity.class));
                    Toast.makeText(ChavePixActivity.this, "Pix de R$"+valor+" realizado com sucesso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChavePixActivity.this, "Oops! Algo deu errado", Toast.LENGTH_SHORT).show();
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