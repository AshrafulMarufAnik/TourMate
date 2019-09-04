package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anik.example.tourmate.Adapter.MomentAdapter;
import com.anik.example.tourmate.ModelClass.Moment;
import com.anik.example.tourmate.ModelClass.Tour;
import com.anik.example.tourmate.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

    private SwipeRefreshLayout swipeRefreshLayout;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);
        init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(MomentsActivity.this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            uid = account.getId();
        }
        else {
            uid = firebaseAuth.getCurrentUser().getUid();
        }

        tourID = getIntent().getStringExtra("tourID");
        //uid = firebaseAuth.getCurrentUser().getUid();

        getAllImagesFromStorage();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllImagesFromStorage();

            }
        });
        configMomentRV();
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

                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MomentsActivity.this, "Tour Moment is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void configMomentRV() {
        momentsRV.setLayoutManager(new GridLayoutManager(this,2));
        momentsRV.setAdapter(momentAdapter);
    }

    private void init() {
        momentsRV = findViewById(R.id.momentsRV);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        momentList = new ArrayList<>();
        momentAdapter = new MomentAdapter(momentList,this);
        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout_MA);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_blue);
    }

    public void goToTourDetails(View view) {
        startActivity(new Intent(MomentsActivity.this,TourDetailsActivity.class).putExtra("tourID",tourID));
        finish();
    }
}
