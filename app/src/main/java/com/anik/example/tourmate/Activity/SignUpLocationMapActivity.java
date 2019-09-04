package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
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

public class SignUpLocationMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private TextView mapLocationTV, searchLocationTV;
    private Button mapConfirmLocationBTN;
    private LinearLayout searchLocationClick;
    private Button editLocationBTN;
    private EditText mapLocationET;
    private ImageView editLocation, locationMarker;
    private FloatingActionButton mapCurrentLocationFABTN;
    private String setLocation;
    private String signUpName = null, signUpEmail = null, signUpPassword = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_location_map);
        init();

        sharedPreferences = getSharedPreferences("signUpInfoSP", MODE_PRIVATE);
        signUpName = sharedPreferences.getString("signUpName", null);
        signUpEmail = sharedPreferences.getString("signUpEmail", null);
        signUpPassword = sharedPreferences.getString("signUpPassword", null);

        if (getIntent().getExtras() != null) {
            int intentSource = getIntent().getIntExtra("fromSignUpPlaceSearch", 0);

            if (intentSource == 1) {
                String searchResultLocation = getIntent().getStringExtra("signUpSearchLocation");
                mapLocationTV.setText(searchResultLocation);
            }
        }

        mapConfirmLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = mapLocationTV.getText().toString();
                if (location.length() <= 0) {
                    Toast.makeText(SignUpLocationMapActivity.this, "Pick Location please", Toast.LENGTH_SHORT).show();
                }
                else if (signUpName != null && signUpEmail != null && signUpPassword != null) {
                    Intent intent = new Intent(SignUpLocationMapActivity.this, SignUpActivity.class);
                    intent.putExtra("signUpIntentLocation", location);
                    intent.putExtra("intentSource", 1);
                    intent.putExtra("signUpIntentName", signUpName);
                    intent.putExtra("signUpIntentEmail", signUpEmail);
                    intent.putExtra("signUpIntentPassword", signUpPassword);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SignUpLocationMapActivity.this, SignUpActivity.class);
                    intent.putExtra("intentSource", 2);
                    intent.putExtra("signUpIntentLocation", location);
                    startActivity(intent);
                    finish();
                }
            }
        });

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editLocation.setVisibility(View.INVISIBLE);
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
                editLocation.setVisibility(View.VISIBLE);
                String location = mapLocationET.getText().toString();
                if (location.length() <= 0) {
                    Toast.makeText(SignUpLocationMapActivity.this, "Please Type location", Toast.LENGTH_SHORT).show();
                } else if (location.length() <= 3) {
                    Toast.makeText(SignUpLocationMapActivity.this, "Type Proper Address", Toast.LENGTH_SHORT).show();
                } else {
                    mapLocationET.setVisibility(View.GONE);
                    mapLocationTV.setVisibility(View.VISIBLE);
                    mapLocationTV.setText(location);
                    editLocationBTN.setVisibility(View.GONE);
                    mapConfirmLocationBTN.setVisibility(View.VISIBLE);
                }
            }
        });

        searchLocationClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpLocationMapActivity.this, SignUpPlaceSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng initialZoomedLatLng = new LatLng(23.7508851, 90.3926964);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialZoomedLatLng, 16));

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                locationMarker.setVisibility(View.VISIBLE);
                targetLocation(map);
            }
        });


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void targetLocation(final GoogleMap googleMap) {
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng targetLatLng = googleMap.getCameraPosition().target;
                locationMarker.setVisibility(View.GONE);
                googleMap.addMarker(new MarkerOptions().position(targetLatLng).title(getAddress(targetLatLng.latitude, targetLatLng.longitude)).snippet(getAddress(targetLatLng.latitude, targetLatLng.longitude)));
                googleMap.addCircle(new CircleOptions().center(targetLatLng));
                mapLocationTV.setText(getAddress(targetLatLng.latitude, targetLatLng.longitude));
            }
        });
        googleMap.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void currentLocation(View view) {
        locationMarker.setVisibility(View.GONE);
        getCurrentLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task location = fusedLocationProviderClient.getLastLocation();
        if (location != null) {
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                        map.addMarker(new MarkerOptions().position(latLng).title(getAddress(currentLocation.getLatitude(), currentLocation.getLongitude())).snippet("Your Location"));
                        map.addCircle(new CircleOptions().center(latLng));
                        setLocation = getAddress(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mapLocationTV.setText(setLocation);
                    }
                }
            });
        }
    }

    public String getAddress(double lat, double lng) {
        String address = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(lat, lng, 1);
            if (addressList.size() > 0) {
                address = addressList.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return address;
    }

    public void storeAsSharedPref(int intentSource) {
        sharedPreferences = getSharedPreferences("intentSourceInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("intentSource", intentSource);
        editor.apply();
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapLocationTV = findViewById(R.id.mapLocationAddressTV);
        mapLocationET = findViewById(R.id.mapLocationAddressET);
        searchLocationClick = findViewById(R.id.searchLocationClick);
        searchLocationTV = findViewById(R.id.searchLocationMapTV);
        editLocation = findViewById(R.id.editLocationIV);
        editLocationBTN = findViewById(R.id.mapEditDoneBTN);
        mapConfirmLocationBTN = findViewById(R.id.mapConfirmLocationBTN);
        mapCurrentLocationFABTN = findViewById(R.id.mapCurrentLocationFABTN);
        locationMarker = findViewById(R.id.locationMarkerIV);
    }
}