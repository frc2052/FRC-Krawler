package com.team2052.frckrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.List;

public class AddListDialogActivity extends Activity implements OnClickListener {
	
	public static final int REQUEST_REFRESH_CODE = 1;
	public static final String EVENT_ID_EXTRA = "com.team2052.frckralwer.eventIDExtra";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_add_list);
		
		findViewById(R.id.addListButton).setOnClickListener(this);
		findViewById(R.id.cancelButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.addListButton) {
			if(!DBManager.getInstance(this).addList(new List(
					getIntent().getIntExtra(EVENT_ID_EXTRA, -1),
					((EditText)findViewById(R.id.listsName)).getText().toString(),
					((EditText)findViewById(R.id.listsDescription)).getText().toString()
					))) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Error");
				builder.setMessage("There was an error in adding the list to the database.");
				builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			} else {
				setResult(ListsActivity.REQUEST_REFRESH);
				finish();
			}
		} else if(v.getId() == R.id.cancel) {
			setResult(ListsActivity.REQUEST_NO_REFRESH);
			finish();
		}
	}
}
