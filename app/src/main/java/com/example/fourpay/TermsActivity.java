package com.example.fourpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class TermsActivity extends AppCompatActivity {

    ImageView backBtn;
    android.widget.Button concordar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        backBtn = findViewById(R.id.backBtn);
        concordar = findViewById(R.id.btn_concordar);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(1, new Intent().putExtra("check", false));
                finish();
            }
        });

        concordar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(0, new Intent().putExtra("check", true));
                finish();
            }
        });


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}