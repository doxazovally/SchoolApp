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
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class editKidPage extends AppCompatActivity {

    private CircleImageView kidpic;
    ImageButton backBtn;
    private EditText kidName, kidSchool, kidTeacherName;
    private TextView kidClass;
    private Button updateKid;
    private  String KidId;


    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    Animation topAnim, bottomAnim, middleAnim;

    //Permission Constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //Image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //Image picked uri
    private Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kid_page);

        kidpic = findViewById(R.id.kidpic);
        kidName = findViewById(R.id.Ekid_Title);
        kidClass = findViewById(R.id.Ekid_Class);
        kidSchool = findViewById(R.id.Ekid_School);
        kidTeacherName = findViewById(R.id.Ekid_Teacher_name);
        updateKid = findViewById(R.id.Update_kid_Btn);
        KidId = getIntent().getStringExtra("KidId");


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);


        kidpic.setAnimation(bottomAnim);
        kidName.setAnimation(bottomAnim);
        kidClass.setAnimation(bottomAnim);
        kidSchool.setAnimation(bottomAnim);
        kidTeacherName.setAnimation(bottomAnim);
        updateKid.setAnimation(bottomAnim);
        backBtn = findViewById(R.id.edit_backBtn);

        firebaseAuth = FirebaseAuth.getInstance();


        loadKidDetails(); // to set on views

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        //Init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        kidpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show dialog to pick Image
                showImagedPick();

            }
        });

        kidClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pick from category
                classDialog();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), parent_Home_page.class);
                startActivity(intent);

            }
        });

        updateKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Flow:
                // 1. Input data
                // 2. Validate data
                // 3. Add data to db
                inputData();
            }
        });


    }



    private void loadKidDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Kids").child(KidId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get data
                        String KidId = ""+dataSnapshot.child("KidId").getValue();
                        String Kidname = ""+dataSnapshot.child("KidName").getValue();
                        String Kidclass = ""+dataSnapshot.child("KidClass").getValue();
                        String Kidschool = ""+dataSnapshot.child("KidSchool").getValue();
                        String Kidimage = ""+dataSnapshot.child("KidImage").getValue();
                        String KidteacherName = ""+dataSnapshot.child("KidTeacherName").getValue();
                        String timestamp = ""+dataSnapshot.child("timestamp").getValue();
                        String uid = ""+dataSnapshot.child("uid").getValue();


                        kidName.setText(Kidname);
                        kidClass.setText(Kidclass);
                        kidSchool.setText(Kidschool);
                        kidTeacherName.setText(KidteacherName);


                        try {
                            Picasso.get().load(Kidimage).placeholder(R.drawable.ic_person_black_24dp).into(kidpic);
                        }
                        catch (Exception e){
                            kidpic.setImageResource(R.drawable.ic_person_black_24dp);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String KIDNAME, KIDCLASS, KIDSCHOOL, KIDTEACHERNAME;

    private void inputData() {
        // (1) Input data
        KIDNAME = kidName.getText().toString().trim();
        KIDCLASS = kidClass.getText().toString().trim();
        KIDSCHOOL = kidSchool.getText().toString().trim();
        KIDTEACHERNAME = kidTeacherName.getText().toString().trim();


        // (2) Validate data
        if (TextUtils.isEmpty(KIDNAME)) {
            Toast.makeText(this, "Enter Your Kid Name...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(KIDCLASS)) {
            Toast.makeText(this, "Select Your Kid Class...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(KIDSCHOOL)) {
            Toast.makeText(this, "Enter Your Kid School...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(KIDTEACHERNAME)) {
            Toast.makeText(this, "Enter Your Kid's Teacher's name...", Toast.LENGTH_SHORT).show();
            return;
        }


        updateKidToDB();
    }

    private void updateKidToDB() {
        // Updating edited kid data to db
        progressDialog.setTitle("Updating Your Kid...");
        progressDialog.show();

        if(image_uri == null){
            //Update without image

            // Setup data to updated
            HashMap<String, Object> hashMap = new HashMap<>();
            //hashMap.put("KidId", "" + timestamp);
            hashMap.put("KidName", "" + KIDNAME);
            hashMap.put("KidClass", "" + KIDCLASS);
            hashMap.put("KidSchool", "" + KIDSCHOOL);
            hashMap.put("KidTeacherName", "" + KIDTEACHERNAME);
            hashMap.put("KidImage", ""); // no image uploaded
            hashMap.put("uid", "" + firebaseAuth.getUid());
            // hashMap.put("uid", "" +firebaseAuth.getUid());

            // Update to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Kids").child(KidId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Product updated to db
                            progressDialog.dismiss();
                            Toast.makeText(editKidPage.this,"Your Kid's profile has been updated successfully...", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to update data to Database
                            progressDialog.dismiss();
                            Toast.makeText(editKidPage.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            finish();

                        }
                    });
        }
        else {
            // Upload with image

            // First upload image to db

            // Name and path of image
            String filePathAndName = "product_images/" + ""+KidId; // Overide previous image using same id
            //Upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Imaged uploaded
                            // Get the uploaded image url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                // Image url received, upload to db

                                // Setup data to be uploaded
                                HashMap<String, Object> hashMap = new HashMap<>();
                                // hashMap.put("productId", "" +timestamp);
                                hashMap.put("KidName", "" + KIDNAME);
                                hashMap.put("KidClass", "" + KIDCLASS);
                                hashMap.put("KidSchool", "" + KIDSCHOOL);
                                hashMap.put("KidTeacherName", "" + KIDTEACHERNAME);
                                hashMap.put("KidImage", "" + downloadImageUri); // Url of the uploaded kid image
                                hashMap.put("uid", "" + firebaseAuth.getUid());

                                // Update to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Kids").child(KidId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Product updated to db
                                                progressDialog.dismiss();
                                                Toast.makeText(editKidPage.this,"Your Kid's Profile has been updated successfully...", Toast.LENGTH_SHORT).show();



                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to update data to Database
                                                progressDialog.dismiss();
                                                Toast.makeText(editKidPage.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();


                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to upload kid image
                            progressDialog.dismiss();
                            Toast.makeText(editKidPage.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    });
        }

    }


    private void classDialog() {
        //Class. Dialoge
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Class Category")
                .setItems(Constants.classCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //Get picked category
                        String category = Constants.classCategory[which];

                        // Set picked category
                        kidClass.setText(category);
                    }
                })
                .show();
    }

    private void showImagedPick() {
        //option to display in dialog
        String[] options = {"Camera", "Gallery"};

        //Image dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Handle clicks
                        if (which == 0) {

                            //Camera clicked
                            if (checkCameraPermission()) {
                                //camera permission allowed
                                pickFromCamera();
                            } else {
                                //camera permission not allowed, request
                                requestCameraPermission();
                            }
                        } else {
                            //Gallery clicked
                            if (checkStoragePermission()) {
                                //Storage permission allowed
                                pickFromGallery();

                            } else {
                                //Storage permission not allowed, request
                                requestStoragePermission();

                            }

                        }
                    }

                })
                .show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    //Handle permission result
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
                        pickFromGallery();
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

    //Handle image pick result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE && data != null) {

                /* Get picked image */
                image_uri = data.getData();
                //Set to image
                kidpic.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //Set to image
                kidpic.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
