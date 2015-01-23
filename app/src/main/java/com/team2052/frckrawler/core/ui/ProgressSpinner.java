package com.team2052.frckrawler.core.ui;

import android.content.Context;
import android.widget.ProgressBar;

public class ProgressSpinner extends ProgressBar {

    public ProgressSpinner(Context context) {

        super(context, null, android.R.attr.progressBarStyleInverse);
        setIndeterminate(true);
    }
}