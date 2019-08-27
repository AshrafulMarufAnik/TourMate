package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.anik.example.tourmate.Adapter.ImagePagerAdapter;
import com.anik.example.tourmate.ModelClass.Moment;
import com.anik.example.tourmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageFullScreenActivity extends Activity {
    private ViewPager viewPager;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private String tourID,uid;
    private ArrayList<Moment> momentArrayList;
    private ImagePagerAdapter imagePagerAdapter;
    private String image;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        init();

        sharedPreferences = getSharedPreferences("TourInfo",MODE_PRIVATE);
        tourID = sharedPreferences.getString("SPTourID",null);

        uid = firebaseAuth.getCurrentUser().getUid();

        if(savedInstanceState != null){
            image = getIntent().getStringExtra("image");
            position = Integer.parseInt(getIntent().getStringExtra("position"));
        }

        getAllImages();
        configPagerAdapter();
    }

    private void configPagerAdapter() {
        viewPager.setCurrentItem(position,true);
        viewPager.setAdapter(imagePagerAdapter);
    }

    private void getAllImages() {
        DatabaseReference allMomentsRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Tour Moments");
        //DatabaseReference allMomentsRef = databaseReference.child("User(TourMateApp)").child("All Tour Moments");
        allMomentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    momentArrayList.clear();

                    for(DataSnapshot momentData: dataSnapshot.getChildren()){
                        Moment newMoment = momentData.getValue(Moment.class);
                        momentArrayList.add(newMoment);
                        imagePagerAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        viewPager = findViewById(R.id.imageViewPager);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        momentArrayList = new ArrayList<>();
        imagePagerAdapter = new ImagePagerAdapter(momentArrayList,this);
    }
}
