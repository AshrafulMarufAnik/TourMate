package com.anik.example.tourmate;

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

public class PhoneNumberInputDialog extends AppCompatDialogFragment {
    private EditText phoneNumberET;
    private DialogListener dialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.phone_log_in_dialog_layout,null);
        phoneNumberET = view.findViewById(R.id.phoneNumberET);

        builder.setView(view);
        builder.setTitle("Log in using phone");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(phoneNumberET.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Enter Phone number", Toast.LENGTH_SHORT).show();
                }
                else if(phoneNumberET.getText().toString().length()<10){
                    Toast.makeText(getActivity(), "Phone number should be 11-digit", Toast.LENGTH_SHORT).show();
                }
                else {
                    String phoneNumber = phoneNumberET.getText().toString();
                    dialogListener.applyText(phoneNumber);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            dialogListener = (DialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"Must implement DialogListener");
        }
    }

    public interface DialogListener{
        void applyText(String phoneNumber);
    }
}
