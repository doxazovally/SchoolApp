package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherProfile extends AppCompatActivity {
    CircleImageView TPU_pic;
    ImageView TPbtn;
    EditText TPU_name, TPU_school, TPU_subject, TPU_class, TPU_phone;
    Button TUpdatebtn;
    TextView updateprofile;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private Uri image_uri;

    //Permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //Image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // permission arrays
    // private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    Animation topAnim, bottomAnim, middleAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile2);

        TPU_pic = findViewById(R.id.TPU_Ppic);
        TPU_name = findViewById(R.id.TPU_name);
        TPU_school = findViewById(R.id.TPU_school);
        TPU_subject = findViewById(R.id.TPU_subject);
        TPU_class = findViewById(R.id.TPU_class);
        TPU_phone = findViewById(R.id.TPU_phone);
        TUpdatebtn = findViewById(R.id.Update_TU_Btn);
        updateprofile = findViewById(R.id.update_T_Acc);

        TPbtn = findViewById(R.id.teacher_profile_back_btn);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        updateprofile.setAnimation(middleAnim);
        TPU_name.setAnimation(middleAnim);
        TPU_phone.setAnimation(middleAnim);
        TPU_school.setAnimation(middleAnim);
        TPU_class.setAnimation(middleAnim);
        TPU_subject.setAnimation(middleAnim);
        TPU_pic.setAnimation(middleAnim);
        updateprofile.setAnimation(middleAnim);
        TUpdatebtn.setAnimation(middleAnim);






        TPbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_from_top);
            }
        });

        TPU_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pick Image
                showImagedDialog();
            }
        });


        TUpdatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // register user
                inputData();
            }
        });
    }

    private String FullName, PhoneNumber, School, Class, Subject;

    private void inputData() {
        FullName = TPU_name.getText().toString().trim();
        PhoneNumber = TPU_phone.getText().toString().trim();
        School = TPU_school.getText().toString().trim();
        Class = TPU_class.getText().toString().trim();
        Subject = TPU_subject.getText().toString().trim();


        updateBtn();
    }

    private void updateBtn() {
        progressDialog.setMessage("Updating Profile...");
        progressDialog.show();

        final String timestamp = ""+System.currentTimeMillis();

        if(image_uri == null){
            //Save info without image

            //Setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            //  hashMap.put("uid", "" +firebaseAuth.getUid());
            hashMap.put("name", "" +FullName);
            hashMap.put("phoneNum", "" +PhoneNumber);
            hashMap.put("classTaught", "" +Class);
            hashMap.put("school", "" +School);
            hashMap.put("subjectTaught", "" +Subject);
            hashMap.put("timestamp", "" +timestamp);
            hashMap.put("profileImage", "");

            //Save to Firebase Database
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Database updated
                            progressDialog.dismiss();
                            Toast.makeText(TeacherProfile.this,"Profile Updated Successfully....", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to update to Firebase Database
                            progressDialog.dismiss();
                            Toast.makeText(TeacherProfile.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
        else{
            //Save info with image

            //Name and path of image
            String filePathAndName = "profile_images/" + ""+firebaseAuth.getUid();
            //Upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                //Setup data to save
                                HashMap<String, Object> hashMap = new HashMap<>();
                                //   hashMap.put("uid", "" +firebaseAuth.getUid());
                                hashMap.put("name", "" +FullName);
                                hashMap.put("phoneNum", "" +PhoneNumber);
                                hashMap.put("classTaught", "" +Class);
                                hashMap.put("school", "" +School);
                                hashMap.put("subjectTaught", "" +Subject);
                                hashMap.put("timestamp", "" +timestamp);
                                hashMap.put("profileImage", "" +downloadImageUri); // Url of the uploaded image

                                //Save to Firebase Database
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Database updated
                                                progressDialog.dismiss();
                                                Toast.makeText(TeacherProfile.this,"Profile Updated Successfully....", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to update to Firebase Database
                                                progressDialog.dismiss();
                                                Toast.makeText(TeacherProfile.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(TeacherProfile.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(getApplicationContext(), Login.class ));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();
                            String email = ""+ds.child("email").getValue();
                            String name = ""+ds.child("name").getValue();
                            String Online = ""+ds.child("Online").getValue();
                            String phone = ""+ds.child("phoneNum").getValue();
                            String tclass = ""+ds.child("classTaught").getValue();
                            String school = ""+ds.child("school").getValue();
                            String subject = ""+ds.child("subjectTaught").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String uid = ""+ds.child("uid").getValue();


                            TPU_name.setText(name);
                            TPU_phone.setText(phone);
                            TPU_class.setText(tclass);
                            TPU_school.setText(school);
                            TPU_subject.setText(subject);



                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black_24dp).into(TPU_pic);

                            }
                            catch (Exception e){
                                TPU_pic.setImageResource(R.drawable.ic_person_black_24dp);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void showImagedDialog() {
        //option to display in dialog
        String [] options = {"Camera", "Gallery"};

        //Image dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Handle clicks
                        if(which == 0){

                            //Camera clicked
                            if(checkCameraPermission()){
                                //camera permission allowed
                                pickFromCamera();
                            }
                            else {
                                //camera permission not allowed, request
                                requestCameraPermission();
                            }
                        }
                        else {
                            //Gallery clicked
                            if(checkStoragePermission()){
                                //Storage permission allowed
                                pickFromGallary();

                            }
                            else {
                                //Storage permission not allowed, request
                                requestStoragePermission();

                            }

                        }
                    }

                })
                .show();
    }

    private void pickFromGallary(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);


    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        // permission allowed
                        pickFromCamera();
                    } else {
                        // permission denied
                        Toast.makeText(this, "Camera permissions are important...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        // permission allowed
                        pickFromGallary();
                    } else {
                        // permission denied
                        Toast.makeText(this, "Storage permission is important...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE && data!= null){

                //Get picked image
                image_uri = data.getData();
                //Set to image
                TPU_pic.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //Set to image
                TPU_pic.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
