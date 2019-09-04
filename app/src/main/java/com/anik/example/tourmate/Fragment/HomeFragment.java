package com.anik.example.tourmate.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anik.example.tourmate.Activity.AddTourActivity;
import com.anik.example.tourmate.Activity.GalleryActivity;
import com.anik.example.tourmate.Activity.LoginActivity;
import com.anik.example.tourmate.Activity.MapActivity;
import com.anik.example.tourmate.Activity.NearbyPlacesActivity;
import com.anik.example.tourmate.Activity.ProfileActivity;
import com.anik.example.tourmate.Activity.TourHistoryActivity;
import com.anik.example.tourmate.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private View view;
    private LinearLayout addTourClick, profileClick, tourHistoryClick, galleryClick, nearbyClick, circleLocatorClick;
    private RelativeLayout openWeatherInfoClick;
    private Button logOutBTN;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleSignInAccount account;
    GoogleSignInClient googleSignInClient;
    private DatabaseReference databaseReference;
    private String userID;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int phoneLoginSP;
    private int phoneLoginIntentSource;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        init();

        account = GoogleSignIn.getLastSignedInAccount(getActivity());

        addTourClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddTourActivity.class);
                startActivity(intent);
            }
        });

        profileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        tourHistoryClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TourHistoryActivity.class);
                startActivity(intent);
            }
        });

        nearbyClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NearbyPlacesActivity.class);
                startActivity(intent);
            }
        });

        galleryClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        circleLocatorClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("intentSource",3);
                startActivity(intent);
            }
        });

        openWeatherInfoClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Under development", Toast.LENGTH_SHORT).show();
            }
        });

        logOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseUser != null){
                    firebaseAuth.signOut();
                    storeAsSharedPref(0);
                    startActivity(new Intent(getActivity(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    getActivity().finish();
                }
                else if(account != null){
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Sign out successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                getActivity().finish();
                            }
                        }
                    });
                }

            }
        });

        return view;
    }

    public void storeAsSharedPref(int phoneLoginInfoSP) {
        sharedPreferences = getActivity().getSharedPreferences("phoneLoginSP",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("phoneLoginInfo",phoneLoginInfoSP);
        editor.apply();
    }

    private void fireBaseStateListener(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d("Signed in","user ID: "+user.getUid());
                }
                else if(user!=null || phoneLoginSP == 1){
                    Log.d("Signed in","user ID: "+user.getUid());
                }
                else {
                    Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                    Intent intent =  new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        //FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //if(authStateListener!=null){
            //FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        //}
    }

    private void init() {
        addTourClick = view.findViewById(R.id.addNewTourClick);
        profileClick = view.findViewById(R.id.profileInfoClick);
        tourHistoryClick = view.findViewById(R.id.openAllTourHistoryClick);
        galleryClick = view.findViewById(R.id.openGalleryClick);
        nearbyClick = view.findViewById(R.id.openNearbyClick);
        circleLocatorClick = view.findViewById(R.id.openCircleLocatorClick);
        openWeatherInfoClick = view.findViewById(R.id.openWeatherInfoClick);
        logOutBTN = view.findViewById(R.id.logOutBTN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

}
