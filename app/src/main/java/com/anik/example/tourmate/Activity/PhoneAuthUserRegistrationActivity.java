package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anik.example.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PhoneAuthUserRegistrationActivity extends AppCompatActivity {
    private EditText nameET, emailET, locationET;
    private Button registerBTN;
    private String uid,number;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth_user_registration);
        init();

        if(getIntent().getExtras() != null){
            number = getIntent().getStringExtra("phoneNumber");
        }

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameET.getText().toString().isEmpty() || emailET.getText().toString().isEmpty() || locationET.getText().toString().isEmpty()) {
                    Toast.makeText(PhoneAuthUserRegistrationActivity.this, "Fill uo fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    String name = nameET.getText().toString();
                    String email = emailET.getText().toString();
                    String location = locationET.getText().toString();
                    String userID = firebaseUser.getUid();

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userID", userID);
                    userMap.put("loggedInWith", "Phone");
                    userMap.put("userPhoneNumber", number);
                    userMap.put("userName", name);
                    userMap.put("userEmail", email);
                    userMap.put("userLocation", location);

                    DatabaseReference userRef = databaseReference.child("User(TourMateApp)").child(userID).child("user information");
                    userRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(PhoneAuthUserRegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            storeAsSharedPref(1);
                            Intent intent = new Intent(PhoneAuthUserRegistrationActivity.this, MainActivity.class);
                            //intent.putExtra("phoneLoginSource",1);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PhoneAuthUserRegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void storeAsSharedPref(int phoneLoginInfoSP) {
        sharedPreferences = getSharedPreferences("phoneLoginSP",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("phoneLoginInfo",phoneLoginInfoSP);
        editor.apply();
    }

    private void init() {
        nameET = findViewById(R.id.phoneAuthUserNameET);
        emailET = findViewById(R.id.phoneAuthUserEmailET);
        locationET = findViewById(R.id.phoneAuthUserLocationET);
        registerBTN = findViewById(R.id.phoneAuthUserRegBTN);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
