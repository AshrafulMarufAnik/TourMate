package com.anik.example.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        fireBaseStateListener();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void goBack(View view) {
        onBackPressed();
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
