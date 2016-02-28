package com.team2052.frckrawler.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.views.ListEditor;

/**
 * @author Adam
 * @since 12/22/2014.
 */
public class EditMetricDialogFragment extends DialogFragment {
    private Spinner mMetricTypeSpinner;
    private EditText mName, mDescription, mMinimum, mMaximum, mIncrementation;
    private FrameLayout mListEditor;
    private ListEditor list;
    private View mListHeader;
    private DBManager mDbSession;
    private Metric mMetric;

    public static EditMetricDialogFragment newInstance(Metric metric) {
        EditMetricDialogFragment fragment = new EditMetricDialogFragment();
        Bundle args = new Bundle();
        args.putLong(BaseActivity.PARENT_ID, metric.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDbSession = DBManager.getInstance(getActivity());
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMetric = mDbSession.getMetricsTable().load(getArguments().getLong(BaseActivity.PARENT_ID));
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Edit Metric");
        b.setView(initViews());
        b.setPositiveButton("Update", (dialog, which) -> {
            EditMetricDialogFragment.this.onUpdateButtonPressed();
        });
        b.setNegativeButton("Cancel", null);

        setupEditor(mMetric.getType());
        autoFill();
        return b.create();
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
        mListHeader = view.findViewById(R.id.list_header);

        mMetricTypeSpinner.setVisibility(View.GONE);
        return view;
    }

    public void setupEditor(@MetricHelper.MetricType int type) {
        switch (type) {
            case MetricHelper.COUNTER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.VISIBLE);
                mListEditor.removeAllViews();
                mListHeader.setVisibility(View.GONE);
                break;
            case MetricHelper.SLIDER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.GONE);
                mListEditor.removeAllViews();
                mListHeader.setVisibility(View.GONE);
                break;
            case MetricHelper.CHOOSER:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.INVISIBLE);
                mIncrementation.setVisibility(View.GONE);
                list = new ListEditor(getActivity());
                mListEditor.removeAllViews();
                mListEditor.addView(list);
                mListHeader.setVisibility(View.VISIBLE);
                break;
            case MetricHelper.CHECK_BOX:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.INVISIBLE);
                mIncrementation.setVisibility(View.GONE);
                list = new ListEditor(getActivity());
                mListEditor.removeAllViews();
                mListEditor.addView(list);
                mListHeader.setVisibility(View.VISIBLE);
                break;
            default:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.GONE);
                mIncrementation.setVisibility(View.GONE);
                mListEditor.removeAllViews();
                mListHeader.setVisibility(View.GONE);
                break;
        }
    }

    private void onUpdateButtonPressed() {
        String name = mName.getText().toString();
        String description = mDescription.getText().toString();
        JsonObject data = new JsonObject();
        data.addProperty("description", description);
        switch (mMetric.getType()) {
            case MetricHelper.BOOLEAN:
                break;
            case MetricHelper.CHOOSER:
            case MetricHelper.CHECK_BOX:
                JsonElement values = JSON.getGson().toJsonTree(list.getValues());
                data.add("values", values);
                break;
            case MetricHelper.COUNTER:
                int inc = Integer.parseInt(mIncrementation.getText().toString());
                data.addProperty("inc", inc);
            case MetricHelper.SLIDER:
                int max = Integer.parseInt(mMaximum.getText().toString());
                int min = Integer.parseInt(mMinimum.getText().toString());
                data.addProperty("min", min);
                data.addProperty("max", max);
                break;
        }

        mMetric.setName(name);
        mMetric.setData(JSON.getGson().toJson(data));
        mMetric.update();
    }

    private void autoFill() {
        JsonObject data = JSON.getAsJsonObject(mMetric.getData());
        mName.setText(mMetric.getName());
        mDescription.setText(data.get("description").getAsString());

        switch (mMetric.getType()) {
            case MetricHelper.COUNTER:
                mIncrementation.setText(data.get("inc").getAsString());
            case MetricHelper.SLIDER:
                mMinimum.setText(data.get("min").getAsString());
                mMaximum.setText(data.get("max").getAsString());
                break;
            case MetricHelper.CHOOSER:
            case MetricHelper.CHECK_BOX:
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
