package com.anik.example.tourmate.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.anik.example.tourmate.Adapter.PlacesAutoCompleteAdapter;
import com.anik.example.tourmate.Adapter.PredictionInterface;
import com.anik.example.tourmate.PlaceAPI.Prediction;
import com.anik.example.tourmate.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchForPlaceActivity extends AppCompatActivity implements PredictionInterface {
    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
    private SearchView searchView;
    private RecyclerView locationSearchRV;

    private List<Prediction> predictions;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_place);

        init();
        initRecyclerView();
        hideSearchViewIcon();
        searchViewAction();


    }

    private void init() {
        searchView = findViewById(R.id.searchlocationSV);
        locationSearchRV = findViewById(R.id.locationNameRecyclerView);

    }

    private void searchViewAction() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    predictions.clear();
                    placesAutoCompleteAdapter.notifyDataSetChanged();
                }
                placesAutoCompleteAdapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    private void hideSearchViewIcon() {
        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        magImage.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        predictions = new ArrayList<>();
        placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getApplicationContext(), predictions, this);
        locationSearchRV.setLayoutManager(new LinearLayoutManager(this));
        locationSearchRV.setAdapter(placesAutoCompleteAdapter);

    }

    @Override
    public void getPrediction(Prediction prediction) {
        geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(prediction.getDescription(), 1);
            hideSoftKeyboard();
            if (addresses.size()>0){
                Toast.makeText(this, String.valueOf(addresses.get(0).getLatitude()), Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    public void openMap(View view) {
        Intent intent = new Intent(SearchForPlaceActivity.this,MapActivity.class);
        intent.putExtra("intentSource",4);
        startActivity(intent);
    }
}

