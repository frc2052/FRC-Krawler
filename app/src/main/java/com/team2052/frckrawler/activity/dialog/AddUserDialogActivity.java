package com.team2052.frckrawler.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.User;

public class AddUserDialogActivity extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_add_user);

        ((Button) findViewById(R.id.addUser)).setOnClickListener(this);
        ((Button) findViewById(R.id.cancel)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;

            case R.id.addUser:
                DBManager.getInstance(this).addUser(new User(
                        ((EditText) findViewById(R.id.nameVal)).getText().toString().trim(),
                        false
                ));
                finish();
                break;
        }
    }
}
