package com.team2052.frckrawler.gui;

/*****
 * Class: MyTextView
 * 
 * Summary: This lets the text and size of the text be defined in the constructors
 * of the TextView class. Otherwise, it is exactly the same.
 *****/

import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;

public class MyTextView extends TextView {

	public MyTextView(Context context, String _text) {
		
		super(context);
		setText(_text);
		setPadding(2, 2, 2, 2);
	}
	
	public MyTextView(Context context, String _text, float _textSize) {
		
		super(context);
		setText(_text);
		setTextSize(TypedValue.COMPLEX_UNIT_SP, _textSize);
		setPadding(2, 2, 2, 2);
	}
}
