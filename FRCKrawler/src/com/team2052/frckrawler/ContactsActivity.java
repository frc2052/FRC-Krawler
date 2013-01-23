package com.team2052.frckrawler;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.Contact;
import com.team2052.frckrawler.gui.MyTextView;

public class ContactsActivity extends StackableTabActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
	}
	
	public void onStart() {
		
		super.onStart();
		
		TableLayout table = (TableLayout)findViewById(R.id.contactsDataTable);
		TableRow row = (TableRow)findViewById(R.id.descriptorsRow);
		
		table.removeAllViews();
		table.addView(row);
		
		Contact[] contacts = DatabaseManager.getInstance(this).
				getContactsByColumns(databaseKeys, parents);
		
		//MyButton editButton = new MyButton(this,)
		
		/*for(Contact c : contacts) {
		 
		 			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			table.addView(new MyTableRow(this, new View[] {
					//editButton,
					new MyTextView(this, c.getName()),
					new MyTextView(this, c.getName()),
					new MyTextView(this, c.getName()),
				}));
			
		}*/
	}
}
