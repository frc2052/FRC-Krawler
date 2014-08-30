package com.team2052.frckrawler.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Game;

/**
 * @author Adam
 */
public class AddGameDialogFragment extends DialogFragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogactivity_add_game, null);
        getDialog().setTitle("Add Game");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        view.findViewById(R.id.addGame).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addGame:
                new Game(((TextView)getView().findViewById(R.id.nameVal)).getText().toString()).save();
                dismiss();
                break;

            case R.id.cancel:
                dismiss();
                return;
        }
    }
}
