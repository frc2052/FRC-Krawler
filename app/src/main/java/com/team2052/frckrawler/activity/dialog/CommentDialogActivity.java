package com.team2052.frckrawler.activity.dialog;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.gui.MyTextView;

public class CommentDialogActivity extends BaseActivity {

    public static final String COMMENT_ARRAY_EXTRA =
            "com.team2052.frckrawler.commentArrayExtra";
    public static final String MATCHES_ARRAY_EXTRA =
            "com.team2052.frckrawler.matchArrayExtra";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_comments);

        LinearLayout commentList = (LinearLayout) findViewById(R.id.commentList);
        String[] matchComments = getIntent().getStringArrayExtra(COMMENT_ARRAY_EXTRA);
        int[] matches = getIntent().getIntArrayExtra(MATCHES_ARRAY_EXTRA);

        for (int i = 0; i < matches.length; i++) {

            String commentString = "\nMatch " + matches[i] + ": ";

            try {
                commentString += matchComments[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            TextView t = new TextView(this);
            t.setText(commentString);
            t.setTextSize(18);
            t.setLayoutParams(new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            commentList.addView(new MyTextView(this, commentString));
        }

        if (matches.length == 0)
            commentList.addView(new MyTextView(this,
                    "This team hasn't played any matches yet."));
    }
}
