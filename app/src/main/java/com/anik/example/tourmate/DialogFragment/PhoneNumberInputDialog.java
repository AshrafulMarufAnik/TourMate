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

import java.util.zip.Inflater;

public class PhoneNumberInputDialog extends AppCompatDialogFragment {
    private EditText phoneNumberET;
    private DialogListener dialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.phone_log_in_dialog_layout,null);

        builder.setView(view);
        builder.setTitle("Log in using phone");
        builder.setCancelable(false);
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
                    Toast.makeText(getActivity(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
                }
                else {
                    String phoneNumber = phoneNumberET.getText().toString();
                    dialogListener.applyText(phoneNumber);
                }
            }
        });

        phoneNumberET = view.findViewById(R.id.phoneNumberET);

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
