package com.team2052.frckrawler.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class TextListEditor extends ListEditor implements DialogInterface.OnClickListener {
	
	private EditText t;
	
	public TextListEditor(Context context) {
		super(context);
	}

	protected void onAddButtonClicked() {
		t = new EditText(getContext());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle("Add...");
		builder.setView(t);
		builder.setPositiveButton("Add", this);
		builder.setNegativeButton("Cancel", this);
		builder.show();
	}

	public void onClick(DialogInterface dialog, int which) {
		if(which == DialogInterface.BUTTON_POSITIVE) {
			addValue(t.getText().toString(), t.getText().toString());
			dialog.dismiss();
			
		} else if(which == DialogInterface.BUTTON_NEGATIVE) {
			dialog.dismiss();
		}
	}
}
