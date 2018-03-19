package com.team2052.frckrawler.listitems.smart;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Metric;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class MetricItemView extends BindableFrameLayout<Metric> {
    @BindView(R.id.metric_list_name)
    TextView mName;

    @BindView(R.id.up_button)
    Button mUp;

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

        String[] metricTypes = getContext().getResources().getStringArray(R.array.metric_types);

        if (metric.getType() >= metricTypes.length) {
            typeString = "Unknown";
        } else {
            typeString = metricTypes[metric.getType()];
        }


        mUp.setOnClickListener(v -> moveUp(metric));

        mType.setText(typeString);
        mName.setText(metric.getName());


    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    private void moveUp(Metric metric) {
        List<Metric> datab = RxDBManager.getInstance(getContext()).getMetricsTable().query(null, null, metric.getGame_id(), null).list();
        int indx = datab.indexOf(metric);

        if (indx != 0) {
            long newi = datab.get(indx - 1).getId();
            datab.get(indx - 1).setId(metric.getId());
            datab.get(indx).setId(newi);
            datab.get(indx).update();
            datab.get(indx - 1).update();


        }


        Activity host = (Activity)this.getContext();

        host.recreate();

    }

}
