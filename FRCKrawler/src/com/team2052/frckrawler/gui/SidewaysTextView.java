package com.team2052.frckrawler.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.widget.TextView;

public class SidewaysTextView extends TextView {
	
	public static final int DOWN_UP = 0;
	public static final int UP_DOWN = 1;
	
	private int direction;
	
	public SidewaysTextView(Context _context) {
		
		this(_context, "");
	}
	
	public SidewaysTextView(Context _context, String text) {
		
		this(_context, text, DOWN_UP);
	}
	
	public SidewaysTextView(Context _context, String _text, int _direction) {
		
		super(_context);
		
		setText(_text);
		setTextSize(18);
		
		direction = _direction;
	}
	
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		    super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		    setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}
	
	public void onDraw(Canvas canvas) {
		
	    TextPaint textPaint = getPaint(); 
	    textPaint.setColor(getCurrentTextColor());
	    textPaint.drawableState = getDrawableState();

	    canvas.save();

	    if(direction != DOWN_UP){
	       canvas.translate(getWidth(), 0);//Draw up to down
	       canvas.rotate(90);
	    }else {
	       canvas.translate(0, getHeight());//Draw down to up
	       canvas.rotate(-90);
	    }


	    canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

	    getLayout().draw(canvas);
	    canvas.restore();
	}
}
