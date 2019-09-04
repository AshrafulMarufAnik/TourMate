package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anik.example.tourmate.Adapter.RouteListAdapter;
import com.anik.example.tourmate.ModelClass.Expense;
import com.anik.example.tourmate.ModelClass.Route;
import com.anik.example.tourmate.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoutePointsActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private ArrayList<Route> routeList;
    private RouteListAdapter routeListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String tourID,uid;
    private RecyclerView routePointsRV;
    private FloatingActionButton addRouteFABTN;
    private static final int REQUEST_CODE = 1;
    private String routeLocation;
    private TextView rp;
    private String routeID;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_points);
        init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(RoutePointsActivity.this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            uid = account.getId();
        }
        else {
            uid = firebaseAuth.getCurrentUser().getUid();
        }

        sharedPreferences = getSharedPreferences("TourInfo",MODE_PRIVATE);
        tourID = sharedPreferences.getString("SPTourID",null);
        //uid = firebaseAuth.getCurrentUser().getUid();

        if(getIntent().getExtras() != null){
            routeLocation = getIntent().getStringExtra("routeLocation");
            if(routeLocation != null){
                setRoutePoints(routeLocation);
            }
        }

        getAllRoutePointsFromDB();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllRoutePointsFromDB();
            }
        });

        configRouteRV();

        addRouteFABTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoutePointsActivity.this, RouteSearchPlaceActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }

    private void configRouteRV() {
        routePointsRV.setLayoutManager(new LinearLayoutManager(this));
        routePointsRV.setAdapter(routeListAdapter);
    }

    private void getAllRoutePointsFromDB() {
        DatabaseReference allRoutesRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Route points Lists");
        allRoutesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    routeList.clear();

                    for(DataSnapshot routeListData: dataSnapshot.getChildren()){
                        Route newRoute = routeListData.getValue(Route.class);
                        routeList.add(newRoute);

                        routeListAdapter.notifyDataSetChanged();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(RoutePointsActivity.this, "Route Points empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RoutePointsActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRoutePoints(String routeLocation) {
        DatabaseReference routeInfoRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Route points Lists");
        routeID = routeInfoRef.push().getKey();

        Route newRoute = new Route(routeLocation,routeID);
        routeInfoRef.child(routeID).setValue(newRoute).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RoutePointsActivity.this, "New Route Point Added", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RoutePointsActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        routePointsRV = findViewById(R.id.routePointsListRV);
        routeList = new ArrayList<>();
        routeListAdapter = new RouteListAdapter(routeList,this);

        addRouteFABTN = findViewById(R.id.addRouteFABTN);
        rp = findViewById(R.id.rpTV);
        swipeRefreshLayout = findViewById(R.id.routeSwipe);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void goToTourDetails(View view) {
        Intent intent = new Intent(RoutePointsActivity.this,TourDetailsActivity.class);
        intent.putExtra("tourID",tourID);
        startActivity(intent);
        finish();
    }
}
