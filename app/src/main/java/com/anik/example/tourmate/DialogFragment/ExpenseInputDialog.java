package com.anik.example.tourmate.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.anik.example.tourmate.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExpenseInputDialog extends AppCompatDialogFragment {
    private EditText expenseTypeET,expenseNoteET,expenseAmountET;
    private Button expenseDateBtn,expenseTimeBtn;
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
        expenseDateBtn = view.findViewById(R.id.expenseDateBTN);
        expenseTimeBtn = view.findViewById(R.id.expenseTimeBTN);

        expenseDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        expenseTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker();
            }
        });

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
                if(expenseTypeET.getText().toString().isEmpty() || expenseAmountET.getText().toString().isEmpty() || expenseDateBtn.getText().toString().isEmpty() || expenseTimeBtn.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Fill up all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    String type = expenseTypeET.getText().toString();
                    note = expenseNoteET.getText().toString();
                    double amount = Double.parseDouble(expenseAmountET.getText().toString());
                    String date = expenseDateBtn.getText().toString();
                    String time = expenseTimeBtn.getText().toString();

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

    private void timePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(calendar.HOUR);
        int minute = calendar.get(calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR, hour);
                time.set(Calendar.MINUTE, minute);
                CharSequence charSequence = DateFormat.format("hh:mm a", time);
                expenseTimeBtn.setText(charSequence);
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
                expenseDateBtn.setText(simpleDateFormat.format(date));
            }
        };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener, year, month, day);
        datePickerDialog.show();
    }
}
