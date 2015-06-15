package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Team;

import java.util.List;

/**
 * @author Adam
 * @since 10/25/2014
 */
public class TeamRecyclerAdapter extends RecyclerView.Adapter<TeamRecyclerAdapter.TeamViewHolder> {
    private Context mContext;
    private List<Team> mTeams;

    public TeamRecyclerAdapter(Context context, List<Team> teams) {
        this.mContext = context;
        this.mTeams = teams;
    }

    @Override
    public TeamViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamViewHolder teamViewHolder, int pos) {
        final Team team = mTeams.get(pos);
        //teamViewHolder.location.setText(team.getLocation());
        teamViewHolder.name.setText(team.getName());
        teamViewHolder.number.setText(String.valueOf(team.getNumber()));
    }

    @Override
    public int getItemCount() {
        return mTeams.size();
    }

    public Team getItemAt(int pos) {
        return mTeams.get(pos);
    }

    public class TeamViewHolder extends RecyclerView.ViewHolder {

        TextView location;
        TextView number;
        TextView name;

        public TeamViewHolder(View itemView) {
            super(itemView);
            this.number = (TextView) itemView.findViewById(R.id.list_item_team_number);
            this.name = (TextView) itemView.findViewById(R.id.list_item_team_name);
            this.location = (TextView) itemView.findViewById(R.id.list_item_team_location);
        }
    }
}
