package com.anik.example.tourmate;

import android.app.StatusBarManager;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private CheckInternetConnection checkInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        replaceFragment(new HomeFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnectivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(checkInternetConnection);
    }

    private void init() {
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        checkInternetConnection = new CheckInternetConnection();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(new HomeFragment());
                    return true;
                case R.id.navigation_tour_history:
                    replaceFragment(new HistoryFragment());
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
