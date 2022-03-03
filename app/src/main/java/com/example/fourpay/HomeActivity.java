package com.example.fourpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import java.text.DecimalFormat;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    ImageButton olho;
    TextView saldo, fatura, faturablack, txt_nome;
    String valor, valorfatura, valorblack;
    LinearLayout pix, recarga, emprestimo, pagar;
    CircleImageView conta;
    int cont = 1;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        conta = findViewById(R.id.btn_conta);
        olho = findViewById(R.id.btn_eye);
        saldo = findViewById(R.id.saldo);
        fatura = findViewById(R.id.fatura_standard);
        txt_nome = findViewById(R.id.txt_nome);
        faturablack = findViewById(R.id.fatura_black);
        pix = findViewById(R.id.linear_pix);
        recarga = findViewById(R.id.linear_recarga);
        emprestimo = findViewById(R.id.linear_emprestimo);
        pagar = findViewById(R.id.linear_pagar);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        atualizarDados();

        olho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cont == 1){
                    olho.setImageResource(R.drawable.ic_eye_white_off);
                    saldo.setText(valor.replaceAll("(?s).", "*"));
                    //fatura.setText(valorfatura.replaceAll("(?s).", "*"));
                    //faturablack.setText(valorblack.replaceAll("(?s).", "*"));
                    cont = 0;
                }
                else{
                    olho.setImageResource(R.drawable.ic_eye_white);
                    saldo.setText(valor);
                    //fatura.setText(valorfatura);
                    //faturablack.setText(valorblack);
                    cont = 1;
                }
            }
        });


        conta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, PerfilActivity.class));
            }
        });

        pix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, PixActivity.class));
                //fazerPix(120.67);
            }
        });

        recarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RecargaActivity.class));
                //recargaCelular(30);
            }
        });

        emprestimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, EmprestimoActivity.class));
                //solicitarEmprestimo(5500.00);
            }
        });

        pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScannerActivity.class));
            }
        });

    }

    public void atualizarDados() {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                if(userData != null){
                    String nome = userData.nome;
                    double renda = userData.renda;
                    renda = round(renda,2);
                    String valorFormatado = new DecimalFormat("#,##0.00").format(renda);
                    valorFormatado = valorFormatado.replace(',','.');
                    StringBuilder valorFinal = new StringBuilder(valorFormatado);
                    valorFinal.insert(valorFormatado.length()-2, ",");
                    valorFinal.deleteCharAt(valorFormatado.length()-3);
                    saldo.setText(String.valueOf(valorFinal));
                    txt_nome.setText(nome);
                    Toast.makeText(HomeActivity.this, "Saldo atualizado!", Toast.LENGTH_SHORT).show();

                    valor = saldo.getText().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Oops! Algo deu errado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fazerPix(double valorpix) {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                if(userData != null){
                    String nome = userData.nome;
                    double renda = userData.renda;
                    double saldoAt = renda - valorpix;
                    saldoAt = round(saldoAt,2);
                    String valorFormatado = new DecimalFormat("#,##0.00").format(saldoAt);
                    valorFormatado = valorFormatado.replace(',','.');
                    StringBuilder valorFinal = new StringBuilder(valorFormatado);
                    valorFinal.insert(valorFormatado.length()-2, ",");
                    valorFinal.deleteCharAt(valorFormatado.length()-3);
                    saldo.setText(String.valueOf(valorFinal));
                    reference.child(userID).child("renda").setValue(saldoAt);
                    Toast.makeText(HomeActivity.this, "Pix de R$"+valorpix+" realizado com sucesso", Toast.LENGTH_SHORT).show();

                    valor = saldo.getText().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Oops! Algo deu errado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pagarBoleto(double valorboleto) {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                if(userData != null){
                    String nome = userData.nome;
                    double renda = userData.renda;
                    double saldoAt = renda - valorboleto;
                    saldoAt = round(saldoAt,2);
                    String valorFormatado = new DecimalFormat("#,##0.00").format(saldoAt);
                    valorFormatado = valorFormatado.replace(',','.');
                    StringBuilder valorFinal = new StringBuilder(valorFormatado);
                    valorFinal.insert(valorFormatado.length()-2, ",");
                    valorFinal.deleteCharAt(valorFormatado.length()-3);
                    saldo.setText(String.valueOf(valorFinal));
                    reference.child(userID).child("renda").setValue(saldoAt);
                    Toast.makeText(HomeActivity.this, "Boleto de R$"+valorboleto+" pago com sucesso", Toast.LENGTH_SHORT).show();

                    valor = saldo.getText().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Oops! Algo deu errado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void recargaCelular(double valorrecarga) {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                if(userData != null){
                    String nome = userData.nome;
                    double renda = userData.renda;
                    double saldoAt = renda - valorrecarga;
                    saldoAt = round(saldoAt,2);
                    String valorFormatado = new DecimalFormat("#,##0.00").format(saldoAt);
                    valorFormatado = valorFormatado.replace(',','.');
                    StringBuilder valorFinal = new StringBuilder(valorFormatado);
                    valorFinal.insert(valorFormatado.length()-2, ",");
                    valorFinal.deleteCharAt(valorFormatado.length()-3);
                    saldo.setText(String.valueOf(valorFinal));
                    reference.child(userID).child("renda").setValue(saldoAt);
                    Toast.makeText(HomeActivity.this, "Recarga de R$"+valorrecarga+" realizada com sucesso", Toast.LENGTH_SHORT).show();

                    valor = saldo.getText().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Oops! Algo deu errado", Toast.LENGTH_SHORT).show();
            }
        });
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
                    String valorFormatado = new DecimalFormat("#,##0.00").format(saldoAt);
                    valorFormatado = valorFormatado.replace(',','.');
                    StringBuilder valorFinal = new StringBuilder(valorFormatado);
                    valorFinal.insert(valorFormatado.length()-2, ",");
                    valorFinal.deleteCharAt(valorFormatado.length()-3);
                    saldo.setText(String.valueOf(valorFinal));
                    reference.child(userID).child("renda").setValue(saldoAt);
                    Toast.makeText(HomeActivity.this, "Você recebeu um empréstimo de R$"+valoremprestimo, Toast.LENGTH_SHORT).show();

                    valor = saldo.getText().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Oops! Algo deu errado", Toast.LENGTH_SHORT).show();
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




