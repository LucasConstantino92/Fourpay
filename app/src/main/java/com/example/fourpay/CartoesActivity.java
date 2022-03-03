package com.example.fourpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CartoesActivity extends AppCompatActivity {

    CheckBox comum, black;
    android.widget.Button btn;
    ImageView back;
    /*ToggleButton toggle;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoes);

        comum = findViewById(R.id.chk_comum);
        black = findViewById(R.id.chk_black);
        btn = findViewById(R.id.btn_prosseguir);
        back = findViewById(R.id.backBtn);
        /*toggle = findViewById(R.id.togglebtn);*/

        comum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comum.isChecked() && !black.isChecked()){
                    btn.setVisibility(View.VISIBLE);
                }

                if(comum.isChecked() && black.isChecked()) {
                    Toast.makeText(CartoesActivity.this, "Você só pode selecionar 1 cartão. \n Cartão Black desmarcado.", Toast.LENGTH_LONG).show();
                    black.setChecked(false);
                }

                if(!comum.isChecked()){
                    btn.setVisibility(View.INVISIBLE);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        /*toggle.setButtonDrawable(R.drawable.ic_eye);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggle.isChecked()){
                    toggle.setButtonDrawable(R.drawable.ic_eye_off);
                }
                else
                {
                    toggle.setButtonDrawable(R.drawable.ic_eye);
                }

            }
        });*/

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(black.isChecked() && !comum.isChecked()){
                    btn.setVisibility(View.VISIBLE);
                }

                if(comum.isChecked() && black.isChecked()){
                    Toast.makeText(CartoesActivity.this, "Você só pode selecionar 1 cartão \n Cartão Standard desmarcado", Toast.LENGTH_LONG).show();
                    comum.setChecked(false);
                    btn.setVisibility(View.VISIBLE);
                }

                if(!black.isChecked()){
                    btn.setVisibility(View.INVISIBLE);
                }
            }
        });


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartoesActivity.this,HomeActivity.class));
            }
        });
    }




}