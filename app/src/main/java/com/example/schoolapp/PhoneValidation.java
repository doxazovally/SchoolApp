package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneValidation extends AppCompatActivity {
    ImageView OTPPageCancel;
    PinView pinView;
    Button OPTverify;
    String number;

    String VerificationCodeBySystem;  // code sent from the system

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_validation);


        OTPPageCancel = findViewById(R.id.OTP_page_cancel);
        pinView = findViewById(R.id.pinview);
        OPTverify = findViewById(R.id.veryfy_OTP);



       // OTPPageCancel.setOnClickListener(new View.OnClickListener() {
       //     @Override
       //     public void onClick(View view) {
       //         onBackPressed();
        //    }
     //   });

        String PhoneNumber = getIntent().getStringExtra("phoneNum");


        sendVerificationCodeToUser(PhoneNumber);
    }

    private void sendVerificationCodeToUser(String PhoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(PhoneNumber)                                                 // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS)                                         // Timeout and unit
                .setActivity(this)                                                           // Activity (for callback binding
                .setCallbacks(mCallbacks)                                                    // OnVerificationStateChallengedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);



    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                    /* "s" represent means a string needs to declared from where the system will send the code
                    and it is represented and stored here in  "codeBySystem" */

                    super.onCodeSent(s, forceResendingToken);

                    VerificationCodeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if(code!=null){
                        pinView.setText(code);
                        verifyCode(code);
                    }

                }


                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    Toast.makeText(PhoneValidation.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            };


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationCodeBySystem,code);


        // signing in if user got code, entered it and is successful
        signInWithPhoneAuthCredential(credential);
    }



     private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneValidation.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(PhoneValidation.this, "Verification Successfull", Toast.LENGTH_SHORT);

                            Intent intent = new Intent(getApplicationContext(), NewPasswordPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else {
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                                Toast.makeText(PhoneValidation.this, "Verification Not Successfull! Try Again", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                });
     }


    public void callNextScreenfromOTP(View view){

        String code = pinView.getText().toString();
        if(!code.isEmpty()){

            verifyCode(code);
        }
       // startActivity(new Intent(getApplicationContext(), NewPasswordPage.class));

    }




}
