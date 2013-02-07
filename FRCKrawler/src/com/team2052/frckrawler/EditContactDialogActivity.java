package com.team2052.frckrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Contact;

public class EditContactDialogActivity extends Activity implements OnClickListener, DialogInterface.OnClickListener {
	
	public static final String CONTACT_ID_EXTRA = "com.team2052.frckrawler.contactIDExtra";
	
	private DBManager db;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_contact);
		
		((Button)findViewById(R.id.save)).setOnClickListener(this);
		((Button)findViewById(R.id.remove)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		
		db = DBManager.getInstance(this);
	}
	
	public void onStart() {
		
		super.onStart();
		
		Contact[] arr = db.getContactsByColumns(new String[] {DBContract.COL_CONTACT_ID}, 
				new String[] {getIntent().getStringExtra(CONTACT_ID_EXTRA)});
		
		Contact c;
		
		if(arr.length > 0)
			c = arr[0];
		else
			return;
		
		((EditText)findViewById(R.id.nameVal)).setText(c.getName());
		((EditText)findViewById(R.id.email)).setText(c.getEmail());
		((EditText)findViewById(R.id.address)).setText(c.getAddress());
		((EditText)findViewById(R.id.phone)).setText(c.getPhoneNumber());
	}

	public void onClick(View v) {
		
		if(v.getId() == R.id.save) {
			
			db.updateContacts(
					new String[] {DBContract.COL_CONTACT_ID}, 
					new String[] {getIntent().getStringExtra(CONTACT_ID_EXTRA)}, 
					new String[] {
						DBContract.COL_CONTACT_NAME,
						DBContract.COL_EMAIL,
						DBContract.COL_ADDRESS,
						DBContract.COL_PHONE_NUMBER
					}, new String[] {
						((EditText)findViewById(R.id.nameVal)).getText().toString(),
						((EditText)findViewById(R.id.email)).getText().toString(),
						((EditText)findViewById(R.id.address)).getText().toString(),
						((EditText)findViewById(R.id.phone)).getText().toString(),
					});
			
			finish();
			
		} else if(v.getId() == R.id.remove) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setMessage("Are you sure you want to remove this contact from the database? " +
					"They will be cast into the cold void of cyberspace for eternity.");
			builder.setTitle("");
			builder.setPositiveButton("Yes", this);
			builder.setNegativeButton("No", this);
			
			builder.show();
			
		} else if(v.getId() == R.id.cancel) {
			
			finish();
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		
		if(which == DialogInterface.BUTTON_POSITIVE) {
			
			db.removeContact(Integer.parseInt(getIntent().getStringExtra(CONTACT_ID_EXTRA)));
			dialog.dismiss();
			finish();
			
		} else if(which == DialogInterface.BUTTON_NEGATIVE) {
			
			dialog.dismiss();
		}
	}

}
