package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anik.example.tourmate.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NearbyPlacesActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private LinearLayout resturentClick,hotelClick,mallClick,gasStationClick,atmClick,hospitalClick;
    private TextView nearbyLocationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng bangladesh = new LatLng(23.7808875, 90.2792378);  //LagLng of Bangladesh Set
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangladesh, 6));

        googleMap.addMarker(new MarkerOptions().position(bangladesh).title("Bangladesh").snippet("Iam hear"));

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void currentLocation(View view) {
        getCurrentLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task location = fusedLocationProviderClient.getLastLocation();
        if(location != null){
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Location currentLocation = (Location) task.getResult();
                        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                        map.addMarker(new MarkerOptions().position(latLng).title(getAddress(currentLocation.getLatitude(),currentLocation.getLongitude())).snippet("Your Location"));
                        map.addCircle(new CircleOptions().center(latLng));
                        String setLocation = getAddress(currentLocation.getLatitude(),currentLocation.getLongitude());
                        nearbyLocationTV.setText(setLocation);
                    }
                }
            });
        }
    }

    public String getAddress(double lat,double lng){
        String address = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(lat,lng,1);
            if(addressList.size()>0){
                address = addressList.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return address;
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        resturentClick = findViewById(R.id.resturentClick);
        hotelClick = findViewById(R.id.hotelClick);
        mallClick = findViewById(R.id.mallClick);
        gasStationClick = findViewById(R.id.gasStationClick);
        atmClick = findViewById(R.id.atmClick);
        hospitalClick = findViewById(R.id.hospitalClick);
        nearbyLocationTV = findViewById(R.id.nearbySearchLocationMapTV);
    }
}
