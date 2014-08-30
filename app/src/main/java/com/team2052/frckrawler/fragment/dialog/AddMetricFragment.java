package com.team2052.frckrawler.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.MetricFactory;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.gui.ListEditor;
import com.team2052.frckrawler.gui.MathMetricListEditor;
import com.team2052.frckrawler.gui.TextListEditor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 8/27/2014.
 */
public class AddMetricFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final String GAME_NAME_EXTRA = "GAME_NAME";
    public static final String METRIC_CATEGORY = "METRIC_CATEGORY";
    private int mMetricCategory;
    private ListEditor list;
    private int mCurrentSelectedMetricType;
    private Game mGame;

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
        mGame = Game.load(Game.class, args.getLong(GAME_NAME_EXTRA));
        list = new TextListEditor(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogactivity_add_metric, null);
        Spinner metricType = (Spinner) view.findViewById(R.id.type);
        getDialog().setTitle("Add Metric");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        metricType.setOnItemSelectedListener(this);
        metricType.setSelection(0);
        view.findViewById(R.id.add).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCurrentSelectedMetricType = position;
        switch (position) {
            case Metric.COUNTER:
                getView().findViewById(R.id.min).setEnabled(true);
                getView().findViewById(R.id.max).setEnabled(true);
                getView().findViewById(R.id.inc).setEnabled(true);
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).removeAllViews();
                break;
            case Metric.SLIDER:
                getView().findViewById(R.id.min).setEnabled(true);
                getView().findViewById(R.id.max).setEnabled(true);
                getView().findViewById(R.id.inc).setEnabled(false);
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).removeAllViews();
                break;
            case Metric.MATH:
                Metric[] choices;
                switch (MetricsActivity.MetricType.VALID_TYPES[mMetricCategory]) {
                    case MATCH_PERF_METRICS:
                        List<Metric> matchMetrics = new Select().from(Metric.class).where("Game = ?", mGame.getId()).and("Category = ?", MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal()).execute();
                        ArrayList<Metric> acceptedMetrics = new ArrayList<Metric>();
                        for (Metric met : matchMetrics) {
                            if (met.type == Metric.COUNTER || met.type == Metric.SLIDER)
                                acceptedMetrics.add(met);
                        }
                        choices = acceptedMetrics.toArray(new Metric[0]);
                        break;

                    case ROBOT_METRICS:
                        List<Metric> robotMetrics = new Select().from(Metric.class).where("Game = ?", mGame.getId()).and("Category = ?", MetricsActivity.MetricType.ROBOT_METRICS.ordinal()).execute();
                        ArrayList<Metric> choosableMetrics = new ArrayList<Metric>();
                        for (Metric m : robotMetrics)
                            if (m.type == Metric.COUNTER || m.type == Metric.SLIDER)
                                choosableMetrics.add(m);
                        choices = choosableMetrics.toArray(new Metric[0]);
                        break;
                    default:
                        choices = new Metric[0];
                }

                getView().findViewById(R.id.min).setEnabled(false);
                getView().findViewById(R.id.max).setEnabled(false);
                getView().findViewById(R.id.inc).setEnabled(false);
                list = new MathMetricListEditor(getActivity(), new String[0], choices);
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).removeAllViews();
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).addView(list);
                break;
            case Metric.CHOOSER:
                getView().findViewById(R.id.min).setEnabled(false);
                getView().findViewById(R.id.max).setEnabled(false);
                getView().findViewById(R.id.inc).setEnabled(false);
                list = new TextListEditor(getActivity());
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).removeAllViews();
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).addView(list);
                break;
            default:
                getView().findViewById(R.id.min).setEnabled(false);
                getView().findViewById(R.id.max).setEnabled(false);
                getView().findViewById(R.id.inc).setEnabled(false);
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).removeAllViews();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add) {
            Metric m = null;
            switch (mCurrentSelectedMetricType) {
                case Metric.BOOLEAN:
                    m = MetricFactory.createBooleanMetric(
                            mGame,
                            MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                            ((TextView) getView().findViewById(R.id.name)).getText().toString(),
                            ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                            ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case Metric.COUNTER:
                    try {
                        m = MetricFactory.createCounterMetric(
                                mGame,
                                MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                                ((TextView) getView().findViewById(R.id.name)).getText().toString(),
                                ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                                Integer.parseInt(((TextView) getView().findViewById(R.id.min)).getText().toString()),
                                Integer.parseInt(((TextView) getView().findViewById(R.id.max)).getText().toString()),
                                Integer.parseInt(((EditText) getView().findViewById(R.id.inc)).getText().toString()),
                                ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                        );
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
                                ((TextView) getView().findViewById(R.id.name)).getText().toString(),
                                ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                                Integer.parseInt(((TextView) getView().findViewById(R.id.min)).getText().toString()),
                                Integer.parseInt(((TextView) getView().findViewById(R.id.max)).getText().toString()),
                                ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                        );
                    } catch (NumberFormatException e) {

                        Toast.makeText(getActivity(), "Could not create addbutton. Make sure you " + "have filled out all of the necessary fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;

                case Metric.CHOOSER:
                    m = MetricFactory.createChooserMetric(
                            mGame,
                            MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                            ((TextView) getView().findViewById(R.id.name)).getText().toString(),
                            ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                            list.getValues(),
                            ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case Metric.TEXT:
                    m = MetricFactory.createTextMetric(
                            mGame,
                            MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                            ((TextView) getView().findViewById(R.id.name)).getText().toString(),
                            ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                            ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case Metric.MATH:
                    String[] selectedMetrics = list.getValues();
                    Integer[] selectedMetricIDs = new Integer[selectedMetrics.length];

                    for (int i = 0; i < selectedMetrics.length; i++)
                        selectedMetricIDs[i] = Integer.valueOf(selectedMetrics[i]);

                    m = MetricFactory.createMathMetric(
                            mGame,
                            MetricsActivity.MetricType.VALID_TYPES[mMetricCategory],
                            ((TextView) getView().findViewById(R.id.name)).getText().toString(),
                            ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                            selectedMetricIDs,
                            ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                    );
                    break;
            }

            if (m != null) {
                m.save();
            }
            MetricsActivity activity = (MetricsActivity) getActivity();
            activity.addMetricToList(m);
            dismiss();

        } else if (v.getId() == R.id.cancel) {
            dismiss();
        }
    }

}
