package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIMER = 5000;

    private FirebaseAuth firebaseAuth;

    TextView appName, appName2;

    Animation topAnim, bottomAnim, middleAnim, slideAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make fullscreen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();


        appName = findViewById(R.id.app_name);
        appName2 = findViewById(R.id.app_name2);


        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);

       
        appName.setAnimation(topAnim);
        appName2.setAnimation(bottomAnim);



        //start login activity in after 3sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    //User not logged in, start login activity
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
                   else if(user.isEmailVerified()){
                //User is logged in, check user type

                     checkUserType();
                 }
                else{
                      startActivity(new Intent(MainActivity.this, Login.class));
                     Toast.makeText(MainActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                    // finish();
                    checkUserType();
                }



            }
        }, 5000);
    }


    private void checkUserType() {
        //If user is parent, start parent main screen
        //If user is teacher, start teacher main screen

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String accountType = ""+dataSnapshot.child("accountType").getValue();
                        if(accountType.equals("Parent")){

                            //User is parent
                            startActivity (new Intent(MainActivity.this, parent_Home_page.class));
                            finish();
                        }
                        else if(accountType.equals("Teacher")) {
                            //User is teacher

                            startActivity (new Intent(MainActivity.this, TeacherHomePage.class));
                            finish();
                        }

                        else {

                            startActivity (new Intent(MainActivity.this, Login.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
