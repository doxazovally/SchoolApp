package com.example.schoolapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.FilterKidClass;
import com.example.schoolapp.FilterTKidClass;
import com.example.schoolapp.R;
import com.example.schoolapp.TeacherHomePage;
import com.example.schoolapp.editKidPage;
import com.example.schoolapp.giveAssignment;
import com.example.schoolapp.model.ModelKid;
import com.example.schoolapp.model.ModelTKid;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterTeacherKidShow extends RecyclerView.Adapter<AdapterTeacherKidShow.HolderTeacherKidShow> {

    private Context context;
    public ArrayList<ModelTKid> kidTList, filterList;
   // private FilterKidClass filter;

    public AdapterTeacherKidShow(Context context, ArrayList<ModelTKid> kidTList) {
        this.context = context;
        this.kidTList = kidTList;
        this.filterList = kidTList;
    }

    @NonNull
    @Override
    public HolderTeacherKidShow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_tstudent, parent, false);
        return new HolderTeacherKidShow (view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderTeacherKidShow holder, int position) {
        // Get data
        final ModelTKid modelTKid = kidTList.get(position);
        final String KidId = modelTKid.getKidId();
        final String uid = modelTKid.getUid();
        String KidName = modelTKid.getKidName();
        String KidSchool = modelTKid.getKidSchool();
        String KidClass = modelTKid.getKidClass();
        String timestamp = modelTKid.getTimestamp();
        String KidTeacherName = modelTKid.getKidTeacherName();
        String KidImage = modelTKid.getKidImage();
        String gender = modelTKid.getGender();
        String parenttname = modelTKid.getParentName();

        // Set data
        holder.gender.setText(gender);
        holder.kidName.setText(KidName);
        holder.kidClass.setText(KidClass);

        try {
            Picasso.get().load(KidImage).placeholder(R.drawable.ic_person_black_24dp).into(holder.TkidIcon);
        }
        catch (Exception e){
            holder.TkidIcon.setImageResource(R.drawable.ic_person_black_24dp);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TeacherHomePage.class);
                intent.putExtra("kalu", uid);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item clicks, show item details (in bottom sheet)
                detailsBottomSheet(modelTKid); // Here model kid contains details of clicked kid

            }
        });
    }

    private void detailsBottomSheet(ModelTKid modelTKid) {
        // Bottom Sheet
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        // Inflate view for bottomSheet
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_kid_bs_profile, null);
        // Set view to bottomSheet
        bottomSheetDialog.setContentView(view);


        // Initiate views of bottomSheet
        CircleImageView kidprofileIcon = view.findViewById(R.id.kidProfilePic);
        TextView bs_name = view.findViewById(R.id.bs_tnameTv);
        TextView bs_school = view.findViewById(R.id.bs_tschool);
        TextView bs_class = view.findViewById(R.id.bs_tClass);
        TextView bs_pname = view.findViewById(R.id.bs_ParentName);
        TextView giveAss = view.findViewById(R.id.GiveAssignment);
        TextView seeFeedbk = view.findViewById(R.id.seeFeedback);
        TextView sendInvite = view.findViewById(R.id.sendInvite);
        TextView AssLog = view.findViewById(R.id.seeAsignment);



        // Get data
        final String KidId = modelTKid.getKidId();
        String uid = modelTKid.getUid();
        final String name = modelTKid.getKidName();
        String school = modelTKid.getKidSchool();
        String classs = modelTKid.getKidClass();
        String parentt = modelTKid.getParentName();
        String image = modelTKid.getKidImage();
        String gender = modelTKid.getGender();


        // Set data
        bs_name.setText("NAME : "  +name);
        bs_school.setText("SCHOOL : "  +school);
        bs_class.setText("CLASS : "  +classs);
        bs_pname.setText("PARENT'S NAME:  " +parentt);


        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_person_black_24dp).into(kidprofileIcon);
        }
        catch (Exception e){
            kidprofileIcon.setImageResource(R.drawable.ic_person_black_24dp);
        }

        // Show dialog
        bottomSheetDialog.show();



        giveAss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open edit kid activity and pass kid id
                Intent intent = new Intent(context, giveAssignment.class);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return kidTList.size();
    }


    class HolderTeacherKidShow extends RecyclerView.ViewHolder{
        /*Holds views of the recycleview*/

        private CircleImageView TkidIcon;
        private TextView  kidClass, kidName, gender;

        public HolderTeacherKidShow(@NonNull View itemView) {
            super(itemView);

            TkidIcon = itemView.findViewById(R.id.kidTIcon_Image);
            kidClass = itemView.findViewById(R.id.kidClass);
            kidName = itemView.findViewById(R.id.kid_name2);
            gender = itemView.findViewById(R.id.kidPGender);



        }
    }
}
