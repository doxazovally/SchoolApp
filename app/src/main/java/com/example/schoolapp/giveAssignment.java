package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

public class giveAssignment extends AppCompatActivity {

    ImageView assbkBtn, assImage;
    TextView teacherAssPage, AssClass, dateGiven, dateOfSubmission, assMode, typeIt, Upload, Pdf,uploadedPdfFile ;
    EditText subject, topic, assTyping;
    Button giveAssi, assPdfBtn;
    LinearLayout pdfLayout;
    private int zDate, zMonth, zYear;


    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    private ProgressDialog progressDialog;



    private Uri image_uri;
    private  Uri PDF_uri;

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
   // private String[] filePermission;

    Animation topAnim, bottomAnim, middleAnim;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_assignment);


        assbkBtn = findViewById(R.id.teacher_Assignment_back_btn);
        teacherAssPage = findViewById(R.id.teacherAssPage);
        AssClass = findViewById(R.id.assClass);
        dateGiven = findViewById(R.id.assDateGiven);
        dateOfSubmission = findViewById(R.id.assSubmissionDate);
        assMode = findViewById(R.id.assMode);
        typeIt = findViewById(R.id.assType);
        Upload = findViewById(R.id.assPicUpload);


        Pdf = findViewById(R.id.assPdfUpload); // button that makes the select file button visible



        subject = findViewById(R.id.assSubject);
        topic = findViewById(R.id.assTopic);
        assImage = findViewById(R.id.assImage);


        pdfLayout = findViewById(R.id.pdfLayout); // makes the layout of the pdf selection available

        uploadedPdfFile = findViewById(R.id.uploadedPdfFile); // textview (PDF) of selected file

        assPdfBtn = findViewById(R.id.assPdfBtn); // button to select a file



        assTyping = findViewById(R.id.typeAss);
        giveAssi = findViewById(R.id.give_assignment);




        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        assbkBtn.setAnimation(topAnim);
        teacherAssPage.setAnimation(topAnim);
        AssClass.setAnimation(topAnim);
        dateGiven.setAnimation(topAnim);
        dateOfSubmission.setAnimation(topAnim);
        assMode.setAnimation(topAnim);
        typeIt.setAnimation(topAnim);
        Upload.setAnimation(topAnim);
        Pdf.setAnimation(topAnim);
        subject.setAnimation(topAnim);
        topic.setAnimation(topAnim);



        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
       // filePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};


        firebaseAuth = FirebaseAuth.getInstance();
        //checkUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        assbkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TeacherHomePage.class);
                startActivity(intent);
            }
        });

        AssClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                classDialog();
            }
        });


        assImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagedDialog();
            }
        });


        typeIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypingSpace();
            }
        });


        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageUpload();
            }
        });


        Pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPdfUpload();
            }
        });


        dateGiven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOfAssGiven();
            }
        });


        dateOfSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOfAssToBeSubmitted();
            }
        });

        giveAssi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

        assPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(giveAssignment.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selectPDFfile();
                }
                else
                    ActivityCompat.requestPermissions(giveAssignment.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 7);

            }
        });


    }



    private void dateOfAssGiven() {
        final Calendar cal = Calendar.getInstance();
        zDate = cal.get(Calendar.DATE);
        zMonth = cal.get(Calendar.MONTH);
        zYear = cal.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(giveAssignment.this, android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                dateGiven.setText(date+ "/" +month+ "/" +year);
            }
        }, zYear, zMonth, zDate);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }


    private void dateOfAssToBeSubmitted() {
        final Calendar cal = Calendar.getInstance();
        zDate = cal.get(Calendar.DATE);
        zMonth = cal.get(Calendar.MONTH);
        zYear = cal.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(giveAssignment.this, android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                dateOfSubmission.setText(date+ "/" +month+ "/" +year);
            }
        }, zYear, zMonth, zDate);
        datePickerDialog.show();
    }

    private void showTypingSpace() {
        // Show space for typing in assignment
        assTyping.setVisibility(View.VISIBLE);
        pdfLayout.setVisibility(View.GONE);
        assImage.setVisibility(View.GONE);

    }

    private void showImageUpload() {
        // Show space for uploading picture
        assImage.setVisibility(View.VISIBLE);
        assTyping.setVisibility(View.GONE);
        pdfLayout.setVisibility(View.GONE);

    }

    private void showPdfUpload() {
        // Show space for pdf uploading assignment
        pdfLayout.setVisibility(View.VISIBLE);
        assTyping.setVisibility(View.GONE);
        assImage.setVisibility(View.GONE);


    }



    private String AssSUBJECT, AssCLASST, AssTOPIC, AssDOASSG, AssDOASSS, AssTYPE, AssIMAGEP, AssPDFT;

    private void inputData () {

        AssSUBJECT = subject.getText().toString().trim();
        AssCLASST = AssClass.getText().toString().trim();
        AssTOPIC = topic.getText().toString().trim();
        AssDOASSG = dateGiven.getText().toString().trim();
        AssDOASSS = dateOfSubmission.getText().toString().trim();
        AssTYPE = assTyping.getText().toString().trim();



        if (TextUtils.isEmpty(AssSUBJECT)) {
            Toast.makeText(this, "Enter Assignment subject...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(AssCLASST)) {
            Toast.makeText(this, "Select class...", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(AssTOPIC)) {
            Toast.makeText(this, "Enter Assignment Topic", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(AssDOASSG)) {
            Toast.makeText(this, "Select Date Of Assinment Given", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(AssDOASSS)) {
            Toast.makeText(this, "Select Date For Assignment To Be Submitted", Toast.LENGTH_SHORT).show();
            return;
        }


        addAssignmentToDB();

    }

    private void addAssignmentToDB() {
        // (3) Adding data to db
        progressDialog.setTitle("Adding Assignment...");
        progressDialog.show();

        final String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            //Upload Assignment without image or pdf file

            // Setup data to be uploaded
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("AssignmentID", "" + timestamp);
            hashMap.put("AssignmentSubject", "" + AssSUBJECT);
            hashMap.put("AssignmentClass", "" + AssCLASST);
            hashMap.put("AssignmentTopic", "" + AssTOPIC);
            hashMap.put("AssignmentDG", "" +  AssDOASSG);
            hashMap.put("AssignmentDtoS", "" +  AssDOASSS);
            hashMap.put("AssignmentTyped", "" +  AssTYPE);
            hashMap.put("AssignmentImage", ""); // no image uploaded
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("accountType", "AssignmentGiven");
            hashMap.put("AssignmentStatus", "In Progress" );
            hashMap.put("assId", "" + firebaseAuth.getUid());


            // add to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Assignments").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Assignment added to db
                            progressDialog.dismiss();
                            Toast.makeText(giveAssignment.this, "Assignment has been added", Toast.LENGTH_SHORT).show();
                            clearData();
                            //  finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to data to Database
                            progressDialog.dismiss();
                            Toast.makeText(giveAssignment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            finish();

                        }
                    });
        }


        else {
            // Upload with image or file
            // First upload image of file to db
            // Name and path of image or file

            String filePathAndName = "Assignment_images/" + "" + timestamp;

            //Upload Assignment image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Imaged uploaded
                            // Get the uploaded image url from file path in the db
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                // Image url received, upload to db


                                // Setup data to be uploaded
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("AssignmentID", "" + timestamp);
                                hashMap.put("AssignmentSubject", "" + AssSUBJECT);
                                hashMap.put("AssignmentClass", "" + AssCLASST);
                                hashMap.put("AssignmentTopic", "" + AssTOPIC);
                                hashMap.put("AssignmentDG", "" +  AssDOASSG);
                                hashMap.put("AssignmentDtoS", "" +  AssDOASSS);
                                hashMap.put("AssignmentTyped", "" +  AssTYPE);
                                hashMap.put("AssignmentImage", "" + downloadImageUri); // Url of the uploaded Assignment image
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("accountType", "AssignmentGiven");
                                hashMap.put("AssignmentStatus", "In Progress" );
                                hashMap.put("assId", "" + firebaseAuth.getUid());


                                // add to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Assignments").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                // Assignment added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(giveAssignment.this, "Assignment has been added...", Toast.LENGTH_SHORT).show();
                                                clearData();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to data to Database
                                                progressDialog.dismiss();
                                                Toast.makeText(giveAssignment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(giveAssignment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

        if(PDF_uri != null) {
            // Upload with image or file
            // First upload image of file to db
            // Name and path of image or file

            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading file...");
            progressDialog.setProgress(0);
            progressDialog.show();


            String filePathAndName = "Assignment_files/" + "" + timestamp;

            //Upload Assignment PDF
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(PDF_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // file uploaded
                            // Get the uploaded pdf url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadPDFUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                // PDF url received, upload to db


                                // Setup data to be uploaded
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("AssignmentID", "" + timestamp);
                                hashMap.put("AssignmentSubject", "" + AssSUBJECT);
                                hashMap.put("AssignmentClass", "" + AssCLASST);
                                hashMap.put("AssignmentTopic", "" + AssTOPIC);
                                hashMap.put("AssignmentDG", "" +  AssDOASSG);
                                hashMap.put("AssignmentDtoS", "" +  AssDOASSS);
                                hashMap.put("AssignmentTyped", "" +  AssTYPE);
                                hashMap.put("AssignmentPDF", "" + downloadPDFUri); // Url of the uploaded Assignment image
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("accountType", "AssignmentGiven");
                                hashMap.put("AssignmentStatus", "In Progress" );
                                hashMap.put("assId", "" + firebaseAuth.getUid());


                                // add to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Assignments").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                // Assignment added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(giveAssignment.this, "Assignment has been added...", Toast.LENGTH_SHORT).show();
                                                clearData();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to data to Database
                                                progressDialog.dismiss();
                                                Toast.makeText(giveAssignment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(giveAssignment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    // Tracking the progress of the upload
                    int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currentProgress);
                }
            });
        }

        else {
            // Upload with image or file
            // First upload image of file to db
            // Name and path of image or file

          //  progressDialog.setTitle("Adding Assignment...");
         //   progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         //   progressDialog.setTitle("Uploading file...");
        //    progressDialog.setProgress(0);
        //    progressDialog.show();


            // Setup data to be uploaded
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("AssignmentID", "" + timestamp);
            hashMap.put("AssignmentSubject", "" + AssSUBJECT);
            hashMap.put("AssignmentClass", "" + AssCLASST);
            hashMap.put("AssignmentTopic", "" + AssTOPIC);
            hashMap.put("AssignmentDG", "" +  AssDOASSG);
            hashMap.put("AssignmentDtoS", "" +  AssDOASSS);
            hashMap.put("AssignmentTyped", "" +  AssTYPE);
            hashMap.put("AssignmentPDF", ""); // no file uploaded
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("accountType", "AssignmentGiven");
            hashMap.put("AssignmentStatus", "In Progress" );
            hashMap.put("assId", "" + firebaseAuth.getUid());


            // add to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Assignments").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Assignment added to db
                            progressDialog.dismiss();
                            Toast.makeText(giveAssignment.this, "Assignment has been added", Toast.LENGTH_SHORT).show();
                            clearData();
                            //  finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to data to Database
                            progressDialog.dismiss();
                            Toast.makeText(giveAssignment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            finish();

                        }
                    });
        }


    }


    private void clearData() {
        //  Clear data after uploading last kid data
        subject.setText("");
        AssClass.setText("");
        topic.setText("");
        dateGiven.setText("");
        dateOfSubmission.setText("");
        assTyping.setText("");
        assImage.setImageResource(R.drawable.bookerass);
        image_uri = null;
        PDF_uri = null;
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
                        AssClass.setText(category);
                    }
                }).show();
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

    private void selectPDFfile() {
        // to allow the user to select file using file manager
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT); // helps to fetch files
        startActivityForResult(intent,99);

    }


   // private void pdfUploading() {
    //    if(ContextCompat.checkSelfPermission(giveAssignment.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
   //         selectPDFfile();
   //     }
   //     else
    //        ActivityCompat.requestPermissions(giveAssignment.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 7);

  //  }



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

        if(requestCode == 7 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectPDFfile();
        }
        else
            Toast.makeText(giveAssignment.this, "Please grant permission...", Toast.LENGTH_SHORT).show();

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }



    // this methods checks whether the user has selected an image or a file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE && data!= null){

                //Get picked image
                image_uri = data.getData();
                //Set to image
                assImage.setImageURI(image_uri);
            }

            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //Set to image
                assImage.setImageURI(image_uri);
            }

            else if(requestCode==99 && data!= null){
                PDF_uri = data.getData();
                uploadedPdfFile.setText(data.getData().getLastPathSegment());

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
