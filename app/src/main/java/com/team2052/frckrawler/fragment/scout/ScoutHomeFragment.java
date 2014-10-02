package com.team2052.frckrawler.fragment.scout;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.*;
import com.team2052.frckrawler.bluetooth.*;
import com.team2052.frckrawler.database.models.User;
import com.team2052.frckrawler.gui.ProgressSpinner;

import java.util.List;

/**
 * @author Adam
 */
public class ScoutHomeFragment extends Fragment implements View.OnClickListener, AlertDialog.OnClickListener {
    private EditText scoutLoginName;
    private BluetoothDevice[] devices;
    private int selectedDeviceAddress;
    private int REQUEST_BT_ENABLE = 1;
    private String mAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        mAddress = sharedPreferences.getString(GlobalValues.MAC_ADRESS_PREF, "null");
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_scout_type, null);
        view.findViewById(R.id.sync).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sync:
                break;
            case R.id.login:
                break;
        }
    }



    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    private class UserDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                List<User> users = new Select().from(User.class).execute();
                boolean isValid = false;
                for (User u : users) {
                    if (u.name.equals(scoutLoginName.getText().toString())) {
                        GlobalValues.userID = u.getId();
                        isValid = true;
                    }
                }
                if (isValid) {
                    /*Intent i = new Intent(getApplicationContext(), ScoutTypeActivity.class);
                    startActivity(i);*/
                } else {
                    Toast.makeText(getActivity(), "Not a valid username. " + "The username must already be in the database.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    scoutLoginName = new EditText(getActivity());
                    scoutLoginName.setHint("Name");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Login");
                    builder.setView(scoutLoginName);
                    builder.setPositiveButton("Login", new UserDialogListener());
                    builder.setNegativeButton("Cancel", new UserDialogListener());
                    builder.show();
                }
            } else {
                dialog.dismiss();
            }
        }
    }
}
