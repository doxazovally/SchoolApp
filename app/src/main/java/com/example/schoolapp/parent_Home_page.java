package com.example.schoolapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.AdapterKidShow;
import com.example.schoolapp.model.ModelKid;
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

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class parent_Home_page extends AppCompatActivity {

    CircleImageView PprofileImage;
    ImageView filterKidBtn, logout, PPeditBtn, PP_addKid_Btn;
    TextView PName, PEmail, PPhone, tabKid, tabAddKid, filteredKid;
    EditText searchKid;

    private RelativeLayout KidRl;
    private RecyclerView kidRv;

    private RelativeLayout AssignmentRlP;
    private RecyclerView AssignmentRvP;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelKid> kidList;
    private AdapterKidShow adapterKidShow;


    Animation topAnim, bottomAnim, middleAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent__home_page);

        PName = findViewById(R.id.sM_Name);
        PEmail = findViewById(R.id.sM_email);
        PPhone = findViewById(R.id.sM_shop);
        logout = findViewById(R.id.kidLogout);
        PPeditBtn = findViewById(R.id.PP_edit_Btn);
        filterKidBtn = findViewById(R.id.filterKidClassBtn);
        filteredKid = findViewById(R.id.filteredKidTv);
        searchKid = findViewById(R.id.searchKidClassView);
        tabKid = findViewById(R.id.tabkid);
        tabAddKid = findViewById(R.id.tabAddKid);
        PprofileImage = findViewById(R.id.PprofileImage);
        PP_addKid_Btn = findViewById(R.id.PP_addKid_Btn);
        KidRl = findViewById(R.id.KidRl);
        kidRv = findViewById(R.id.KidRv);
        AssignmentRvP = findViewById(R.id.AssignmentRvP);
        AssignmentRlP = findViewById(R.id.AssignmentRlP);




        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        kidRv.setAnimation(middleAnim);
        AssignmentRvP.setAnimation(bottomAnim);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadAllKid();
        showKidUI();
        showKidAssignments();


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeoffline();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();


            }
        });



        tabKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load Kids
                showKidUI();

            }
        });

        tabAddKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load Assignments
                showKidAssignments();


            }
        });

        PP_addKid_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), addKidPage.class);
                startActivity(intent);
                finish();

            }
        });


        filterKidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent_Home_page.this);
                builder.setTitle("Filter Kid:")
                        .setItems(Constants.classCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get selected item
                                String selected = Constants.classCategory1[which];
                                filteredKid.setText(selected);
                                if (selected.equals("All")) {
                                    //load all
                                    loadAllKid();
                                } else {
                                    // Load filtered
                                    loadFilteredKId(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        PPeditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), parentProfile.class);
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
                        Toast.makeText(parent_Home_page.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(parent_Home_page.this, Login.class));
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
                            PName.setText(name);
                            PPhone.setText(phone);
                            PEmail.setText(email);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black_24dp).into(PprofileImage);
                            } catch (Exception e) {
                                PprofileImage.setImageResource(R.drawable.ic_person_black_24dp);
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadFilteredKId(final String selected) {
        kidList = new ArrayList<>();

        //Get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Kids")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Before getting reset list
                        kidList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String kidClass = "" + ds.child("KidClass").getValue();

                            // If selected class matches kid class, then add in list
                            if (selected.equals(kidClass)) {
                                ModelKid modelKid = ds.getValue(ModelKid.class);
                                kidList.add(modelKid);
                            }

                        }

                        // Setup adapter
                        adapterKidShow = new AdapterKidShow(parent_Home_page.this, kidList);
                        // Set adapter
                        kidRv.setAdapter(adapterKidShow);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadAllKid() {
        kidList = new ArrayList<>();

        //Get all kid
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Kids")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Before getting reset list
                        kidList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ModelKid modelKid = ds.getValue(ModelKid.class);
                            kidList.add(modelKid);
                        }

                        // Setup adapter
                        adapterKidShow = new AdapterKidShow(parent_Home_page.this, kidList);
                        // Set adapter
                        kidRv.setAdapter(adapterKidShow);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    private void showKidUI() {
        // Show products UI and hide orders UI
        KidRl.setVisibility(View.VISIBLE);

        tabKid.setTextColor(getResources().getColor(R.color.colorPrimary));
        tabKid.setBackgroundResource(R.drawable.shape_rec_05);

        tabAddKid.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tabAddKid.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }


    private void showKidAssignments() {
        // Load Assignments

        tabKid.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tabKid.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabAddKid.setTextColor(getResources().getColor(R.color.colorPrimary));
        tabAddKid.setBackgroundResource(R.drawable.shape_rec_05);


    }


}
