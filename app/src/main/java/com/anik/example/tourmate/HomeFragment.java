package com.anik.example.tourmate;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HomeFragment extends Fragment {
    private View view;
    private LinearLayout addTourLL,profileLL,mapsLL,galleryLL;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();

        addTourLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddTourActivity.class);
                startActivity(intent);
            }
        });

        profileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void init() {
        addTourLL = view.findViewById(R.id.addNewTourClick);
        profileLL = view.findViewById(R.id.profileInfoClick);
        mapsLL = view.findViewById(R.id.openMapsClick);
        galleryLL = view.findViewById(R.id.openGalleryClick);
    }

}
