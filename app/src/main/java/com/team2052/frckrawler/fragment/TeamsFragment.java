package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.activity.TeamInfoActivity;
import com.team2052.frckrawler.adapters.TeamRecyclerAdapter;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.TeamDao;

import org.lucasr.twowayview.ItemClickSupport;

import java.util.List;

public class TeamsFragment extends RecyclerViewFragment
{
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener()
        {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int i, long l)
            {
                Team team = mDaoSession.getTeamDao().load(((TeamRecyclerAdapter) mAdapter).getItemAt(i).getNumber());
                startActivity(TeamInfoActivity.newInstance(getActivity(), team));
            }
        });

        return view;
    }

    @Override
    public void loadList()
    {
        new GetTeamsTask().execute();
    }

    private class GetTeamsTask extends AsyncTask<Void, Void, List<Team>>
    {

        @Override
        protected List<Team> doInBackground(Void... params)
        {
            return mDaoSession.getTeamDao().queryBuilder().orderAsc(TeamDao.Properties.Number).list();
        }

        @Override
        protected void onPostExecute(List<Team> teams)
        {
            setAdapter(new TeamRecyclerAdapter(getActivity(), teams));
        }
    }
}
