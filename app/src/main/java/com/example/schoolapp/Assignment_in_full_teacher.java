package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schoolapp.adapters.AdapterAssignmentTeacher;
import com.example.schoolapp.model.ModelAss;
import com.example.schoolapp.model.ModelAssignment_T;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class Assignment_in_full_teacher extends AppCompatActivity {

    private String AssignmentID;
    ImageButton AssignmentShowT_backBtn, writeFeedBackBtnT;
    CardView assignmentInFull_T;
    TextView ADsuject, ADtopic, ADclass, ADdg, ADdos, ADassID, AssignmentStatusTVT;
    EditText AssiTyped;
    ImageView AssiImage;


    private FirebaseAuth firebaseAuth;

    Animation topAnim, bottomAnim, middleAnim;

    private ArrayList<ModelAss> modelAssList;
    private AdapterAssignmentTeacher adapterAssignmentTeacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_in_full_teacher);

        AssignmentShowT_backBtn = findViewById(R.id.AssignmentShowT_backBtn);
        writeFeedBackBtnT = findViewById(R.id.writeFeedBackBtnT);
        assignmentInFull_T = findViewById(R.id.assignmentInFull_T);
        ADsuject = findViewById(R.id.AssignmentSubjectTvT);
        ADtopic = findViewById(R.id.AssignmentTopicAsTvT);
        ADclass = findViewById(R.id.AssignmentClassAsTvT);
        ADdg = findViewById(R.id.AssignmentDateGivenAsTvT);
        ADdos = findViewById(R.id.AssignmentDateSubmissionTvT);
        ADassID = findViewById(R.id.AssignmentIdTvT);
        AssiTyped = findViewById(R.id.assignmentypedd);
        AssiImage = findViewById(R.id.AssignmentImage);
        AssignmentStatusTVT = findViewById(R.id.AssignmentStatusTVT);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        assignmentInFull_T.setAnimation(middleAnim);

        Intent intent = getIntent();
        AssignmentID = intent.getStringExtra("AssignmentID");


        AssignmentShowT_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TeacherHomePage.class);
                startActivity(intent);
            }
        });

        writeFeedBackBtnT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), teacherFeedback.class);
                startActivity(intent);

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        loadAssignmentDetails();
       // loadAssignmentItems();

    }


    private void loadAssignmentDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Assignments").child(AssignmentID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // First get data
                        String AssignmentClass = ""+dataSnapshot.child(" AssignmentClass").getValue();
                        String AssignmentDG = ""+dataSnapshot.child("AssignmentDG").getValue();
                        String AssignmentID = ""+dataSnapshot.child("AssignmentID").getValue();
                        String AssignmentDtoS = ""+dataSnapshot.child("AssignmentDtoS:").getValue();
                        String AssignmentTopic = ""+dataSnapshot.child("AssignmentTopic").getValue();
                      //  String orderTo = ""+dataSnapshot.child("orderTo").getValue();
                        String AssignmentTyped = ""+dataSnapshot.child("AssignmentTyped:").getValue();
                        String AssignmentSubject = ""+dataSnapshot.child("AssignmentSubject").getValue();
                        String TassStatus = ""+dataSnapshot.child("AssignmentStatus").getValue();
                        String AssignImage = ""+dataSnapshot.child("AssignmentImage").getValue();
                      //  String Assignpdf = ""+dataSnapshot.child("AssignmentPDF").getValue();

                        // TassignmentLogDateGivenTv.setText(TassDG);

                     //   Calendar calendarr = Calendar.getInstance();
                    //    calendar.setTimeInMillis(Long.parseLong(AssignmentDtoS));
                   //     String formatedDatee = DateFormat.format("dd/MM/yyyy  hh:mm a", calendarr).toString();

                        if (TassStatus.equals("In Progress")){
                            AssignmentStatusTVT.setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                        else if (TassStatus.equals("Done")){
                            AssignmentStatusTVT.setTextColor(getResources().getColor(R.color.colorBlue));
                        }
                        else if (TassStatus.equals("Not Done")) {
                            AssignmentStatusTVT.setTextColor(getResources().getColor(R.color.colorRed));
                        }
                        else if (TassStatus.equals("Cancelled")) {
                            AssignmentStatusTVT.setTextColor(getResources().getColor(R.color.coloryellowDark));
                        }

                        // Set data
                        ADassID.setText(AssignmentID);
                        ADsuject.setText(AssignmentSubject);
                        ADtopic.setText(AssignmentTopic);
                        ADclass.setText(AssignmentClass);
                        AssignmentStatusTVT.setText(TassStatus);
                        ADdg.setText(AssignmentDG);
                        ADdos.setText(AssignmentDtoS);
                        AssiTyped.setText(AssignmentTyped);


                        try {
                            Picasso.get().load(AssignImage).placeholder(R.drawable.ic_person_black_24dp).into(AssiImage);
                        } catch (Exception e) {
                            AssiImage.setImageResource(R.drawable.bookerass);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }



}
