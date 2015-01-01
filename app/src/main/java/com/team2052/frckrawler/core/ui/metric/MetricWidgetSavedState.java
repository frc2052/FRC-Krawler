package com.team2052.frckrawler.core.ui.metric;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class MetricWidgetSavedState extends View.BaseSavedState {
    public static final Creator<MetricWidgetSavedState> CREATOR = new ClassLoaderCreator<MetricWidgetSavedState>() {
        @Override
        public MetricWidgetSavedState createFromParcel(Parcel source, ClassLoader loader) {
            return createFromParcel(source);
        }

        @Override
        public MetricWidgetSavedState createFromParcel(Parcel source) {
            return new MetricWidgetSavedState(source);
        }

        @Override
        public MetricWidgetSavedState[] newArray(int size) {
            return new MetricWidgetSavedState[size];
        }
    };
    String value;

    private MetricWidgetSavedState(Parcel source) {
        super(source);
    }

    public MetricWidgetSavedState(Parcelable superState) {
        super(superState);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.value);
    }
}
