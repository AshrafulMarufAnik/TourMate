package com.anik.example.tourmate.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.example.tourmate.Activity.AddTourActivity;
import com.anik.example.tourmate.Activity.TourDetailsActivity;
import com.anik.example.tourmate.R;
import com.anik.example.tourmate.ModelClass.Tour;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TourHistoryAdapter extends RecyclerView.Adapter<TourHistoryAdapter.ViewHolder> {
    private ArrayList<Tour> tourList;
    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private String uid;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public TourHistoryAdapter(ArrayList<Tour> tourList, Context context) {
        this.tourList = tourList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_tour_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(context.getApplicationContext(), gso);
        account = GoogleSignIn.getLastSignedInAccount(context.getApplicationContext());

        if(account != null){
            uid = account.getId();
        }
        else {
            uid = firebaseAuth.getCurrentUser().getUid();
        }

        final Tour currentTour = tourList.get(position);

        holder.tourName.setText(currentTour.getTourName());
        holder.tourLocation.setText(currentTour.getTourLocation());
        holder.tourStartDate.setText(currentTour.getTourDate());
        final String tourID = currentTour.getTourID();

        holder.detailsClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TourDetailsActivity.class);
                intent.putExtra("dTourID",tourID);
                intent.putExtra("intentSource",2);
                context.startActivity(intent);
            }
        });

        holder.updateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeUpdateTourInfoAsSharedPref(currentTour.getTourID(),currentTour.getTourName(),
                        currentTour.getTourLocation(),String.valueOf(currentTour.getTourBudget()),currentTour.getTourDate(),
                        currentTour.getTourTime(),currentTour.getTourReturnDate());

                Intent intent = new Intent(context, AddTourActivity.class);
                intent.putExtra("updateIntent",1);
                //intent.putExtra("updateTourName",currentTour.getTourName());
                //intent.putExtra("updateTourID",currentTour.getTourID());
                //intent.putExtra("updateTourLocation",currentTour.getTourLocation());
                //intent.putExtra("updateTourDate",currentTour.getTourDate());
               //intent.putExtra("updateTourTime",currentTour.getTourTime());
               //intent.putExtra("updateTourReturnDate",currentTour.getTourReturnDate());
               //intent.putExtra("updateTourBudget",String.valueOf(currentTour.getTourBudget()));
                context.startActivity(intent);
            }
        });

        holder.deleteClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setTitle("Delete Tour");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //String uid = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference tourRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(currentTour.getTourID());
                        tourRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Tour Deleted", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(context,"Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        tourList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tourName,tourLocation,tourStartDate,tourEndDate,tourTotalExpense;
        private LinearLayout detailsClick,updateClick,deleteClick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tourName = itemView.findViewById(R.id.itemTourNameTV);
            tourLocation = itemView.findViewById(R.id.itemTourLocationTV);
            tourStartDate = itemView.findViewById(R.id.itemTourStartDateTV);
            tourEndDate = itemView.findViewById(R.id.itemTourEndDateTV);
            tourTotalExpense = itemView.findViewById(R.id.itemTourTotalExpenseTV);
            detailsClick = itemView.findViewById(R.id.itemDetailsClick);
            updateClick = itemView.findViewById(R.id.itemUpdateClick);
            deleteClick = itemView.findViewById(R.id.itemDeleteClick);
        }
    }

    public void storeUpdateTourInfoAsSharedPref(String tid, String name, String location, String budget, String date, String time, String returnDate) {
        sharedPreferences = context.getSharedPreferences("updateTourInfoSP", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("SPUpdateTourId", tid);
        editor.putString("SPUpdateTourName", name);
        editor.putString("SPUpdateTourLocation", location);
        editor.putString("SPUpdateTourBudget", budget);
        editor.putString("SPUpdateTourDate", date);
        editor.putString("SPUpdateTourTime", time);
        editor.putString("SPUpdateTourReturnDate", returnDate);
        editor.commit();
        editor.apply();
    }
}
