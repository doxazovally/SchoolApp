package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schoolapp.model.ModelKid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class addKidPage extends AppCompatActivity {
    private CircleImageView kidpic;
    private EditText kidName, kidSchool, kidTeacherName, kidparentName;
    private TextView kidClass;
    private Button addKid;
    private ImageButton addbk;
    RadioButton male, female, custom;
    LinearLayout gendar;
    String Genda;


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
        setContentView(R.layout.activity_add_kid_page);

        kidpic = findViewById(R.id.kidpic);
        kidName = findViewById(R.id.kid_Title);
        kidClass = findViewById(R.id.kid_Class);
        kidSchool = findViewById(R.id.kid_School);
        kidTeacherName = findViewById(R.id.kid_Teacher_name);
        addKid = findViewById(R.id.Add_kid_Btn);
        addbk = findViewById(R.id.add_backBtn);
        gendar = findViewById(R.id.gender_Layout);
        male = findViewById(R.id.gender_male);
        female = findViewById(R.id.gender_female);
        custom = findViewById(R.id.gender_custom);
        kidparentName = findViewById(R.id.kid_Pname);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);


        kidpic.setAnimation(bottomAnim);
        kidName.setAnimation(bottomAnim);
        kidClass.setAnimation(bottomAnim);
        kidSchool.setAnimation(bottomAnim);
        kidTeacherName.setAnimation(bottomAnim);
        kidparentName.setAnimation(bottomAnim);
        addKid.setAnimation(bottomAnim);
        gendar.setAnimation(bottomAnim);

        firebaseAuth = FirebaseAuth.getInstance();

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

        addKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
                // Flow:
                // 1. Input data
                // 2. Validate data
                // 3. Add data to db

            }
        });

        addbk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load Kids
                Intent intent = new Intent(getApplicationContext(), parent_Home_page.class);
                startActivity(intent);
                finish();

            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.gender_male:
                if (checked)
                    Genda = G1;
                break;

            case R.id.gender_female:
                if (checked)
                    Genda = G2;
                break;

            case R.id.gender_custom:
                if (checked)
                    Genda = G3;
                break;
        }

    }


    private String KIDNAME, KIDCLASS, KIDSCHOOL, KIDTEACHERNAME, KIDPARENTNAME, G1, G2, G3, GENDER;

    private void inputData() {
        // (1) Input data
        KIDNAME = kidName.getText().toString().trim();
        KIDCLASS = kidClass.getText().toString().trim();
        KIDSCHOOL = kidSchool.getText().toString().trim();
        KIDPARENTNAME = kidparentName.getText().toString().trim();
        KIDTEACHERNAME = kidTeacherName.getText().toString().trim();
        G1 = male.getText().toString().trim();
        G2 = female.getText().toString().trim();
        G3 = custom.getText().toString().trim();
        GENDER = Genda;


        // (2) Validate data
        if (TextUtils.isEmpty(KIDPARENTNAME)) {
            Toast.makeText(this, "Enter Parent Name...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(KIDNAME)) {
            Toast.makeText(this, "Enter Kid's Name...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(GENDER)) {
            Toast.makeText(this, "Enter Kid's Gender...", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            GENDER = Genda;
        }

        if (TextUtils.isEmpty(KIDCLASS)) {
            Toast.makeText(this, "Select Kid's Class...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(KIDSCHOOL)) {
            Toast.makeText(this, "Enter Kid's School...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(KIDTEACHERNAME)) {
            Toast.makeText(this, "Enter Kid's Teacher's Name...", Toast.LENGTH_SHORT).show();
            return;
        }


        addKidToDB();

    }


    private void addKidToDB() {
        // (3) Adding data to db
        progressDialog.setTitle("We Are Adding Your Kid...");
        progressDialog.show();

        final String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            //Upload without image

            // Setup data to be uploaded
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("KidId", "" + timestamp);
            hashMap.put("KidName", "" + KIDNAME);
            hashMap.put("KidClass", "" + KIDCLASS);
            hashMap.put("KidSchool", "" + KIDSCHOOL);
            hashMap.put("KidTeacherName", "" + KIDTEACHERNAME);
            hashMap.put("KidImage", ""); // no image uploaded
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("accountType", "Umuaka");
            hashMap.put("Gender", "" + GENDER);
            hashMap.put("ParentName", "" + KIDPARENTNAME);
            hashMap.put("uid", "" + firebaseAuth.getUid());


            // add to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Kids").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Kid added to db
                            progressDialog.dismiss();
                            Toast.makeText(addKidPage.this, "Your Kid has been added...", Toast.LENGTH_SHORT).show();
                            clearData();
                            //  finish();


                            Intent intent = new Intent(getApplicationContext(), parent_Home_page.class);
                            startActivity(intent);
                            finish();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to data to Database
                            progressDialog.dismiss();
                            Toast.makeText(addKidPage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            finish();

                        }
                    });
        }
        else {
            // Upload with image
            // First upload image to db
            // Name and path of image

            String filePathAndName = "Kids_images/" + "" + timestamp;

            //Upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Imaged uploaded
                            // Get the uploaded image url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                // Image url received, upload to db

                                // Setup data to be uploaded
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("KidId", "" + timestamp);
                                hashMap.put("KidName", "" + KIDNAME);
                                hashMap.put("KidClass", "" + KIDCLASS);
                                hashMap.put("KidSchool", "" + KIDSCHOOL);
                                hashMap.put("KidTeacherName", "" + KIDTEACHERNAME);
                                hashMap.put("KidImage", "" + downloadImageUri); // Url of the uploaded kid image
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("ParentName", "" + KIDPARENTNAME);
                                hashMap.put("Gender", "" + GENDER);
                                hashMap.put("accountType", "Umuaka");
                                hashMap.put("uid", "" + firebaseAuth.getUid());


                                // add to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Kids").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                // Kid added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(addKidPage.this, "Your Kid has been added...", Toast.LENGTH_SHORT).show();
                                                clearData();

                                                Intent intent = new Intent(getApplicationContext(), parent_Home_page.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to data to Database
                                                progressDialog.dismiss();
                                                Toast.makeText(addKidPage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                finish();

                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to upload product image
                            progressDialog.dismiss();
                            Toast.makeText(addKidPage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }


    private void clearData() {
        //  Clear data after uploading last kid data
        kidparentName.setText("");
        kidName.setText("");
        kidClass.setText("");
        kidSchool.setText("");
        kidTeacherName.setText("");
        kidpic.setImageResource(R.drawable.ic_person_black_24dp);
        image_uri = null;
    }


    private void classDialog() {
        //Cate. Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Class Category")
                .setItems(com.example.schoolapp.Constants.classCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //Get picked category

                        String category = Constants.classCategory[which];
                        kidClass.setText(category);
                    }
                }).show();
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
