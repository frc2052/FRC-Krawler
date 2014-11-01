package com.team2052.frckrawler.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;

import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

/**
 * @author Adam
 * @since 10/25/2014
 */
public abstract class RecyclerViewFragment extends BaseFragment
{
    protected TwoWayView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.recyclerview, container, false);
        mRecyclerView = (TwoWayView) view.findViewById(R.id.recycler_view);

        if (mAdapter != null)
            setAdapter(mAdapter);
        else
            loadList();
        setLayoutManager();
        setDecor();
        return view;
    }

    public void setAdapter(RecyclerView.Adapter adapter)
    {
        mRecyclerView.setAdapter(mAdapter = adapter);
    }

    public void setDecor()
    {
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
    }

    public void setLayoutManager()
    {
        mRecyclerView.setLayoutManager(new ListLayoutManager(getActivity(), TwoWayLayoutManager.Orientation.VERTICAL));
    }

    public abstract void loadList();
}
