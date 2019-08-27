package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private TextView mapLocationTV;
    private Button mapConfirmLocationBTN;
    private Button editLocationBTN;
    private EditText mapLocationET;
    private ImageView editLocation;
    private FloatingActionButton mapCurrentLocationFABTN;
    private String setLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        init();

        final int intentSource = getIntent().getIntExtra("intentSource",0);

        if(intentSource == 3){
            mapLocationTV.setVisibility(View.GONE);
            mapConfirmLocationBTN.setVisibility(View.GONE);
            Toast.makeText(MapActivity.this, "Under development", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MapActivity.this,MainActivity.class));
            finish();
        }

        mapConfirmLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(intentSource == 2){
                    String location = mapLocationTV.getText().toString();
                    if(location.length()<=0){
                        Toast.makeText(MapActivity.this, "Pick Location please", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(MapActivity.this,AddTourActivity.class);
                        intent.putExtra("location",location);
                        startActivity(intent);
                        finish();
                    }
                }
                else if(intentSource == 1){
                    String location = mapLocationTV.getText().toString();
                    if(location.length()<=0){
                        Toast.makeText(MapActivity.this, "Pick Location please", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(MapActivity.this,SignUpActivity.class);
                        intent.putExtra("location",location);
                        startActivity(intent);
                        finish();
                    }
                }
                else if(intentSource == 3){
                    Toast.makeText(MapActivity.this, "Circle Locator not done", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = mapLocationTV.getText().toString();
                mapLocationTV.setVisibility(View.INVISIBLE);
                mapLocationET.setVisibility(View.VISIBLE);
                mapLocationET.setText(location);
                mapConfirmLocationBTN.setVisibility(View.GONE);
                editLocationBTN.setVisibility(View.VISIBLE);
            }
        });

        editLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = mapLocationET.getText().toString();
                if(location.length()<=0){
                    Toast.makeText(MapActivity.this, "Please Type location", Toast.LENGTH_SHORT).show();
                }
                else if(location.length()<=3){
                    Toast.makeText(MapActivity.this, "Type Proper Address", Toast.LENGTH_SHORT).show();
                }
                else {
                    mapLocationET.setVisibility(View.GONE);
                    mapLocationTV.setVisibility(View.VISIBLE);
                    mapLocationTV.setText(location);
                    editLocationBTN.setVisibility(View.GONE);
                    mapConfirmLocationBTN.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng initialZoomedLatLng = new LatLng(23.7508851, 90.3926964);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialZoomedLatLng, 15));
        googleMap.addMarker(new MarkerOptions().position(initialZoomedLatLng));

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
                        setLocation = getAddress(currentLocation.getLatitude(),currentLocation.getLongitude());
                        mapLocationTV.setText(setLocation);
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

        mapLocationTV = findViewById(R.id.mapLocationAddressTV);
        mapLocationET = findViewById(R.id.mapLocationAddressET);
        editLocation = findViewById(R.id.editLocationIV);
        editLocationBTN = findViewById(R.id.mapEditDoneBTN);
        mapConfirmLocationBTN = findViewById(R.id.mapConfirmLocationBTN);
        mapCurrentLocationFABTN = findViewById(R.id.mapCurrentLocationFABTN);
    }
}
