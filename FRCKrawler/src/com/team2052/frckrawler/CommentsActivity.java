package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.structures.Comment;
import com.team2052.frckrawler.gui.*;

//NOT IMPLEMENTED!

public class CommentsActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int EDIT_COMMENT_BUTTON_ID = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		
		((Button)findViewById(R.id.addComment)).setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		TableLayout table = (TableLayout)findViewById(R.id.commentsDataTable);
		TableRow row = (TableRow)findViewById(R.id.descriptorsRow);
		
		table.removeAllViews();
		table.addView(row);
		
		Comment[] c = DBManager.getInstance(this).
				getCommentsByColumns(databaseKeys, parents);
		
		for(int i = 0; i < c.length; i++) {
			
			int color = Color.TRANSPARENT;
			
			if(i % 2 != 0)
				color = Color.BLUE;
			
			table.addView(new MyTableRow(this, new View[] {
					new MyButton(this, "Edit Comment", this, Integer.valueOf(i)),
					new MyTextView(this, Integer.toString(c[i].getUserID()), 18),
					new MyTextView(this, c[i].getText(), 18),
					new MyTextView(this, c[i].getTimeStamp().toString(), 18)
			}, color));
		}
	}

	@Override
	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addComment :
				
				i = new Intent(this, AddCommentDialogActivity.class);
				i.putExtra(AddCommentDialogActivity.TEAM_NUMBER_EXTRA, 
						parents[getAddressOfDatabaseKey(DBContract.COL_TEAM_NUMBER)]);
				startActivity(i);
				
				break;
				
			case EDIT_COMMENT_BUTTON_ID :
				
				i = new Intent(this, EditCommentDialogActivity.class);
				i.putExtra(EditCommentDialogActivity.COMMENT_ID_EXTRA, 
						((Integer)v.getTag()).intValue());
				startActivity(i);
				
				break;
		}
	}

}
