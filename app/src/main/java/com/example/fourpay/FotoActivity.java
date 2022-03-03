package com.example.fourpay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class FotoActivity extends AppCompatActivity {

    android.widget.Button camera, galeria, btnSeguir;
    TextView tv_confirme, tv_labelConfirme;
    ImageView back;
    CircleImageView img;
    LottieAnimationView animation_check;
    LottieAnimationView animation_load;
    public static final int IMAG_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;
    public static final int requestcamera_code = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        animation_check = findViewById(R.id.animacao_check);
        animation_load = findViewById(R.id.animacao_load);

        camera = findViewById(R.id.btn_camera);
        galeria = findViewById(R.id.btn_gallery);
        img = findViewById(R.id.img_cam);
        back = findViewById(R.id.backBtn);
        btnSeguir = findViewById(R.id.buttonEnviar);
        tv_confirme = findViewById(R.id.txt_confirme);
        tv_labelConfirme = findViewById(R.id.txt_confirmeLabel);

        btnSeguir.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime perssion
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        //permission already granted
                        pickImageFromGallery( );
                    }
                }
                else {
                    //system os is less the marshmallow
                    pickImageFromGallery( );
                }
            }
        });

        if (ContextCompat.checkSelfPermission(FotoActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FotoActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FotoActivity.this, CartoesActivity.class));
            }
        });
    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAG_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                } else {
                    //permission was denied
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
    //handle result of picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAG_PICK_CODE) {
            // set image to image view
            btnSeguir.setVisibility(View.VISIBLE);
            animation_load.setVisibility(View.VISIBLE);
            animation_check.setVisibility(View.INVISIBLE);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_labelConfirme.setText("Validamos sua foto com sucesso!");
                    tv_confirme.setText("Foto validada");
                    animation_load.setVisibility(View.INVISIBLE);
                    animation_check.setVisibility(View.VISIBLE);
                    animation_check.playAnimation();
                    img.setImageURI(data.getData());
                }
            },3000);
        } else {
            assert data != null;
            Bitmap picture = (Bitmap) data.getExtras().get("data");
            btnSeguir.setVisibility(View.VISIBLE);
            animation_load.setVisibility(View.VISIBLE);
            animation_check.setVisibility(View.INVISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_labelConfirme.setText("Validamos sua foto com sucesso!");
                    tv_confirme.setText("Foto validada");
                    animation_load.setVisibility(View.INVISIBLE);
                    animation_check.setVisibility(View.VISIBLE);
                    animation_check.playAnimation();
                    img.setImageBitmap(picture);
                }
            },3000);
        }
    }

}