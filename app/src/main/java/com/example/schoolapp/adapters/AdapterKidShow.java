package com.example.schoolapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.editKidPage;
import com.example.schoolapp.model.ModelKid;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.example.schoolapp.FilterKidClass;
//import com.team.my_gorcery.com.team.my_gorcery.activities.edit_ProductActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterKidShow extends RecyclerView.Adapter<AdapterKidShow.HolderKidShow> implements Filterable {

    private Context context;
    public ArrayList<ModelKid> kidList, filterList;
    private FilterKidClass filter;

    public AdapterKidShow(Context context, ArrayList<ModelKid> kidList) {
        this.context = context;
        this.kidList = kidList;
        this.filterList = kidList;
    }

    @NonNull
    @Override
    public HolderKidShow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_kid, parent, false);
        return new HolderKidShow (view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderKidShow holder, int position) {
        // Get data
        final ModelKid modelKid = kidList.get(position);
        String KidId = modelKid.getKidId();
        String uid = modelKid.getUid();
        String umuaka = modelKid.getUmuaka();
        String KidName = modelKid.getKidName();
        String KidSchool = modelKid.getKidSchool();
        String KidClass = modelKid.getKidClass();
        String timestamp = modelKid.getTimestamp();
        String KidTeacherName = modelKid.getKidTeacherName();
        String KidImage = modelKid.getKidImage();
        String gender = modelKid.getGender();

        // Set data
        holder.kidSchool.setText(KidSchool);
        holder.kidName.setText(KidName);

        try {
            Picasso.get().load(KidImage).placeholder(R.drawable.ic_person_black_24dp).into(holder.kidIcon);
        }
        catch (Exception e){
            holder.kidIcon.setImageResource(R.drawable.ic_person_black_24dp);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item clicks, show item details (in bottom sheet)
                detailsBottomSheet(modelKid); // Here model kid contains details of clicked product

            }
        });
    }

    private void detailsBottomSheet(ModelKid modelKid) {
        // Bottom Sheet
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        // Inflate view for bottomSheet
        View view = LayoutInflater.from(context).inflate(R.layout.kid_bs_profile, null);
        // Set view to bottomSheet
        bottomSheetDialog.setContentView(view);


        // Initiate views of bottomSheet
      //  ImageButton bs_backbtn = view.findViewById(R.id.bs_backbtn);
        TextView editBtn = view.findViewById(R.id.kidProfilePic_E);
        TextView deletBtn = view.findViewById(R.id.kidProfilePic_D);
        CircleImageView kidprofileIcon = view.findViewById(R.id.kidProfilePic);
        TextView bs_name = view.findViewById(R.id.bs_nameTv);
        TextView bs_school = view.findViewById(R.id.bs_school);
        TextView bs_class = view.findViewById(R.id.bs_Class);
        TextView bs_teachername = view.findViewById(R.id.bs_TeacherName);


        // Get data
        final String KidId = modelKid.getKidId();
        String uid = modelKid.getUid();
        final String name = modelKid.getKidName();
        String school = modelKid.getKidSchool();
        String classs = modelKid.getKidClass();
        String timestamp = modelKid.getTimestamp();
        String teacher = modelKid.getKidTeacherName();
        String image = modelKid.getKidImage();




        // Set data
        bs_name.setText("NAME : "  +name);
        bs_school.setText("SCHOOL : "  +school);
        bs_class.setText("CLASS : "  +classs);
        bs_teachername.setText("TEACHER'S NAME : "  +teacher);


        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_person_black_24dp).into(kidprofileIcon);
        }
        catch (Exception e){
            kidprofileIcon.setImageResource(R.drawable.ic_person_black_24dp);
        }

        // Show dialog
        bottomSheetDialog.show();


        // Edit click
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open edit kid activity and pass kid id
                Intent intent = new Intent(context, editKidPage.class);
                intent.putExtra("KidId", KidId);
                context.startActivity(intent);


            }
        });

        // Delete click

        deletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

                // Show delete confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete "+name+" ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete
                                deleteKid(KidId); // targeting the (kid id) in the db
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel and dismiss dialog
                            }
                        })
                        .show();
                // tabOrders.setTextColor(getResources().getColor(R.color.brownLightcolor));

            }
        });


    }

    private void deleteKid(String KidId) {
        // Delete product from its db id

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Kids").child(KidId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Product deleted
                        Toast.makeText(context, "Kid deleted successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete of product
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return kidList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterKidClass(this, filterList);
        }
        return null;
    }


    class HolderKidShow extends RecyclerView.ViewHolder{
        /*Holds views of the recycleview*/

        private CircleImageView kidIcon;
        private TextView  kidSchool, kidName;

        public HolderKidShow(@NonNull View itemView) {
            super(itemView);

            kidIcon = itemView.findViewById(R.id.kidIcon_Image);
            kidSchool = itemView.findViewById(R.id.kidPSchool);
            kidName = itemView.findViewById(R.id.kid_name);


        }
    }
}
