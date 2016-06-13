package com.team2052.frckrawler.consumer;

import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.subscribers.BaseScoutData;
import com.team2052.frckrawler.views.metric.MetricWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Adam on 5/4/2016.
 */
public class BaseScoutDataConsumer extends DataConsumer<BaseScoutData> {
    @BindView(R.id.comments)
    public TextInputLayout mComments;
    @BindView(R.id.robot)
    public Spinner mSpinner;
    @BindView(R.id.metric_widget_list)
    public LinearLayout mMetricList;

    private List<Robot> robots;

    @Override
    public void updateData(BaseScoutData data) {
        if (data == null) {
            return;
        }

        if (robots == null || robots.size() != data.getRobots().size()) {
            robots = data.getRobots();
            List<String> robotStrings = Lists.newArrayList();
            for (int i = 0; i < robots.size(); i++) {
                Robot event = robots.get(i);
                robotStrings.add(event.getTeam_id() + ", " + event.getTeam().getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, robotStrings);
            mSpinner.setAdapter(adapter);

            // Reload
            mSpinner.setSelection(0);
        }

        setValues(data.getMetricValues());
        if (mComments.getEditText() != null) {
            mComments.getEditText().setText(data.getComment());
        }
    }

    public List<MetricValue> getValues() {
        List<MetricValue> values = new ArrayList<>();
        for (int i = 0; i < mMetricList.getChildCount(); i++) {
            values.add(((MetricWidget) mMetricList.getChildAt(i)).getValue());
        }
        return values;
    }

    protected void setValues(List<MetricValue> metricValues) {
        if (metricValues.size() != mMetricList.getChildCount()) {
            //This shouldn't happen, but just in case
            mMetricList.removeAllViews();
            for (int i = 0; i < metricValues.size(); i++) {
                Optional<MetricWidget> widget = MetricWidget.createWidget(mActivity, metricValues.get(i));
                if (widget.isPresent()) {
                    mMetricList.addView(widget.get());
                }
            }
        } else {
            for (int i = 0; i < metricValues.size(); i++) {
                ((MetricWidget) mMetricList.getChildAt(i)).setMetricValue(metricValues.get(i));
            }
        }
    }

    public String getComment() {
        if (mComments == null || mComments.getEditText() == null) {
            return "";
        } else {
            return mComments.getEditText().getText().toString();
        }
    }

    @Nullable
    public Robot getSelectedRobot() {
        if (robots == null)
            return null;
        return robots.get(mSpinner.getSelectedItemPosition());
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, rootView);
    }
}
