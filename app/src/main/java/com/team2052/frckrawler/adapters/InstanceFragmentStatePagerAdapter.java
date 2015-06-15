package com.team2052.frckrawler.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by adam on 6/14/15.
 */
public abstract class InstanceFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public InstanceFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);

        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    /**
     * Gets the fragment that is currently viewed
     *
     * @param position the position in the adapter of the item
     * @return the object of the fragment
     */
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
