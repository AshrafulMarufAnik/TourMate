package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anik.example.tourmate.R;
import com.anik.example.tourmate.ModelClass.Tour;
import com.anik.example.tourmate.Adapter.TourHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TourHistoryActivity extends AppCompatActivity {
    private RecyclerView tourHistoryRV;
    private TourHistoryAdapter tourHistoryAdapter;
    private ArrayList<Tour> tourList;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private SwipeRefreshLayout swipeRefreshLayout_THA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_history);
        init();
        uid = firebaseAuth.getCurrentUser().getUid();

        swipeRefreshLayout_THA.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTourDataFromDBThroughModelClass();

            }
        });

        getAllTourDataFromDBThroughModelClass();
        configTourHistoryRV();

    }

    private void configTourHistoryRV() {
        tourHistoryRV.setLayoutManager(new LinearLayoutManager(this));
        tourHistoryRV.setAdapter(tourHistoryAdapter);


    }

    private void getAllTourDataFromDBThroughModelClass() {
        DatabaseReference allTourRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information");
        allTourRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    tourList.clear();

                    for(DataSnapshot tourData: dataSnapshot.getChildren()){
                        Tour newTour = tourData.getValue(Tour.class);
                        tourList.add(newTour);
                        tourHistoryAdapter.notifyDataSetChanged();

                    }
                    swipeRefreshLayout_THA.setRefreshing(false);
                }
                else {
                    Toast.makeText(TourHistoryActivity.this, "Tour History is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        tourHistoryRV = findViewById(R.id.tourHistoryRV);
        tourList = new ArrayList<>();
        tourHistoryAdapter = new TourHistoryAdapter(tourList,this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        swipeRefreshLayout_THA = findViewById(R.id.swiperefreshlayout_THA);
        swipeRefreshLayout_THA.setColorSchemeResources(R.color.color_blue);
    }

    public void goBack(View view) {
        startActivity(new Intent(TourHistoryActivity.this,MainActivity.class));
        finish();
    }
}
