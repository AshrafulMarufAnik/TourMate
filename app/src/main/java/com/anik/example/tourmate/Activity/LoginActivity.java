package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anik.example.tourmate.Reciever.CheckInternetConnection;
import com.anik.example.tourmate.DialogFragment.PhoneNumberInputDialog;
import com.anik.example.tourmate.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements PhoneNumberInputDialog.DialogListener {
    private final int RC_SIGN_IN = 0;
    private CheckInternetConnection checkInternetConnection;
    private RelativeLayout googleSignInClick;
    private EditText emailET, passwordET;
    private Button logInBTN;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String PhoneNumber;
    private int intentSource;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int phoneLoginSP;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getPermissions();
        init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        sharedPreferences = getSharedPreferences("phoneLoginSP", MODE_PRIVATE);
        phoneLoginSP = sharedPreferences.getInt("phoneLoginInfo", 0);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (getIntent().getExtras() != null) {
            String email = getIntent().getStringExtra("email");
            String password = getIntent().getStringExtra("password");
            emailET.setText(email);
            passwordET.setText(password);
            intentSource = getIntent().getIntExtra("phoneLoginSource",0);
        }

        if (phoneLoginSP == 1 && firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(firebaseUser != null && firebaseUser.isEmailVerified()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(account != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        logInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter Email & Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    String email = emailET.getText().toString();
                    String password = passwordET.getText().toString();
                    logInWithEmailPassword(email, password);
                }
            }
        });

        googleSignInClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissions() {
        String[] permissions = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(permissions, 0);

            }
        }
        else {
            Toast.makeText(this, "Please Check permissions in App info", Toast.LENGTH_LONG).show();
        }
    }

    private void logInWithEmailPassword(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()) {
                    checkEmailVerification();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Log in Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            boolean emailFlag = user.isEmailVerified();

            if (emailFlag) {
                Toast.makeText(this, "Log in Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            else {
                Toast.makeText(this, "Please check & verify your email", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        }
        catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(final GoogleSignInAccount account) {
        if(account != null){
            final String personId = account.getId();
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            Uri personPhoto = account.getPhotoUrl();

            //image storing to storage remaining

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userID", personId);
            userMap.put("loggedInWith", "Google");
            userMap.put("userName", personName);
            userMap.put("userEmail", personEmail);

            DatabaseReference userRef = databaseReference.child("User(TourMateApp)").child(personId).child("user information");
            userRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //storeGoogleSignInIdAsSharedPref(personId);
                        Toast.makeText(LoginActivity.this, "Sign-in Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                }
            });
        }
    }

    public void storeGoogleSignInIdAsSharedPref(String googleSignInId) {
        sharedPreferences = getSharedPreferences("googleLoginSP",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("googleLoginIdInfo",googleSignInId);
        editor.putString("signInType","googleSignIn");
        editor.apply();
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
        googleSignInClick = findViewById(R.id.googleSignUpClick);
        emailET = findViewById(R.id.loginEmailET);
        passwordET = findViewById(R.id.loginPasswordET);
        logInBTN = findViewById(R.id.logInBTN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void goToSignUp(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        finish();
    }

    public void checkConnectivity() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternetConnection, intentFilter);
    }

    public void phoneLogInWithOTP(View view) {
        openDialog();
    }

    private void openDialog() {
        PhoneNumberInputDialog phoneNumberInputDialog = new PhoneNumberInputDialog();
        phoneNumberInputDialog.show(getSupportFragmentManager(), "PhoneNumber Input Dialog");
        phoneNumberInputDialog.setCancelable(false);
    }

    @Override
    public void applyText(String phoneNumber) {
        String number = phoneNumber;
        Intent intent = new Intent(LoginActivity.this, PhoneVerificationActivity.class);
        intent.putExtra("phoneNumber", number);
        startActivity(intent);
    }
}
