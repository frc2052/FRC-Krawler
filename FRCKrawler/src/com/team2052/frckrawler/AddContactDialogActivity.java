package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Contact;

public class AddContactDialogActivity extends Activity implements OnClickListener {
	
	public static final String TEAM_NUMBER_EXTRA = "com.team2052.frckrawler.teamNumberExtra";
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_add_contact);
		
		((Button)findViewById(R.id.addContact)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
	}
	
	public void onClick(View v) {
		
		if(v.getId() == R.id.addContact) {
			
			DBManager.getInstance(this).addContact(new Contact(
					getIntent().getIntExtra(TEAM_NUMBER_EXTRA, -1),
					((EditText)findViewById(R.id.nameVal)).getText().toString(),
					((EditText)findViewById(R.id.email)).getText().toString(),
					((EditText)findViewById(R.id.address)).getText().toString(),
					((EditText)findViewById(R.id.phone)).getText().toString()
					));
			
			finish();
			
		} else if(v.getId() == R.id.cancel) {
			
			finish();
		}
		
	}
}
