package com.anik.example.tourmate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.example.tourmate.ModelClass.Expense;
import com.anik.example.tourmate.R;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private ArrayList<Expense> expenseList;
    private Context context;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense currentExpense = expenseList.get(position);

        holder.typeTV.setText(currentExpense.getExpenseType());
        holder.noteTV.setText(currentExpense.getExpenseNote());
        holder.amountTV.setText(String.valueOf(currentExpense.getExpenseAmount()));
        holder.dateTV.setText(currentExpense.getExpenseDate());
        holder.timeTV.setText(currentExpense.getExpenseTime());

        String expenseID = currentExpense.getExpenseID();

        holder.menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //option menu
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
