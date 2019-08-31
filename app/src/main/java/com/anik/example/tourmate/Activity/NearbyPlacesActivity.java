package com.anik.example.tourmate.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anik.example.tourmate.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearbyPlacesActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);


        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng bangladesh = new LatLng(23.7808875,90.2792378);  //LagLng of Bangladesh Set
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangladesh,6));

        googleMap.addMarker(new MarkerOptions().position(bangladesh).title("Bangladesh").snippet("Iam hear"));

        googleMap.setMyLocationEnabled(true);
    }
}
