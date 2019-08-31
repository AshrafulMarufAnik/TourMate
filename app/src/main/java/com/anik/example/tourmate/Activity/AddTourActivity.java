package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.anik.example.tourmate.R;
import com.anik.example.tourmate.ModelClass.Tour;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTourActivity extends AppCompatActivity {
    private Button addNewTourBTn;
    private EditText tourNameET,tourBudgetET;
    private LinearLayout addTourReturnDateClick;
    private LinearLayout tourLocationClick;
    private TextView dateTV,timeTV,setLocationTV,setReturnDateTV;
    private LinearLayout departureDateClick,departureTimeClick;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private long dateInMS;
    private String intentLocation;
    private String name,budget,returnDate,date,time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour);

        init();

        intentLocation = getIntent().getStringExtra("location");

        if(getIntent().getExtras() != null){
            intentLocation = getIntent().getStringExtra("location");
            name = getIntent().getStringExtra("intentName");
            budget = getIntent().getStringExtra("intentBudget");
            returnDate = getIntent().getStringExtra("intentReturnDate");
            date = getIntent().getStringExtra("intentDate");
            time = getIntent().getStringExtra("intentTime");

            setLocationTV.setText(intentLocation);
            tourNameET.setText(name);
            tourBudgetET.setText(budget);
            setReturnDateTV.setText(returnDate);
            dateTV.setText(date);
            timeTV.setText(time);
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
                if(tourNameET.getText().toString() != null || tourBudgetET.getText().toString() != null || setReturnDateTV.getText().toString() != null || dateTV.getText().toString() != null || timeTV.getText().toString() != null){
                    String name = tourNameET.getText().toString();
                    String budget = tourBudgetET.getText().toString();
                    String returnDate = setReturnDateTV.getText().toString();
                    String date = dateTV.getText().toString();
                    String time = timeTV.getText().toString();

                    Intent intent = new Intent(AddTourActivity.this,MapActivity.class);
                    intent.putExtra("intentSource",2);
                    intent.putExtra("name",name);
                    intent.putExtra("budget",budget);
                    intent.putExtra("returnDate",returnDate);
                    intent.putExtra("date",date);
                    intent.putExtra("time",time);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(AddTourActivity.this,MapActivity.class);
                    intent.putExtra("intentSource",2);
                    startActivity(intent);
                }
            }
        });

        addTourReturnDateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { returnDatePicker();
            }
        });

        addNewTourBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(intentLocation==null || tourNameET.getText().toString().isEmpty() || tourBudgetET.getText().toString().isEmpty() || setReturnDateTV.getText().toString().isEmpty()){
                    Toast.makeText(AddTourActivity.this, "No data added", Toast.LENGTH_SHORT).show();
                }
                else {
                    final String name = tourNameET.getText().toString();
                    final String location = intentLocation;
                    final double budget = Double.parseDouble(tourBudgetET.getText().toString());
                    final String returnDate = setReturnDateTV.getText().toString();
                    final String date = dateTV.getText().toString();
                    final String time = timeTV.getText().toString();

                    final String uid = firebaseAuth.getCurrentUser().getUid();

                    DatabaseReference tourInfoRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information");
                    final String tourID = tourInfoRef.push().getKey();

                    Tour newTour = new Tour(tourID,name,location,returnDate,date,time,budget);
                    tourInfoRef.child(tourID).setValue(newTour).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AddTourActivity.this, "New Tour added", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddTourActivity.this,TourDetailsActivity.class);
                                intent.putExtra("tourID",tourID);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddTourActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
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
        }, hour, minute,false);
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
        startActivity(new Intent(AddTourActivity.this,MainActivity.class));
        finish();
    }
}
