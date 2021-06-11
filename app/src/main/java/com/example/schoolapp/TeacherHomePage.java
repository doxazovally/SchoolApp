package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schoolapp.adapters.AdapterAssignmentTeacher;
import com.example.schoolapp.adapters.AdapterKidShow;
import com.example.schoolapp.adapters.AdapterTeacherKidShow;
import com.example.schoolapp.model.ModelAss;
import com.example.schoolapp.model.ModelAssignment_T;
import com.example.schoolapp.model.ModelKid;
import com.example.schoolapp.model.ModelTKid;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherHomePage extends AppCompatActivity {

    ImageButton TeacherHomeLogoutBtn, filterKidClassBtn, TP_edit_Btn, Tlogout, TP_addAss_Btn;
    CircleImageView TprofileImage;
    EditText TsearchKidClassView;
    TextView TM_Name, TM_phone, TM_email, tabAssignment, tabStudents, filteredKidTv;

    private RelativeLayout TKidRl;
    private RecyclerView studentRv;

    private RelativeLayout AssignmentRl;
    private RecyclerView AssignmentRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelTKid> kidTList;
    private AdapterTeacherKidShow adapterKidTShow;

    private ArrayList<ModelAss> modelAssList;
    private AdapterAssignmentTeacher adapterAssignmentTeacher;


    Animation topAnim, bottomAnim, middleAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home_page);


        TeacherHomeLogoutBtn = findViewById(R.id.teacher_home_logoutBtn);
        filteredKidTv = findViewById(R.id.filteredKidTv);
        TP_edit_Btn = findViewById(R.id.TP_edit_Btn);
        Tlogout = findViewById(R.id.teacher_home_logoutBtn);
        TeacherHomeLogoutBtn = findViewById(R.id.teacher_home_logoutBtn);
        TprofileImage = findViewById(R.id.TprofileImage);
        filterKidClassBtn = findViewById(R.id.filterKidClassBtn);
        TM_Name = findViewById(R.id.TM_Name);
        TM_email = findViewById(R.id.TM_email);
        TM_phone = findViewById(R.id.TM_phone);
        tabAssignment = findViewById(R.id.tabAssignment);
        tabStudents = findViewById(R.id.tabStudents);
        studentRv = findViewById(R.id.studentRv);
        TP_addAss_Btn = findViewById(R.id.TP_addAss_Btn);
        TKidRl = findViewById(R.id.TKidRl);
        TsearchKidClassView = findViewById(R.id.TsearchKidClassView);
        AssignmentRl = findViewById(R.id.AssignmentRl);
        AssignmentRv = findViewById(R.id.AssignmentRv);



        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        studentRv.setAnimation(middleAnim);
        AssignmentRv.setAnimation(bottomAnim);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadAllKid();
       // showKidUI();
       // showAssignmentGiven();





        TeacherHomeLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                makeMeoffline();
            }
        });


        tabStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load products
                showKidUI();

            }
        });

        tabAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load orders
                showAssignmentGiven();


            }
        });



        filterKidClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherHomePage.this);
                builder.setTitle("Filter Kid:")
                        .setItems(Constants.classCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get selected item
                                String selected = Constants.classCategory1[which];
                                filteredKidTv.setText(selected);
                                if (selected.equals("All")) {
                                    //load all
                                    loadAllKid();
                                } else {
                                    // Load filtered
                                    loadFilteredkid(selected);
                                }
                            }
                        })
                        .show();
            }
        });


        TP_addAss_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), giveAssignment.class);
                startActivity(intent);

            }
        });


        TP_edit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), TeacherProfile.class);
                startActivity(intent);

            }
        });
    }



    private void makeMeoffline() {

        //Make user online after logging in
        progressDialog.setMessage("Logging Out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online", "false");

        //Updating info to database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Update successful
                        firebaseAuth.signOut();
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Update failed
                        progressDialog.dismiss();
                        Toast.makeText(TeacherHomePage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(TeacherHomePage.this, Login.class));
            finish();
        } else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Get user data
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String phone = "" + ds.child("phoneNum").getValue();
                            String email = "" + ds.child("email").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String accountType = "" + ds.child("accountType").getValue();

                            // Set user data
                            TM_Name.setText(name);
                            TM_phone.setText(phone);
                            TM_email.setText(email);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black_24dp).into(TprofileImage);
                            } catch (Exception e) {
                                TprofileImage.setImageResource(R.drawable.ic_person_black_24dp);
                            }

                            loadAssignment();


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadAssignment() {

        // Initiate order List
        modelAssList = new ArrayList<>();

        // Buyer Get orders
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelAssList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Assignments");
                    ref.orderByChild("assId").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            ModelAss modelAss = ds.getValue(ModelAss.class);

                                            // Add to list
                                            modelAssList.add(modelAss);
                                        }

                                        // Set to adapter
                                        adapterAssignmentTeacher = new AdapterAssignmentTeacher(TeacherHomePage.this, modelAssList);

                                        // set to recyclerview
                                        AssignmentRv.setAdapter(adapterAssignmentTeacher);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFilteredkid(final String selected) {
        kidTList = new ArrayList<>();

        //Get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Before getting reset list
                        kidTList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String uid = ""+ds.getRef().getKey();


                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Kids");
                            ref.orderByChild("accountType").equalTo("Umuaka")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){

                                                    String kidClass = "" + ds.child("KidClass").getValue();


                                                    // If selected class matches kid class, then add in list
                                                    if (selected.equals(kidClass)) {
                                                        ModelTKid modelTKid = ds.getValue(ModelTKid.class);
                                                        kidTList.add(modelTKid);
                                                    }


                                                }

                                                // Setup Adapter
                                                adapterKidTShow = new AdapterTeacherKidShow(TeacherHomePage.this, kidTList);

                                                // Setup Adapter for recyclerView
                                                studentRv.setAdapter(adapterKidTShow);

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                });

   }


    private void loadAllKid() {
        kidTList = new ArrayList<>();

        //Get all kid
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        // Clear list before adding kid
                        kidTList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            String uid = ""+ds.getRef().getKey();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Kids");
                            ref.orderByChild("accountType").equalTo("Umuaka")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    ModelTKid modelTKid = ds.getValue(ModelTKid.class);

                                                    // displaying all Kids to List
                                                    kidTList.add(modelTKid);
                                                }

                                                // Setup Adapter
                                                adapterKidTShow = new AdapterTeacherKidShow(TeacherHomePage.this, kidTList);

                                                // Setup Adapter for recyclerView
                                                studentRv.setAdapter(adapterKidTShow);

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }



    private void showKidUI() {
        // Show Kids UI and hide orders UI
        TKidRl.setVisibility(View.VISIBLE);
        AssignmentRl.setVisibility(View.GONE);

        tabStudents.setTextColor(getResources().getColor(R.color.colorPrimary));
        tabStudents.setBackgroundResource(R.drawable.shape_rec_05);

        tabAssignment.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tabAssignment.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showAssignmentGiven() {
        // Show Given Assignment UI and hide orders UI
        AssignmentRl.setVisibility(View.VISIBLE);
        TKidRl.setVisibility(View.GONE);


        tabStudents.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tabStudents.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabAssignment.setTextColor(getResources().getColor(R.color.colorPrimary));
        tabAssignment.setBackgroundResource(R.drawable.shape_rec_05);

    }







}
