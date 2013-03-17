package com.team2052.frckrawler.gui;

import android.content.Context;
import android.widget.ProgressBar;

public class ProgressSpinner extends ProgressBar {

	public ProgressSpinner(Context context) {
		
		super(context);
		setIndeterminate(true);
	}
}
