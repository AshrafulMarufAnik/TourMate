package com.anik.example.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TourDetailsActivity extends AppCompatActivity {
    private LinearLayout routeClick,expenseClick,circleClick,tourMomentsClick,addMomentsClick;
    private TextView tourNameTV,tourLocationTV,tourBudgetTV,tourTotalExpenseTV,tourReturnDateTV;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Tour currentTour;
    private String tourID;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);

        init();
        tourID = getIntent().getStringExtra("tourID");
        uid = firebaseAuth.getCurrentUser().getUid();
        getTourDetailsFromDB();

        routeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TourDetailsActivity.this,RoutePointsActivity.class);
                intent.putExtra("tourID",tourID);
                startActivity(intent);
            }
        });

        expenseClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TourDetailsActivity.this,ExpenseListActivity.class);
                intent.putExtra("tourID",tourID);
                startActivity(intent);
            }
        });


    }

    private void getTourDetailsFromDB() {
        DatabaseReference tourRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Tour Basic Info");
        tourRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentTour = dataSnapshot.getValue(Tour.class);
                    tourNameTV.setText(currentTour.getTourName());
                    tourLocationTV.setText(currentTour.getTourLocation());
                    tourBudgetTV.setText(String.valueOf(currentTour.getTourBudget()));
                    tourReturnDateTV.setText(currentTour.getTourReturnDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TourDetailsActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        routeClick = findViewById(R.id.viewRoutesClick);
        expenseClick = findViewById(R.id.viewExpensesClick);
        circleClick = findViewById(R.id.viewCirclesClick);
        tourMomentsClick = findViewById(R.id.viewTourMomentsClick);
        addMomentsClick = findViewById(R.id.addTourMomentsClick);
        tourNameTV = findViewById(R.id.detailsTourNameTV);
        tourLocationTV = findViewById(R.id.detailsTourLocationTV);
        tourBudgetTV = findViewById(R.id.detailsTourBudgetTV);
        tourTotalExpenseTV = findViewById(R.id.detailsTourTotalExpenseTV);
        tourReturnDateTV = findViewById(R.id.detailsTourReturnDateTV);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public void goToMainActivity(View view) {
        startActivity(new Intent(TourDetailsActivity.this,MainActivity.class));
    }
}
