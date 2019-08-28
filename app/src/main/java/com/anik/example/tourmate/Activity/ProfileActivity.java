package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anik.example.tourmate.R;
import com.anik.example.tourmate.ModelClass.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameTV,emailTV,locationTV;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        uid = firebaseAuth.getCurrentUser().getUid();
        fireBaseStateListener();

        getUserInfoFromDB();
    }

    private void getUserInfoFromDB() {
        DatabaseReference userRef = databaseReference.child("User(TourMateApp)").child(uid).child("user information");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User currentUser = dataSnapshot.getValue(User.class);
                    nameTV.setText(currentUser.getUserName());
                    emailTV.setText(currentUser.getUserEmail());
                    locationTV.setText(currentUser.getUserLocation());
                    // location and image works
                }
                else {
                    Toast.makeText(ProfileActivity.this, "No User Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init() {
        nameTV = findViewById(R.id.userNameTV);
        emailTV = findViewById(R.id.userEmailTV);
        locationTV = findViewById(R.id.userLocationTV);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void goBack(View view) {
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        finish();
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    private void fireBaseStateListener(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d("Signed in","user ID: "+user.getUid());
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    Intent intent =  new Intent(ProfileActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }
}
