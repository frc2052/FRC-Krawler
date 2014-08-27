package com.team2052.frckrawler.activity.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.activity.ListsActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.List;

public class EditListDialogActivity extends BaseActivity implements OnClickListener {

    public static final String LIST_ID_EXTRA = "com.team2052.frckrawler.listIDExtra";

    private List list;
    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_edit_list);

        dbManager = DBManager.getInstance(this);
        List[] listArr = dbManager.getListsByColumns(
                new String[]{DBContract.COL_LIST_ID},
                new String[]{Integer.toString(getIntent().getIntExtra(LIST_ID_EXTRA, -1))});

        if (listArr.length > 0)
            list = listArr[0];

        ((EditText) findViewById(R.id.listsName)).setText(list.getName());
        ((EditText) findViewById(R.id.listsDescription)).setText(list.getDescription());
        findViewById(R.id.addListButton);
        findViewById(R.id.removeList);
        findViewById(R.id.cancelButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addListButton:

                setResult(ListsActivity.REQUEST_REFRESH);
                finish();
                break;

            case R.id.removeList:

                dbManager.removeList(list.getListID());
                setResult(ListsActivity.REQUEST_REFRESH);
                finish();
                break;

            case R.id.cancelButton:

                setResult(ListsActivity.REQUEST_NO_REFRESH);
                finish();
                break;
        }
    }
}
