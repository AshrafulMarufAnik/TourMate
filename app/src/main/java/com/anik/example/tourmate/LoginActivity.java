package com.anik.example.tourmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {

    }

    public void goToSignUp(View view) {
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        finish();
    }

    public void goToHome(View view) {
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
    }
}
