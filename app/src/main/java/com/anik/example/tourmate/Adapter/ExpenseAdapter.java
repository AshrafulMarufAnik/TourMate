package com.anik.example.tourmate.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.example.tourmate.ModelClass.Expense;
import com.anik.example.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private ArrayList<Expense> expenseList;
    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private SharedPreferences sharedPreferences;

    public ExpenseAdapter(ArrayList<Expense> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_expense_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Expense currentExpense = expenseList.get(position);

        holder.typeTV.setText(currentExpense.getExpenseType());
        holder.noteTV.setText(currentExpense.getExpenseNote());
        holder.amountTV.setText(String.valueOf(currentExpense.getExpenseAmount()));
        holder.dateTV.setText(currentExpense.getExpenseDate());
        holder.timeTV.setText(currentExpense.getExpenseTime());

        String expenseID = currentExpense.getExpenseID();

        holder.menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = context.getSharedPreferences("TourInfo",MODE_PRIVATE);
                String tourID = sharedPreferences.getString("SPTourID",null);
                String uid = firebaseAuth.getCurrentUser().getUid();
                final DatabaseReference tourRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Expense Lists").child(currentExpense.getExpenseID());

                PopupMenu popupMenu = new PopupMenu(context,holder.menuIV);
                popupMenu.inflate(R.menu.expense_option_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.deleteExpense){
                            tourRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, "Expense deleted", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            expenseList.remove(position);
                            notifyDataSetChanged();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView typeTV,noteTV,amountTV,dateTV,timeTV;
        private ImageView menuIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            typeTV = itemView.findViewById(R.id.expenseTypeTV);
            noteTV = itemView.findViewById(R.id.expenseNoteTV);
            amountTV = itemView.findViewById(R.id.expenseAmountTV);
            dateTV = itemView.findViewById(R.id.expenseDateTV);
            timeTV = itemView.findViewById(R.id.expenseTimeTV);
            menuIV = itemView.findViewById(R.id.expenseMenuIV);
        }
    }
}
