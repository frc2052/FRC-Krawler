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
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.gui.ListEditor;
import com.team2052.frckrawler.gui.MathMetricListEditor;
import com.team2052.frckrawler.gui.TextListEditor;
import com.team2052.frckrawler.listitems.MetricListElement;

import java.util.ArrayList;

/**
 * Created by Adam on 8/27/2014.
 */
public class AddMetricFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final String GAME_NAME_EXTRA = "GAME_NAME";
    public static final String METRIC_CATEGORY = "METRIC_CATEGORY";
    private String mGameName;
    private int mMetricCategory;
    private ListEditor list;
    private int mCurrentSelectedMetricType;

    public static AddMetricFragment newInstance(int metricCategory, String gameName) {
        AddMetricFragment f = new AddMetricFragment();
        Bundle args = new Bundle();
        args.putInt(METRIC_CATEGORY, metricCategory);
        args.putString(GAME_NAME_EXTRA, gameName);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        //mMetricCategory = args.getInt(METRIC_CATEGORY, MetricsActivity.MATCH_PERF_METRICS);
        mGameName = args.getString(GAME_NAME_EXTRA, "");
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
            case DBContract.COUNTER:
                getView().findViewById(R.id.min).setEnabled(true);
                getView().findViewById(R.id.max).setEnabled(true);
                getView().findViewById(R.id.inc).setEnabled(true);
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).removeAllViews();
                break;
            case DBContract.SLIDER:
                getView().findViewById(R.id.min).setEnabled(true);
                getView().findViewById(R.id.max).setEnabled(true);
                getView().findViewById(R.id.inc).setEnabled(false);
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).removeAllViews();
                break;
            case DBContract.MATH:
                DBManager db = DBManager.getInstance(getActivity());
                Metric[] choices;
                /*switch (mMetricCategory) {
                    case MetricsActivity.MATCH_PERF_METRICS:
                        Metric[] matchMetrics = db.getMatchPerformanceMetricsByColumns(new String[]{DBContract.COL_GAME_NAME}, new String[]{mGameName});
                        ArrayList<Metric> acceptedMetrics = new ArrayList<Metric>();
                        for (Metric met : matchMetrics) {
                            if (met.getType() == DBContract.COUNTER ||
                                    met.getType() == DBContract.SLIDER)
                                acceptedMetrics.add(met);
                        }
                        choices = acceptedMetrics.toArray(new Metric[0]);
                        break;

                    case MetricsActivity.ROBOT_METRICS:
                        Metric[] robotMetrics = db.getRobotMetricsByColumns(new String[]{DBContract.COL_GAME_NAME}, new String[]{mGameName});
                        ArrayList<Metric> choosableMetrics = new ArrayList<Metric>();
                        for (Metric m : robotMetrics)
                            if (m.getType() == DBContract.COUNTER ||
                                    m.getType() == DBContract.SLIDER)
                                choosableMetrics.add(m);
                        choices = choosableMetrics.toArray(new Metric[0]);
                        break;

                    default:
                        choices = new Metric[0];
                }*/

                /*getView().findViewById(R.id.min).setEnabled(false);
                getView().findViewById(R.id.max).setEnabled(false);
                getView().findViewById(R.id.inc).setEnabled(false);
                list = new MathMetricListEditor(getActivity(), new String[0], choices);
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).removeAllViews();
                ((FrameLayout) getView().findViewById(R.id.listEditorSlot)).addView(list);*/
                break;
            case DBContract.CHOOSER:
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
                case DBContract.BOOLEAN:
                    m = Metric.MetricFactory.createBooleanMetric(
                            mGameName,
                            ((EditText) getView().findViewById(R.id.name)).getText().toString(),
                            ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                            ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case DBContract.COUNTER:
                    try {
                        m = Metric.MetricFactory.createCounterMetric(
                                mGameName,
                                ((EditText) getView().findViewById(R.id.name)).getText().toString(),
                                ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                                Integer.parseInt(((EditText) getView().findViewById(R.id.min)).getText().toString()),
                                Integer.parseInt(((EditText) getView().findViewById(R.id.max)).getText().toString()),
                                Integer.parseInt(((EditText) getView().findViewById(R.id.inc)).getText().toString()),
                                ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                        );
                    } catch (NumberFormatException e) {

                        Toast.makeText(getActivity(), "Could not create addbutton. Make sure you " + "have filled out all of the necessary fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;

                case DBContract.SLIDER:
                    try {
                        m = Metric.MetricFactory.createSliderMetric(
                                mGameName,
                                ((EditText) getView().findViewById(R.id.name)).getText().toString(),
                                ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                                Integer.parseInt(((EditText) getView().findViewById(R.id.min)).getText().toString()),
                                Integer.parseInt(((EditText) getView().findViewById(R.id.max)).getText().toString()),
                                ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                        );
                    } catch (NumberFormatException e) {

                        Toast.makeText(getActivity(), "Could not create addbutton. Make sure you " + "have filled out all of the necessary fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;

                case DBContract.CHOOSER:
                    m = Metric.MetricFactory.createChooserMetric(
                            mGameName,
                            ((EditText) getView().findViewById(R.id.name)).getText().toString(),
                            ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                            list.getValues(),
                            ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case DBContract.TEXT:
                    m = Metric.MetricFactory.createTextMetric(
                            mGameName,
                            ((EditText) getView().findViewById(R.id.name)).getText().toString(),
                            ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                            ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case DBContract.MATH:
                    String[] selectedMetrics = list.getValues();
                    Integer[] selectedMetricIDs = new Integer[selectedMetrics.length];

                    for (int i = 0; i < selectedMetrics.length; i++)
                        selectedMetricIDs[i] = Integer.valueOf(selectedMetrics[i]);

                    m = Metric.MetricFactory.createMathMetric(
                            mGameName,
                            ((EditText) getView().findViewById(R.id.name)).getText().toString(),
                            ((EditText) getView().findViewById(R.id.description)).getText().toString(),
                            selectedMetricIDs,
                            ((CheckBox) getView().findViewById(R.id.displayed)).isChecked()
                    );

                    break;
            }

            DBManager db = DBManager.getInstance(getActivity());

            /*switch (mMetricCategory) {
                case MetricsActivity.MATCH_PERF_METRICS:
                    db.addMatchPerformanceMetric(m);
                    break;

                case MetricsActivity.ROBOT_METRICS:
                    db.addRobotMetric(m);
                    break;

                case MetricsActivity.DRIVER_METRICS:
                    db.addDriverMetric(m);
                    break;
            }*/
            MetricsActivity activity = (MetricsActivity) getActivity();
            activity.mAdapter.add(activity.mAdapter.getCount(), new MetricListElement(m, activity));
            activity.mAdapter.notifyDataSetChanged();
            dismiss();

        } else if (v.getId() == R.id.cancel) {
            dismiss();
        }
    }
}
