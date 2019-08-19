package com.anik.example.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        if (firebaseUser!=null && firebaseUser.isEmailVerified()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        if(getIntent().getExtras()!=null){
            String email = getIntent().getStringExtra("email");
            String password = getIntent().getStringExtra("password");
            emailET.setText(email);
            passwordET.setText(password);
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
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
                googleSignIn();
            }
        });
    }

    private void logInWithEmailPassword(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()){
                    checkEmailVerification();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Log in Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            boolean emailFlag = user.isEmailVerified();

            if(emailFlag){
                Toast.makeText(this, "Log in Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
            else {
                Toast.makeText(this, "Please check & verify your email", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    fireBaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    Log.w("Error", "Google sign in failed", e);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("FireBaseAuthLog", "FireBaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    handleUser(user);
                } else {
                    Log.w("Sign in Error", "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                    handleUser(null);
                }
            }
        });

    }

    private void handleUser(FirebaseUser user) {
        String personId = user.getUid();
        String personName = user.getDisplayName();
        String personEmail = user.getEmail();
        Uri personPhoto = user.getPhotoUrl();

        //image storing to storage remaining

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userID", personId);
        userMap.put("loggedInWith", "Google");
        userMap.put("googleUserName", personName);
        userMap.put("googleUserEmail", personEmail);

        DatabaseReference userRef = databaseReference.child("User(TourMateApp)").child(personId).child("user information");
        userRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(LoginActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        phoneNumberInputDialog.show(getSupportFragmentManager(),"PhoneNumber Input Dialog");
    }

    @Override
    public void applyText(String phoneNumber) {
        String number = phoneNumber;
        Intent intent = new Intent(LoginActivity.this,PhoneVerificationActivity.class);
        intent.putExtra("phoneNumber",number);
        startActivity(intent);
    }
}
