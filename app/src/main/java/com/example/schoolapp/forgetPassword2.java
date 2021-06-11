package com.example.schoolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class forgetPassword2 extends AppCompatActivity {

    ImageView fb2;
    Button otpResetMobile, otpResetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password2);


        fb2 = findViewById(R.id.fp2_backBtn);
        otpResetMobile = findViewById(R.id.password_reset_mobile);
        otpResetEmail= findViewById(R.id.password_reset_email);



        fb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), forgetPass1.class);
                startActivity(intent);


            }
        });

        otpResetMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), PhoneValidation.class);
                startActivity(intent);


            }
        });

    }
}
