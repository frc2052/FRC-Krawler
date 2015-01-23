package com.team2052.frckrawler.core.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.listeners.ListUpdateListener;
import com.team2052.frckrawler.core.ui.ListEditor;
import com.team2052.frckrawler.core.ui.TextListEditor;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;

/**
 * @author Adam
 */
public class AddMetricFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final String GAME_NAME_EXTRA = "GAME_NAME";
    public static final String METRIC_CATEGORY = "METRIC_CATEGORY";
    private int mMetricCategory;
    private ListEditor list;
    private int mCurrentSelectedMetricType;
    private Game mGame;
    private Spinner mMetricTypeSpinner;
    private EditText mName, mDescription, mMinimum, mMaximum, mIncrementation;
    private FrameLayout mListEditor;
    private View mDivider;
    private View mListHeader;

    public static AddMetricFragment newInstance(int metricCategory, Game game) {
        AddMetricFragment f = new AddMetricFragment();
        Bundle args = new Bundle();
        args.putInt(METRIC_CATEGORY, metricCategory);
        args.putLong(GAME_NAME_EXTRA, game.getId());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMetricCategory = args.getInt(METRIC_CATEGORY, -1);
        mGame = ((FRCKrawler) getActivity().getApplication()).getDaoSession().getGameDao().load(args.getLong(GAME_NAME_EXTRA));
        list = new TextListEditor(getActivity());
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Add Metric");
        b.setView(initViews());
        b.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveMetric();
            }
        });
        b.setNegativeButton("Cancel", null);
        mMetricTypeSpinner.setOnItemSelectedListener(this);
        mMetricTypeSpinner.setSelection(0);
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
        mDivider = view.findViewById(R.id.divider);
        mListHeader = view.findViewById(R.id.list_header);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCurrentSelectedMetricType = position;
        switch (position) {
            case Utilities.MetricUtil.COUNTER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.VISIBLE);
                mListEditor.removeAllViews();
                mDivider.setVisibility(View.GONE);
                mListHeader.setVisibility(View.GONE);
                break;
            case Utilities.MetricUtil.SLIDER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.GONE);
                mListEditor.removeAllViews();
                mDivider.setVisibility(View.GONE);
                mListHeader.setVisibility(View.GONE);
                break;
            case Utilities.MetricUtil.CHOOSER:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.INVISIBLE);
                mIncrementation.setVisibility(View.GONE);
                list = new TextListEditor(getActivity());
                mListEditor.removeAllViews();
                mListEditor.addView(list);
                mDivider.setVisibility(View.VISIBLE);
                mListHeader.setVisibility(View.VISIBLE);
                break;
            case Utilities.MetricUtil.TIMER:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.INVISIBLE);
                mIncrementation.setVisibility(View.GONE);
                mListEditor.removeAllViews();
                mDivider.setVisibility(View.GONE);
                mListHeader.setVisibility(View.GONE);
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
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
    }

    private void saveMetric() {
        Metric m = null;
        String name = mName.getText().toString();
        String description = mDescription.getText().toString();
        
        switch (mCurrentSelectedMetricType) {
            case Utilities.MetricUtil.BOOLEAN:
                m = Utilities.MetricUtil.createBooleanMetric(
                        mGame,
                        Utilities.MetricUtil.MetricType.VALID_TYPES[mMetricCategory],
                        name,
                        description);
                break;

            case Utilities.MetricUtil.COUNTER:
                try {
                    m = Utilities.MetricUtil.createCounterMetric(mGame,
                            Utilities.MetricUtil.MetricType.VALID_TYPES[mMetricCategory],
                            name,
                            description,
                            Integer.parseInt(mMinimum.getText().toString()),
                            Integer.parseInt(mMaximum.getText().toString()),
                            Integer.parseInt(mIncrementation.getText().toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Could not create add_button. Make sure you " + "have filled out all of the necessary fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

            case Utilities.MetricUtil.SLIDER:
                try {
                    m = Utilities.MetricUtil.createSliderMetric(
                            mGame,
                            Utilities.MetricUtil.MetricType.VALID_TYPES[mMetricCategory],
                            name,
                            description,
                            Integer.parseInt(mMinimum.getText().toString()),
                            Integer.parseInt(mMaximum.getText().toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Could not create add_button. Make sure you " + "have filled out all of the necessary fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

            case Utilities.MetricUtil.CHOOSER:
                m = Utilities.MetricUtil.createChooserMetric(
                        mGame,
                        Utilities.MetricUtil.MetricType.VALID_TYPES[mMetricCategory],
                        name,
                        description,
                        list.getValues());
                break;
            case Utilities.MetricUtil.TIMER:
                m = Utilities.MetricUtil.createTimerMetric(mGame, Utilities.MetricUtil.MetricType.VALID_TYPES[mMetricCategory], name, description);
                break;
        }

        if (m != null) {
            ((FRCKrawler) getActivity().getApplication()).getDaoSession().getMetricDao().insert(m);
        }
        ((ListUpdateListener) getParentFragment()).updateList();
        dismiss();
    }

}