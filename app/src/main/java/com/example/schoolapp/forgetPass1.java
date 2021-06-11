package com.example.schoolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class forgetPass1 extends AppCompatActivity {

    Button forgetPassNext;
    ImageView fb1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass1);


        forgetPassNext = findViewById(R.id.forgetPass_next);
        fb1 = findViewById(R.id.fp1_backBtn);


        forgetPassNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), forgetPassword2.class);
                startActivity(intent);


            }
        });

        fb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);


            }
        });

    }
}
