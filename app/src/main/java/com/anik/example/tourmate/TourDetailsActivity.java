package com.anik.example.tourmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TourDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);

        init();
    }

    private void init() {

    }

    public void goToMainActivity(View view) {
        startActivity(new Intent(TourDetailsActivity.this,MainActivity.class));
    }
}
