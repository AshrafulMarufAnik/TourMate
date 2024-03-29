package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.anik.example.tourmate.ModelClass.Expense;
import com.anik.example.tourmate.ModelClass.Route;
import com.anik.example.tourmate.R;
import com.anik.example.tourmate.ModelClass.Tour;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddTourActivity extends AppCompatActivity {
    private Button addNewTourBTn, updateTourBTN;
    private EditText tourNameET, tourBudgetET;
    private LinearLayout addTourReturnDateClick;
    private LinearLayout tourLocationClick;
    private TextView dateTV, timeTV, setLocationTV, setReturnDateTV, titleTV;
    private LinearLayout departureDateClick, departureTimeClick;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private long dateInMS;
    private String intentLocation;
    private int updateIntent=0, updateMapReturn;
    private String name, budget, returnDate, date, time;
    private String updateTID;
    private String uid;
    private int updateMapSource = 0;
    private ArrayList<Route> routeList = new ArrayList<>();
    private ArrayList<Expense> expenseList = new ArrayList<>();
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int intentSourceFromTourPlaceSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour);

        init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(AddTourActivity.this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            uid = account.getId();
        } else {
            uid = firebaseAuth.getCurrentUser().getUid();
        }

        if (getIntent().getExtras() != null) {
            intentSourceFromTourPlaceSearch = getIntent().getIntExtra("fromTourPlaceSearch", 0);
            updateIntent = getIntent().getIntExtra("updateIntent", 0);
        }

        if (intentSourceFromTourPlaceSearch == 1) {
            intentLocation = getIntent().getStringExtra("tourLocation");
            String name = getIntent().getStringExtra("intentTourName");
            String budget = getIntent().getStringExtra("intentTourBudget");
            String date = getIntent().getStringExtra("intentTourDate");
            String time = getIntent().getStringExtra("intentTourTime");
            String returnDate = getIntent().getStringExtra("intentTourReturnDate");

            setLocationTV.setText(intentLocation);
            tourNameET.setText(name);
            tourBudgetET.setText(budget);
            setReturnDateTV.setText(returnDate);
            dateTV.setText(date);
            timeTV.setText(time);

        }

        if (updateIntent == 1) {
            titleTV.setText("Update Tour");
            addNewTourBTn.setText("Update");

            sharedPreferences = getSharedPreferences("updateTourInfoSP",MODE_PRIVATE);
            updateTID = sharedPreferences.getString("SPUpdateTourId",null);
            String tName = sharedPreferences.getString("SPUpdateTourName",null);
            String tLocation = sharedPreferences.getString("SPUpdateTourLocation",null);
            double tBudget = Double.parseDouble(sharedPreferences.getString("SPUpdateTourBudget",null));
            String tReturnDate = sharedPreferences.getString("SPUpdateTourReturnDate",null);
            String tDate = sharedPreferences.getString("SPUpdateTourDate",null);
            String tTime = sharedPreferences.getString("SPUpdateTourTime",null);

            setLocationTV.setText(tLocation);
            tourNameET.setText(tName);
            tourBudgetET.setText(String.valueOf(tBudget));
            setReturnDateTV.setText(tReturnDate);
            dateTV.setText(tDate);
            timeTV.setText(tTime);
        }
        else if(updateIntent == 2){
            titleTV.setText("Update Tour");
            addNewTourBTn.setText("Update");

            sharedPreferences = getSharedPreferences("updateTourInfoSP",MODE_PRIVATE);
            updateTID = sharedPreferences.getString("SPUpdateTourId",null);
            String tName = sharedPreferences.getString("SPUpdateTourName",null);
            String tLocation = getIntent().getStringExtra("tourLocation");
            double tBudget = Double.parseDouble(sharedPreferences.getString("SPUpdateTourBudget",null));
            String tReturnDate = sharedPreferences.getString("SPUpdateTourReturnDate",null);
            String tDate = sharedPreferences.getString("SPUpdateTourDate",null);
            String tTime = sharedPreferences.getString("SPUpdateTourTime",null);

            setLocationTV.setText(tLocation);
            tourNameET.setText(tName);
            tourBudgetET.setText(String.valueOf(tBudget));
            setReturnDateTV.setText(tReturnDate);
            dateTV.setText(tDate);
            timeTV.setText(tTime);
        }

        departureDateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        departureTimeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker();
            }
        });

        tourLocationClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updateIntent == 1){
                    Intent intent = new Intent(AddTourActivity.this, TourLocationSearchActivity.class);
                    startActivity(intent);
                }
                else {
                    if (tourNameET.getText().toString() != null || tourBudgetET.getText().toString() != null || setReturnDateTV.getText().toString() != null || dateTV.getText().toString() != null || timeTV.getText().toString() != null) {
                        String name = tourNameET.getText().toString();
                        String budget = tourBudgetET.getText().toString();
                        String returnDate = setReturnDateTV.getText().toString();
                        String date = dateTV.getText().toString();
                        String time = timeTV.getText().toString();

                        storeNewTourInfoAsSharedPref(name, budget, date, time, returnDate);
                        Intent intent = new Intent(AddTourActivity.this, TourLocationSearchActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(AddTourActivity.this, TourLocationSearchActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        addTourReturnDateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnDatePicker();
            }
        });

        addNewTourBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateIntent == 1) {
                    updateTourData(updateTID);
                } else {
                    addNewTour();
                }
            }
        });
    }

    public void storeNewTourInfoAsSharedPref(String name, String budget, String date, String time, String returnDate) {
        sharedPreferences = getSharedPreferences("NewTourInfoSP", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("tourName", name);
        editor.putString("tourBudget", budget);
        editor.putString("tourDate", date);
        editor.putString("tourTime", time);
        editor.putString("tourReturnDate", returnDate);
        editor.commit();
        editor.apply();
    }

    private void addNewTour() {
        if (intentLocation == null || tourNameET.getText().toString().isEmpty() || tourBudgetET.getText().toString().isEmpty() || setReturnDateTV.getText().toString().isEmpty() || dateTV.getText().toString().isEmpty() || timeTV.getText().toString().isEmpty()) {
            Toast.makeText(AddTourActivity.this, "Fill up all fields", Toast.LENGTH_SHORT).show();
        } else {
            final String name = tourNameET.getText().toString();
            final String location = setLocationTV.getText().toString();
            final double budget = Double.parseDouble(tourBudgetET.getText().toString());
            final String returnDate = setReturnDateTV.getText().toString();
            final String date = dateTV.getText().toString();
            final String time = timeTV.getText().toString();

            //final String uid = firebaseAuth.getCurrentUser().getUid();

            DatabaseReference tourInfoRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information");
            final String tourID = tourInfoRef.push().getKey();

            Tour newTour = new Tour(tourID, name, location, returnDate, date, time, budget);
            tourInfoRef.child(tourID).setValue(newTour).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddTourActivity.this, "New Tour added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddTourActivity.this, TourDetailsActivity.class);
                        intent.putExtra("tourID", tourID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddTourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateTourData(final String updateTID) {
        if (setLocationTV.getText().toString() == null || tourNameET.getText().toString().isEmpty() || tourBudgetET.getText().toString().isEmpty() || setReturnDateTV.getText().toString().isEmpty()) {
            Toast.makeText(AddTourActivity.this, "Insert new data to update", Toast.LENGTH_SHORT).show();
        } else {
            String uName = tourNameET.getText().toString();
            String uLocation = setLocationTV.getText().toString();
            double uBudget = Double.parseDouble(tourBudgetET.getText().toString());
            String uReturnDate = setReturnDateTV.getText().toString();
            String uDate = dateTV.getText().toString();
            String uTime = timeTV.getText().toString();

            final String uid = firebaseAuth.getCurrentUser().getUid();

            DatabaseReference tourInfoRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(updateTID);

            tourInfoRef.child("Route points Lists").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        routeList.clear();
                        for (DataSnapshot routeListData : dataSnapshot.getChildren()) {
                            Route newRoute = routeListData.getValue(Route.class);
                            routeList.add(newRoute);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AddTourActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            tourInfoRef.child("Expense Lists").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        expenseList.clear();
                        for (DataSnapshot expenseListData : dataSnapshot.getChildren()) {
                            Expense newExpense = expenseListData.getValue(Expense.class);
                            expenseList.add(newExpense);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AddTourActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            //Tour updateTour = new Tour(updateTID,uName,uLocation,uReturnDate,uDate,uTime,uBudget);
            Tour updateTour = new Tour(updateTID, uName, uLocation, uReturnDate, uDate, uTime, uBudget, routeList, expenseList);

            tourInfoRef.setValue(updateTour).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddTourActivity.this, "Tour updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddTourActivity.this, TourDetailsActivity.class);
                        intent.putExtra("uTourID", updateTID);
                        intent.putExtra("intentSource", 1);
                        startActivity(intent);
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddTourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void returnDatePicker() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;

                String currentDate = day + "/" + month + "/" + year;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                Date date = null;

                try {
                    date = simpleDateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateInMS = date.getTime();
                setReturnDateTV.setText(simpleDateFormat.format(date));
            }
        };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void timePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(calendar.HOUR);
        int minute = calendar.get(calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR, hour);
                time.set(Calendar.MINUTE, minute);
                CharSequence charSequence = DateFormat.format("hh:mm a", time);
                timeTV.setText(charSequence);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void datePicker() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;

                String currentDate = day + "/" + month + "/" + year;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                Date date = null;

                try {
                    date = simpleDateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateInMS = date.getTime();
                dateTV.setText(simpleDateFormat.format(date));
            }
        };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void init() {
        addNewTourBTn = findViewById(R.id.saveNewTourBTN);
        tourNameET = findViewById(R.id.addTourNameET);
        titleTV = findViewById(R.id.a1TV);
        tourLocationClick = findViewById(R.id.addTourLocationClick);
        setLocationTV = findViewById(R.id.setMapLocationTV);
        tourBudgetET = findViewById(R.id.addTourBudgetET);
        addTourReturnDateClick = findViewById(R.id.addTourReturnDateClick);
        setReturnDateTV = findViewById(R.id.setReturnDateTV);
        departureDateClick = findViewById(R.id.addDepartureDateClick);
        departureTimeClick = findViewById(R.id.addDepartureTimeClick);
        dateTV = findViewById(R.id.addDateTV);
        timeTV = findViewById(R.id.addTimeTV);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void goBack(View view) {
        startActivity(new Intent(AddTourActivity.this, MainActivity.class));
        finish();
    }
}
