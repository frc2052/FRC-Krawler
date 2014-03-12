package com.team2052.frckrawler.gui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class PopupMenuButton extends ImageButton 
				implements OnClickListener, 
					android.content.DialogInterface.OnClickListener {
	private List<String> actionText;
	private List<Runnable> clickActions;
	
	public PopupMenuButton(Context context) {
		super(context);
		setOnClickListener(this);
		setImageResource(android.R.drawable.ic_menu_more);
		actionText = new ArrayList<String>();
		clickActions = new ArrayList<Runnable>();
	}
	
	public void addItem(String text, Runnable clickAction) {
		actionText.add(text);
		clickActions.add(clickAction);
	}

	@Override
	public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setItems(actionText.toArray(new String[0]), this);
		builder.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		clickActions.get(which).run();
	}
}
