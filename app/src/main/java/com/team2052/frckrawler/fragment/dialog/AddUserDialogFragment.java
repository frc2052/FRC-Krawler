package com.team2052.frckrawler.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.User;

/**
 * @author Adam
 */
public class AddUserDialogFragment extends DialogFragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogactivity_add_user, null);
        view.findViewById(R.id.addUser).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        getDialog().setTitle("Add User");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                dismiss();
                break;
            case R.id.addUser:
                new User(((EditText) getView().findViewById(R.id.nameVal)).getText().toString().trim()).save();
                dismiss();
                return;
        }
    }
}
