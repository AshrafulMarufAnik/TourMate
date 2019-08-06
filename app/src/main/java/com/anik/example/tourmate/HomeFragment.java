package com.anik.example.tourmate;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {
    private View view;
    private LinearLayout addTourClick, profileClick, tourHistoryClick, galleryClick, nearbyClick, circleLocatorClick;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userID;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        if(firebaseUser!=null && firebaseUser.isEmailVerified()){
            userID = firebaseUser.getUid();
        }


        addTourClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddTourActivity.class);
                startActivity(intent);
            }
        });

        profileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        tourHistoryClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),TourHistoryActivity.class);
                startActivity(intent);
            }
        });

        nearbyClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NearbyPlacesActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void init() {
        addTourClick = view.findViewById(R.id.addNewTourClick);
        profileClick = view.findViewById(R.id.profileInfoClick);
        tourHistoryClick = view.findViewById(R.id.openAllTourHistoryClick);
        galleryClick = view.findViewById(R.id.openGalleryClick);
        nearbyClick = view.findViewById(R.id.openNearbyClick);
        circleLocatorClick = view.findViewById(R.id.openCircleLocatorClick);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

}
