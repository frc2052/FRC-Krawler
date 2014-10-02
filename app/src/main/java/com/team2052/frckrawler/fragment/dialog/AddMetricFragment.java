package com.team2052.frckrawler.fragment.dialog;

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

import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.MetricFactory;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.gui.ListEditor;
import com.team2052.frckrawler.gui.TextListEditor;

/**
 * @author Adam
 */
public class AddMetricFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener
{

    public static final String GAME_NAME_EXTRA = "GAME_NAME";
    public static final String METRIC_CATEGORY = "METRIC_CATEGORY";
    private int mMetricCategory;
    private ListEditor list;
    private int mCurrentSelectedMetricType;
    private Game mGame;
    private Spinner mMetricTypeSpinner;
    private EditText mName, mDescription, mMinimum, mMaximum, mIncrementation;
    private FrameLayout mListEditor;

    public static AddMetricFragment newInstance(int metricCategory, Game game)
    {
        AddMetricFragment f = new AddMetricFragment();
        Bundle args = new Bundle();
        args.putInt(METRIC_CATEGORY, metricCategory);
        args.putLong(GAME_NAME_EXTRA, game.getId());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMetricCategory = args.getInt(METRIC_CATEGORY, -1);
        mGame = Game.load(Game.class, args.getLong(GAME_NAME_EXTRA));
        list = new TextListEditor(getActivity());
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Add Metric");
        b.setView(initViews());
        b.setPositiveButton("Add", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                saveMetric();
            }
        });
        b.setNegativeButton("Cancel", null);
        mMetricTypeSpinner.setOnItemSelectedListener(this);
        mMetricTypeSpinner.setSelection(0);
        return b.create();
    }

    private View initViews()
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialogactivity_add_metric, null);
        mMetricTypeSpinner = (Spinner) view.findViewById(R.id.type);
        mName = (EditText) view.findViewById(R.id.name);
        mDescription = (EditText) view.findViewById(R.id.description);
        mMinimum = (EditText) view.findViewById(R.id.minimum);
        mMaximum = (EditText) view.findViewById(R.id.maximum);
        mIncrementation = (EditText) view.findViewById(R.id.incrementation);
        mListEditor = (FrameLayout) view.findViewById(R.id.list_editor);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        mCurrentSelectedMetricType = position;
        switch (position) {
            case Metric.COUNTER:
                mMinimum.setEnabled(true);
                mMaximum.setEnabled(true);
                mIncrementation.setEnabled(true);
                mListEditor.removeAllViews();
                break;
            case Metric.SLIDER:
                mMinimum.setEnabled(true);
                mMaximum.setEnabled(true);
                mIncrementation.setEnabled(false);
                mListEditor.removeAllViews();
                break;
            case Metric.CHOOSER:
                mMinimum.setEnabled(false);
                mMaximum.setEnabled(false);
                mIncrementation.setEnabled(false);
                list = new TextListEditor(getActivity());
                mListEditor.removeAllViews();
                mListEditor.addView(list);
                break;
            default:
                mMinimum.setEnabled(false);
                mMaximum.setEnabled(false);
                mIncrementation.setEnabled(false);
                mListEditor.removeAllViews();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == 0) {


        } else if (v.getId() == R.id.cancel) {
            dismiss();
        }
    }

    private void saveMetric()
    {
        Metric m = null;
        switch (mCurrentSelectedMetricType) {
            case Metric.BOOLEAN:
                m = MetricFactory.createBooleanMetric(
                        mGame,
                        MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                        mName.getText().toString(),
                        mDescription.getText().toString());
                break;

            case Metric.COUNTER:
                try {
                    m = MetricFactory.createCounterMetric(
                            mGame,
                            MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                            mName.getText().toString(),
                            mDescription.getText().toString(),
                            Integer.parseInt(mMinimum.getText().toString()),
                            Integer.parseInt(mMaximum.getText().toString()),
                            Integer.parseInt(mIncrementation.getText().toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Could not create addbutton. Make sure you " + "have filled out all of the necessary fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

            case Metric.SLIDER:
                try {
                    m = MetricFactory.createSliderMetric(
                            mGame,
                            MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                            mName.getText().toString(),
                            mDescription.getText().toString(),
                            Integer.parseInt(mMinimum.getText().toString()),
                            Integer.parseInt(mMaximum.getText().toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Could not create addbutton. Make sure you " + "have filled out all of the necessary fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

            case Metric.CHOOSER:
                m = MetricFactory.createChooserMetric(
                        mGame,
                        MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                        mName.getText().toString(),
                        mDescription.getText().toString(),
                        list.getValues());
                break;

        }

        if (m != null) {
            m.save();
        }
        ((ListUpdateListener) getActivity()).updateList();
        dismiss();
    }

}
