package com.anik.example.tourmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

public class TourHistoryActivity extends AppCompatActivity {
    private RecyclerView tourHistoryRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_history);
        init();

        getTourDataFromDBThroughModelClass();
        configTourHistoryRV();

    }

    private void configTourHistoryRV() {
    }

    private void getTourDataFromDBThroughModelClass() {
    }

    private void init() {
        tourHistoryRV = findViewById(R.id.tourHistoryRV);
    }

    public void goBack(View view) {
        onBackPressed();
    }
}
