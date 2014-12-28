package com.team2052.frckrawler.adapters;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;

/**
 * @author Adam
 * @since 12/13/2014.
 */
public class SetupFragmentAdapter extends PagerAdapter {

    private Activity activity;

    public SetupFragmentAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int resId = 0;

        switch (position) {
            case 0:
                resId = R.id.welcome_page;
                break;
            case 1:
                resId = R.id.bluetooth_setup_page;
                break;
            case 2:
                resId = R.id.setup_role_page;
                break;
        }

        return activity.findViewById(resId);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Do nothing
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
