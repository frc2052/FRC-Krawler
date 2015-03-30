package com.team2052.frckrawler.core.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.listeners.ListUpdateListener;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.ui.ListEditor;
import com.team2052.frckrawler.core.util.MetricUtil;
import com.team2052.frckrawler.db.Metric;

/**
 * @author Adam
 * @since 12/22/2014.
 */
public class EditMetricDialogFragment extends DialogFragment {
    private Spinner mMetricTypeSpinner;
    private EditText mName, mDescription, mMinimum, mMaximum, mIncrementation;
    private FrameLayout mListEditor;
    private View mDivider;
    private ListEditor list;
    private View mListHeader;
    private int mCurrentSelectedMetricType;
    private DBManager mDbSession;
    private Metric mMetric;

    public static EditMetricDialogFragment newInstance(Metric metric) {
        EditMetricDialogFragment fragment = new EditMetricDialogFragment();
        Bundle args = new Bundle();
        args.putLong(DatabaseActivity.PARENT_ID, metric.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDbSession = ((FRCKrawler) getActivity().getApplication()).getDBSession();

        super.onCreate(savedInstanceState);
    }

    private View initViews() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_metric, null);
        mMetricTypeSpinner = (Spinner) view.findViewById(R.id.type);
        mName = (EditText) view.findViewById(R.id.name);
        mDescription = (EditText) view.findViewById(R.id.description);
        mMinimum = (EditText) view.findViewById(R.id.minimum);
        mMaximum = (EditText) view.findViewById(R.id.maximum);
        mIncrementation = (EditText) view.findViewById(R.id.incrementation);
        mListEditor = (FrameLayout) view.findViewById(R.id.list_editor);
        mDivider = view.findViewById(R.id.divider);
        mListHeader = view.findViewById(R.id.list_header);

        mMetricTypeSpinner.setVisibility(View.GONE);
        return view;
    }

    public void setupEditor(int type) {
        switch (type) {
            case MetricUtil.COUNTER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.VISIBLE);
                mListEditor.removeAllViews();
                mDivider.setVisibility(View.GONE);
                mListHeader.setVisibility(View.GONE);
                break;
            case MetricUtil.SLIDER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.GONE);
                mListEditor.removeAllViews();
                mDivider.setVisibility(View.GONE);
                mListHeader.setVisibility(View.GONE);
                break;
            case MetricUtil.CHOOSER:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.INVISIBLE);
                mIncrementation.setVisibility(View.GONE);
                list = new ListEditor(getActivity());
                mListEditor.removeAllViews();
                mListEditor.addView(list);
                mDivider.setVisibility(View.VISIBLE);
                mListHeader.setVisibility(View.VISIBLE);
                break;
            case MetricUtil.CHECK_BOX:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.INVISIBLE);
                mIncrementation.setVisibility(View.GONE);
                list = new ListEditor(getActivity());
                mListEditor.removeAllViews();
                mListEditor.addView(list);
                mDivider.setVisibility(View.VISIBLE);
                mListHeader.setVisibility(View.VISIBLE);
                break;
            default:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.GONE);
                mIncrementation.setVisibility(View.GONE);
                mListEditor.removeAllViews();
                mDivider.setVisibility(View.GONE);
                mListHeader.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMetric = mDbSession.getDaoSession().getMetricDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Edit " + mMetric.getName());
        b.setView(initViews());
        b.setPositiveButton("Update", (dialog, which) -> onUpdateButtonPressed());
        b.setNegativeButton("Cancel", null);

        setupEditor(mMetric.getType());
        autoFill();
        return b.create();
    }

    private void onUpdateButtonPressed() {
        String name = mName.getText().toString();
        String description = mDescription.getText().toString();
        JsonObject data = new JsonObject();
        data.addProperty("description", description);
        switch (mMetric.getType()) {
            case MetricUtil.BOOLEAN:
                break;
            case MetricUtil.CHOOSER:
            case MetricUtil.CHECK_BOX:
                JsonElement values = JSON.getGson().toJsonTree(list.getValues());
                data.add("values", values);
                break;
            case MetricUtil.COUNTER:
                int inc = Integer.parseInt(mIncrementation.getText().toString());
                data.addProperty("inc", inc);
            case MetricUtil.SLIDER:
                int max = Integer.parseInt(mMaximum.getText().toString());
                int min = Integer.parseInt(mMinimum.getText().toString());
                data.addProperty("min", min);
                data.addProperty("max", max);
                break;
        }

        mMetric.setName(name);
        mMetric.setData(JSON.getGson().toJson(data));
        mDbSession.getDaoSession().update(mMetric);
        ((ListUpdateListener) getParentFragment()).updateList();
    }

    private void autoFill() {
        JsonObject data = JSON.getAsJsonObject(mMetric.getData());
        mName.setText(mMetric.getName());
        mDescription.setText(data.get("description").getAsString());

        switch (mMetric.getType()) {
            case MetricUtil.BOOLEAN:
                //do nothing
                break;
            case MetricUtil.COUNTER:
                mIncrementation.setText(data.get("inc").getAsString());
            case MetricUtil.SLIDER:
                mMinimum.setText(data.get("min").getAsString());
                mMaximum.setText(data.get("max").getAsString());
                break;
            case MetricUtil.CHOOSER:
            case MetricUtil.CHECK_BOX:
                JsonArray values = data.get("values").getAsJsonArray();
                if (list != null) {
                    for (JsonElement element : values) {
                        list.addListItem(element.getAsString());
                    }
                }
                break;
        }
    }
}
