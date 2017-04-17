package com.team2052.frckrawler.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

public abstract class ViewOnClickFragmentStatePagerAdapter extends InstanceFragmentStatePagerAdapter {

    public ViewOnClickFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void onClick(View view, int position) {
        Fragment registeredFragment = getRegisteredFragment(position);
        if (registeredFragment instanceof View.OnClickListener) {
            ((View.OnClickListener) registeredFragment).onClick(view);
        }
    }
}
