package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anik.example.tourmate.Adapter.MomentAdapter;
import com.anik.example.tourmate.ModelClass.Moment;
import com.anik.example.tourmate.ModelClass.Tour;
import com.anik.example.tourmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MomentsActivity extends AppCompatActivity {
    private RecyclerView momentsRV;
    private String tourID,uid;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Moment> momentList;
    private MomentAdapter momentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);
        init();

        tourID = getIntent().getStringExtra("tourID");
        uid = firebaseAuth.getCurrentUser().getUid();

        getAllImagesFromStorage();
        configMomentRV();
    }

    private void configMomentRV() {
        momentsRV.setLayoutManager(new GridLayoutManager(this,3));
        momentsRV.setAdapter(momentAdapter);
    }

    private void getAllImagesFromStorage() {
        DatabaseReference allMomentsRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Tour Moments");
        allMomentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    momentList.clear();

                    for(DataSnapshot momentData: dataSnapshot.getChildren()){
                        Moment newMoment = momentData.getValue(Moment.class);
                        momentList.add(newMoment);
                        momentAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    Toast.makeText(MomentsActivity.this, "Tour Moment is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        momentsRV = findViewById(R.id.momentsRV);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        momentList = new ArrayList<>();
        momentAdapter = new MomentAdapter(momentList,this);
    }

    public void goToTourDetails(View view) {
        startActivity(new Intent(MomentsActivity.this,TourDetailsActivity.class));
        finish();
    }
}
