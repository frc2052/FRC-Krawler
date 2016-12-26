package com.team2052.frckrawler.listitems.smart;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.util.MetricHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class MetricItemView extends BindableFrameLayout<Metric> {
    @BindView(R.id.metric_list_name)
    TextView mName;

    @BindView(R.id.metric_list_type)
    TextView mType;

    public MetricItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_metric;
    }

    @Override
    public void bind(Metric metric) {
        setOnClickListener(v -> notifyItemAction(SmartAdapterInteractions.EVENT_CLICKED));
        this.setFocusable(true);
        this.setClickable(true);

        String typeString;

        switch (metric.getType()) {
            case MetricHelper.BOOLEAN:
                typeString = "Boolean";
                break;
            case MetricHelper.COUNTER:
                typeString = "Counter";
                break;
            case MetricHelper.CHECK_BOX:
                typeString = "Checkbox";
                break;
            case MetricHelper.CHOOSER:
                typeString = "Chooser";
                break;
            case MetricHelper.SLIDER:
                typeString = "Slider";
                break;
            case MetricHelper.STOP_WATCH:
                typeString = "Stopwatch";
                break;
            default:
                typeString = "Unknown";
        }

        mType.setText(typeString);
        mName.setText(metric.getName());
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
