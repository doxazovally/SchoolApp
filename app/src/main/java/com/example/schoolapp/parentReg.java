package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
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
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class parentReg extends AppCompatActivity {

    ImageView backBtn;
    Button createAcc, login;
    TextView titleText;
    private CircleImageView Uprofile;
    CountryCodePicker countryPicker;
    TextInputLayout Pfullname, Pemail, Ppassword, PphoneNumber;
    String number;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_reg);


        backBtn = findViewById(R.id.parent_signup_back_btn);
        createAcc = findViewById(R.id.create_parent_Login_btn);
        login = findViewById(R.id.Parent_login_button);
        Pfullname = findViewById(R.id.P_fullname);
        Pemail = findViewById(R.id.P_email);
        Ppassword = findViewById(R.id.P_password);
        PphoneNumber = findViewById(R.id.Psignup_phone_number);
        Uprofile = findViewById(R.id.profileImage);
        countryPicker = findViewById(R.id.country_code_picker);



        countryPicker.showFullName(true);


        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_from_top);
            }
        });


        Uprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pick Image
                showImagedDialog();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // back to login page

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });



        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // register user
                inputData();
            }
        });


    }


   // public void callNextParentLogin(View view) {

       // Intent intent = new Intent(getApplicationContext(), parent_login2.class);

       // Pair[] pairs = new Pair[1];

       // pairs[0] = new Pair<View, String>(backBtn, "transition_create_Acc_back_btn");
        //  pairs[0] = new Pair<View, String>(next, "transition_next_btn");
       // pairs[2] = new Pair<View, String>(login, "transition_login_btn");
      //  pairs[3] = new Pair<View, String>(titleText, "transition_createAcc_text");

       // ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(parentReg.this, pairs);
       // startActivity(intent, options.toBundle());

   // }


    private String FullName, Username, Email,Password, PhoneNumber, PcountryPick;
    private void inputData() {
        //Input data
        FullName = Pfullname.getEditText().getText().toString().trim();
        Email = Pemail.getEditText().getText().toString().trim();
        Password = Ppassword.getEditText().getText().toString().trim();
        PhoneNumber = PphoneNumber.getEditText().getText().toString().trim();



       // PcountryPick = countryPick.getEditText().getText.toString().trim();

        //Validate data
        if(TextUtils.isEmpty(FullName)){
            Toast.makeText(this, "Please enter your Fullname...", Toast.LENGTH_SHORT).show();
            return;
        }


        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Password.length()<6){
            Toast.makeText(this, "Password must be atleast 6 characters...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(PhoneNumber)){
            Toast.makeText(this, "Please enter your PhoneNumber...", Toast.LENGTH_SHORT).show();
            return;
        }

              //  String getUserEnteredPhoneNumber = PphoneNumber.getEditText().getText().toString().trim();
             //   String PhoneNumber = "+"+countryPicker.getFullNumber()+getUserEnteredPhoneNumber;


      //  if (TextUtils.isEmpty(countryPick)){
      //      Toast.makeText(this, "Please select your country...", Toast.LENGTH_SHORT);
       //     return;


       // }


        createAccount();

    }


    private void createAccount() {
        progressDialog.setMessage("Creating Your Account");
        progressDialog.show();

        //Create account
        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Account created
                        saverFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Failure in creating account
                        progressDialog.dismiss();
                        Toast.makeText(parentReg.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Saving Account Info...");

        final String timestamp = ""+System.currentTimeMillis();

        if(image_uri == null){
            //Save info without image

            //Setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" +firebaseAuth.getUid());
            hashMap.put("email", "" +Email);
            hashMap.put("name", "" +FullName);
            hashMap.put("phoneNum", "" +PhoneNumber);
            hashMap.put("timestamp", "" +timestamp);
            hashMap.put("accountType", "Parent");
            hashMap.put("Online", "true");
            hashMap.put("profileImage", "");

            //Save to Firebase Database
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Database updated
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(parentReg.this,"Please check your email for verification", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(parentReg.this, Login.class));
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(parentReg.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to update to Firebase Database
                            progressDialog.dismiss();
                            startActivity(new Intent(parentReg.this, parent_Home_page.class));
                            finish();

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
                                hashMap.put("uid", "" +firebaseAuth.getUid());
                                hashMap.put("email", "" +Email);
                                hashMap.put("name", "" +FullName);
                                hashMap.put("phoneNum", ""+PhoneNumber);
                                hashMap.put("timestamp", "" +timestamp);
                                hashMap.put("accountType", "Parent");
                                hashMap.put("Online", "true");
                                hashMap.put("profileImage", "" +downloadImageUri); // Url of the uploaded image

                                //Save to Firebase Database
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    Toast.makeText(parentReg.this,"Please check your email for verification", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(parentReg.this, Login.class));
                                                                    finish();
                                                                }
                                                                else {
                                                                    Toast.makeText(parentReg.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                //Database updated
                                                progressDialog.dismiss();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to update to Firebase Database
                                                progressDialog.dismiss();
                                                startActivity(new Intent(parentReg.this, parent_Home_page.class));
                                                finish();

                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                             Toast.makeText(parentReg.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
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
                Uprofile.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //Set to image
                Uprofile.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

