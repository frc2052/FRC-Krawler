package com.team2052.frckrawler.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.R;

import java.util.List;

public class ListEditor extends FrameLayout {

    private LinearLayout list;

    public ListEditor(Context context) {
        super(context);
        init();
    }

    public ListEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListEditor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_editor, this, true);
        View addButton = findViewById(R.id.list_editor_add);
        list = (LinearLayout) findViewById(R.id.list_editor_list);
        addButton.setOnClickListener(v -> {
            final TextView t = new EditText(ListEditor.this.getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(ListEditor.this.getContext());
            builder.setTitle("Add List Item");
            builder.setView(t);
            builder.setPositiveButton("Add", (dialog, which) -> {
                ListEditor.this.addListItem(t.getText().toString());
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    public void addListItem(String text) {
        ListEditorListItem listEditorListItem = new ListEditorListItem(getContext());
        list.addView(listEditorListItem);
        listEditorListItem.initWithParams(text);
    }

    public List<String> getValues() {
        List<String> values = Lists.newArrayList();

        for (int i = 0; i < list.getChildCount(); i++) {
            ListEditorListItem listEditorListItem = (ListEditorListItem) list.getChildAt(i);
            values.add(listEditorListItem.getText());
        }

        return values;
    }

    public class ListEditorListItem extends FrameLayout {

        private TextView textView;
        private String text;

        public ListEditorListItem(Context context) {
            super(context);
        }

        public ListEditorListItem(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ListEditorListItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public ListEditorListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        private void initWithParams(String text) {
            LayoutInflater.from(getContext()).inflate(R.layout.list_editor_list_item, this, true);
            this.text = text;

            textView = (TextView) findViewById(android.R.id.text1);
            textView.setText(text);

            findViewById(R.id.list_editor_remove).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.removeViewAt(ListEditorListItem.this.getIndex());
                }
            });
        }

        public int getIndex() {
            return list.indexOfChild(this);
        }

        public String getText() {
            return text;
        }
    }


}
