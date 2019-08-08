package com.anik.example.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {
    private EditText verificationCodeET;
    private Button loginBTN;
    private String phoneNumber,code,verificationID;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        init();
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sendOTP();

    }

    public void phoneLogIn(View view) {
        code = verificationCodeET.getText().toString();

        if(code.isEmpty()){
            Toast.makeText(PhoneVerificationActivity.this, "Enter Verification code", Toast.LENGTH_SHORT).show();
        }
        else if(code.length()==6){
            verifyCode(code);
        }
        else {
            Toast.makeText(PhoneVerificationActivity.this, "Must be 6 digit code", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+88"+phoneNumber,60, TimeUnit.SECONDS,this,mCallBacks);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String SmsCode = phoneAuthCredential.getSmsCode();

            if(SmsCode!=null){
                verificationCodeET.setText(SmsCode);
                verifyCode(SmsCode);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerificationActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID = s;
        }
    };


    private void verifyCode(String smsCode) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID,smsCode);
        signInWithCredential(phoneAuthCredential);
    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(PhoneVerificationActivity.this,PhoneAuthUserRegistrationActivity.class);
                    intent.putExtra("phoneNumber",phoneNumber);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PhoneVerificationActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        verificationCodeET = findViewById(R.id.phoneOtpET);
        loginBTN = findViewById(R.id.phoneLogInBTN);
        firebaseAuth = FirebaseAuth.getInstance();
    }

}
