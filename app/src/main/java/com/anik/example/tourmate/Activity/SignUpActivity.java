package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anik.example.tourmate.Reciever.CheckInternetConnection;
import com.anik.example.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private CheckInternetConnection checkInternetConnection;
    private EditText nameET,emailET,locationET,passwordET;
    private Button signUpBTN;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
        //checkConnectivity();

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameET.getText().toString().isEmpty() || emailET.getText().toString().isEmpty() || locationET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Fill up the fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    String name = nameET.getText().toString();
                    String email = emailET.getText().toString();
                    String location = locationET.getText().toString();
                    String password = passwordET.getText().toString();

                    signUpWithEmailPassword(name,email,location,password);
                }
            }
        });


    }

    private void signUpWithEmailPassword(final String name, final String email, final String location, final String password) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Check your email for verification", Toast.LENGTH_LONG).show();

                                String userID = firebaseAuth.getCurrentUser().getUid();
                                Map<String,Object> userMap = new HashMap<>();
                                userMap.put("userID",userID);
                                userMap.put("loggedInWith", "Email,Password");
                                userMap.put("userName",name);
                                userMap.put("userEmail",email);
                                userMap.put("userLocation",location);

                                DatabaseReference userRef = databaseReference.child("User(TourMateApp)").child(userID).child("user information");
                                userRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                            intent.putExtra("email",email);
                                            intent.putExtra("password",password);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //checkConnectivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterReceiver(checkInternetConnection);
    }

    private void init() {
        checkInternetConnection = new CheckInternetConnection();
        nameET = findViewById(R.id.signUpNameET);
        emailET = findViewById(R.id.signUpEmailET);
        locationET = findViewById(R.id.signUpLocationET);
        passwordET = findViewById(R.id.signUpPasswordET);
        signUpBTN = findViewById(R.id.signUpBTN);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public void goToLogIn(View view) {
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }

    public void checkConnectivity(){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternetConnection,intentFilter);
    }
}
