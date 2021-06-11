package com.example.schoolapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.Assignment_in_full_teacher;
import com.example.schoolapp.R;
import com.example.schoolapp.model.ModelAss;
import com.example.schoolapp.model.ModelAssignment_T;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;

public class AdapterAssignmentTeacher extends RecyclerView.Adapter<AdapterAssignmentTeacher.HolderAssignmentTeacher> {

  private Context context;
  private ArrayList<ModelAss> modelAssList;

  public AdapterAssignmentTeacher (Context context, ArrayList<ModelAss> modelAssList){
      this.context = context;
      this.modelAssList = modelAssList;

  }


  @NonNull
  @Override
  public HolderAssignmentTeacher onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      // Inflate layout
      View view = LayoutInflater.from(context).inflate(R.layout.assignment_log_teacher, parent, false);
      return new HolderAssignmentTeacher(view);
  }

  @Override
  public void onBindViewHolder(@NonNull HolderAssignmentTeacher holder, int position) {
      // Get data
      ModelAss modelAss = modelAssList.get(position);
       String TassSubject = modelAss.getAssignmentSubject();
       String TassTopic = modelAss.getAssignmentTopic();
       String TassClass = modelAss.getAssignmentClass();
       String TassDG = modelAss.getAssignmentDG();
       String TassDOS = modelAss.getAssignmentDtoS();
       final String AssignmentID = modelAss.getAssignmentID();
       String TassStatus = modelAss.getAssignmentStatus();
       String TassTyped = modelAss.getAssignmentTyped();
       String TassImaged = modelAss.getAssignmentImage();
      // String TassTo = modelAssignment_t.getTAto();

      // Get data
     // loadAssignmentInfo(modelAssignment_t, holder);

      // set data
      holder.TassignmentLogIdTv.setText("Assignment ID : "+AssignmentID);
      holder.TassLogSubjectTv.setText("Subject : "+TassSubject);
      holder.TassignmentLogTopic.setText("Topic : "+TassTopic);
      holder.TassignmentStatusTv.setText(TassStatus);
      holder.TassignmentLogClass.setText(TassClass);
      holder.TassignmentLogDateGivenTv.setText(TassDG);
      holder.TassignmentLogDateSubmissionTv.setText(TassDOS);


      // Change Assignment status text color
      if (TassStatus.equals("In Progress")){
          holder.TassignmentStatusTv.setTextColor(context.getResources().getColor(R.color.colorAccent));
      }
      else if (TassStatus.equals("Done")){
          holder.TassignmentStatusTv.setTextColor(context.getResources().getColor(R.color.colorBlue));
      }
      else if (TassStatus.equals("Not Done")) {
          holder.TassignmentStatusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
      }
      else if (TassStatus.equals("Cancelled")) {
          holder.TassignmentStatusTv.setTextColor(context.getResources().getColor(R.color.coloryellowDark));
      }


      try {
          Picasso.get().load(TassImaged).placeholder(R.drawable.bookerass).into(holder.assignmentImage);
      }
      catch (Exception e){
          holder.assignmentImage.setImageResource(R.drawable.bookerass);
      }


      // Handle clicks listener, show shop details
      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              // to open the Assignment details, we will need keys: AssignmentId,
              Intent intent = new Intent(context, Assignment_in_full_teacher.class);
              intent.putExtra("AssignmentID : ", AssignmentID);
              context.startActivity(intent); // we will get these values through intent on Assignment_in_full_Teacher
          }
      });


  }


  @Override
  public int getItemCount() {
      return modelAssList.size();
  }



  public class HolderAssignmentTeacher extends RecyclerView.ViewHolder {

      //Views of Assignment Log
      ImageView assignmentImage;
      EditText  assignmentypedd;
      private TextView TassignmentLogIdTv, TassLogSubjectTv, TassignmentLogDateGivenTv, TassignmentStatusTv, TassignmentLogTopic, TassignmentLogClass, TassignmentLogDateSubmissionTv;


      public HolderAssignmentTeacher(@NonNull View itemView) {
          super(itemView);

          TassignmentLogIdTv = itemView.findViewById(R.id.TassignmentLogIdTv);
          TassLogSubjectTv = itemView.findViewById(R.id.TassLogSubjectTv);
          TassignmentLogDateGivenTv = itemView.findViewById(R.id.TassignmentLogDateGivenTv);
          TassignmentStatusTv = itemView.findViewById(R.id.TassignmentStatusTv);
          TassignmentLogTopic = itemView.findViewById(R.id.TassignmentLogTopic);
          TassignmentLogClass = itemView.findViewById(R.id.TassignmentLogClass);
          TassignmentLogDateSubmissionTv = itemView.findViewById(R.id.TassignmentLogDateSubmissionTv);
          assignmentypedd = itemView.findViewById(R.id.Tassignmentypedd);
          assignmentImage = itemView.findViewById(R.id.TassignmentImage);
      }
  }

}
