package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anik.example.tourmate.ModelClass.Expense;
import com.anik.example.tourmate.Adapter.ExpenseAdapter;
import com.anik.example.tourmate.DialogFragment.ExpenseInputDialog;
import com.anik.example.tourmate.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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

import java.util.ArrayList;

public class ExpenseListActivity extends AppCompatActivity implements ExpenseInputDialog.DialogListener {
    private RecyclerView expenseListRV;
    private FloatingActionMenu addExpenseFAM;
    private FloatingActionButton addExpenseFABTN;
    private String tourID,uid,expenseID;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ArrayList<Expense> expenseList;
    private ExpenseAdapter expenseAdapter;
    private SwipeRefreshLayout swipeRefreshLayoutELA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        init();
        tourID = getIntent().getStringExtra("tourID");
        uid = firebaseAuth.getCurrentUser().getUid();

        addExpenseFABTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInputDialog();

            }
        });

        swipeRefreshLayoutELA.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getExpenseDataFromDBThroughModelClass();

            }
        });

        getExpenseDataFromDBThroughModelClass();
        configExpenseListRV();
    }

    private void configExpenseListRV() {
        expenseListRV.setLayoutManager(new LinearLayoutManager(this));
        expenseListRV.setAdapter(expenseAdapter);

    }

    private void getExpenseDataFromDBThroughModelClass() {
        DatabaseReference allExpensesRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Expense Lists");
        allExpensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    expenseList.clear();

                    for(DataSnapshot expenseListData: dataSnapshot.getChildren()){
                        Expense newExpense = expenseListData.getValue(Expense.class);
                        expenseList.add(newExpense);
                        expenseAdapter.notifyDataSetChanged();
                    }
                    swipeRefreshLayoutELA.setRefreshing(false);
                }
                else {
                    Toast.makeText(ExpenseListActivity.this, "Expense List is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openInputDialog() {
        ExpenseInputDialog expenseInputDialog = new ExpenseInputDialog();
        expenseInputDialog.show(getSupportFragmentManager(),"Expense input Dialog");
        addExpenseFAM.close(true);
        expenseInputDialog.setCancelable(false);
    }

    private void init() {
        expenseListRV = findViewById(R.id.expenseListRV);
        addExpenseFAM = findViewById(R.id.addExpenseFAM);
        addExpenseFABTN = findViewById(R.id.addExpenseFABTN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseList,this);

        swipeRefreshLayoutELA = findViewById(R.id.swiperefreshlayout_ELA);
    }

    @Override
    public void applyTexts(String expenseType, String expenseNote, double expenseAmount, String expenseDate, String expenseTime) {
        DatabaseReference expenseInfoRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Expense Lists");
        expenseID = expenseInfoRef.push().getKey();

        Expense newExpense = new Expense(expenseType,expenseNote,expenseDate,expenseTime,expenseID,expenseAmount);
        expenseInfoRef.child(expenseID).setValue(newExpense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ExpenseListActivity.this, "Expense Added", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExpenseListActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void goToTourDetailsActivity(View view) {
        Intent intent = new Intent(ExpenseListActivity.this,TourDetailsActivity.class);
        intent.putExtra("tourID",tourID);
        startActivity(intent);
        finish();
    }
}
