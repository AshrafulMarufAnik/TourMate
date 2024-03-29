package com.anik.example.tourmate.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import com.anik.example.tourmate.Reciever.CheckInternetConnection;
import com.anik.example.tourmate.Fragment.CircleFragment;
import com.anik.example.tourmate.Fragment.HomeFragment;
import com.anik.example.tourmate.Fragment.NotificationFragment;
import com.anik.example.tourmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private CheckInternetConnection checkInternetConnection;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SharedPreferences sharedPreferences;
    private int phoneLoginSP;
    private int phoneLoginIntentSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        //checkConnectivity();

        replaceFragment(new HomeFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private void fireBaseStateListener(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null || phoneLoginSP == 1){
                    Log.d("Signed in","user ID: "+user.getUid());
                }
                else if(phoneLoginSP ==1 || phoneLoginIntentSource == 1){
                    Log.d("Signed in","user ID: "+user.getUid());
                }
                else {
                    Toast.makeText(MainActivity.this, "You Signed Out", Toast.LENGTH_SHORT).show();
                    Intent intent =  new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checkConnectivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterReceiver(checkInternetConnection);
    }

    private void init() {
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        checkInternetConnection = new CheckInternetConnection();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(new HomeFragment());
                    return true;
                case R.id.navigation_notifications:
                    replaceFragment(new NotificationFragment());
                    return true;
                case R.id.navigation_myCircle:
                    replaceFragment(new CircleFragment());
                    return true;
            }
            return false;
        }
    };

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    public void checkConnectivity(){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternetConnection,intentFilter);
    }

}
