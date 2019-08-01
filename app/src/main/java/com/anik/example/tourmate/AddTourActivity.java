package com.anik.example.tourmate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.cachapa.expandablelayout.ExpandableLayout;

public class AddTourActivity extends AppCompatActivity {
    private BottomSheetBehavior mBottomSheetBehavior;
    private RelativeLayout addRouteClick;
    private TextView bottomSheetState;
    private ImageView bottomSheetStateIV;
    private ExpandableLayout expandableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour);

        init();



    }

    private void init() {

    }
}
