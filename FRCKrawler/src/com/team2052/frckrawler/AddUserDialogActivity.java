package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.User;

public class AddUserDialogActivity extends Activity implements OnClickListener {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diaolgactivity_add_user);
		
		((Button)findViewById(R.id.addUser)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
	}
	
	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.cancel :
				
				finish();
				
				break;
				
			case R.id.addUser :
				
				DatabaseManager.getInstance(this).addUser(new User(
						((EditText)findViewById(R.id.nameVal)).getText().toString(),
						((CheckBox)findViewById(R.id.superuserVal)).isChecked()
						));
				finish();
				
				break;
		}
	}
}
