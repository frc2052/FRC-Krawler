package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.structures.Contact;
import com.team2052.frckrawler.gui.*;

public class ContactsActivity extends StackableTabActivity implements OnClickListener{
	
	private static final int EDIT_BUTTON_ID = 1;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		findViewById(R.id.addContact).setOnClickListener(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		TableLayout table = (TableLayout)findViewById(R.id.contactsDataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		Contact[] contacts = DBManager.getInstance(this).
				getContactsByColumns(databaseKeys, parents);
		
		for(int i = 0; i < contacts.length; i++) {
		 
			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			MyButton editButton = new MyButton
					(this, "Edit Contact", this, Integer.valueOf(contacts[i].getContactID()));
			editButton.setId(EDIT_BUTTON_ID);
			
			table.addView(new MyTableRow(this, new View[] {
					editButton,
					new MyTextView(this, contacts[i].getName(), 18),
					new MyTextView(this, contacts[i].getEmail(), 18),
					new MyTextView(this, contacts[i].getAddress(), 18),
					new MyTextView(this, contacts[i].getPhoneNumber(), 18)
				}, color));
		}
	}

	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addContact:
				
				i = new Intent(this, AddContactDialogActivity.class);
				i.putExtra(AddContactDialogActivity.TEAM_NUMBER_EXTRA, 
						Integer.parseInt(parents[getAddressOfDatabaseKey
						                           (DBContract.COL_TEAM_NUMBER)]));
				startActivity(i);
				
				break;
				
			case EDIT_BUTTON_ID:
				
				i = new Intent(this, EditContactDialogActivity.class);
				i.putExtra(EditContactDialogActivity.CONTACT_ID_EXTRA, 
						v.getTag().toString());
				startActivity(i);
				
				break;
		}
	}
}
