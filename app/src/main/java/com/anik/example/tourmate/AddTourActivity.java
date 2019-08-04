package com.anik.example.tourmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.cachapa.expandablelayout.ExpandableLayout;

public class AddTourActivity extends AppCompatActivity {
    private Button addNewTourBTn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour);

        init();

        addNewTourBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddTourActivity.this, "No data added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        addNewTourBTn = findViewById(R.id.saveNewTourBTN);
    }
}
