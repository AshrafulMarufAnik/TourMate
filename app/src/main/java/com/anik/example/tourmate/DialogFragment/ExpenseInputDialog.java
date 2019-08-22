package com.anik.example.tourmate.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.anik.example.tourmate.R;

public class ExpenseInputDialog extends AppCompatDialogFragment {
    private EditText expenseTypeET,expenseNoteET,expenseAmountET,expenseDateET,expenseTimeET;
    private DialogListener dialogListener;
    private String note="";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.expense_input_dialog_layout,null);

        expenseTypeET = view.findViewById(R.id.expenseTypeET);
        expenseNoteET = view.findViewById(R.id.expenseNoteET);
        expenseAmountET = view.findViewById(R.id.expenseAmountET);
        expenseDateET = view.findViewById(R.id.expenseDateET);
        expenseTimeET = view.findViewById(R.id.expenseTimeET);

        builder.setView(view);
        builder.setTitle("Add new expense");
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(expenseTypeET.getText().toString().isEmpty() || expenseAmountET.getText().toString().isEmpty() || expenseDateET.getText().toString().isEmpty() || expenseTimeET.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Fill up all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    String type = expenseTypeET.getText().toString();
                    note = expenseNoteET.getText().toString();
                    double amount = Double.parseDouble(expenseAmountET.getText().toString());
                    String date = expenseDateET.getText().toString();
                    String time = expenseTimeET.getText().toString();

                    dialogListener.applyTexts(type,note,amount,date,time);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            dialogListener = (ExpenseInputDialog.DialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"Must implement DialogListener");
        }
    }

    public interface DialogListener{
        void applyTexts(String expenseType,String expenseNote,double expenseAmount,String expenseDate,String expenseTime);
    }
}
