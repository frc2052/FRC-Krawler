package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.background.DeleteGameTask;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.subscribers.KeyValueListSubscriber;
import com.team2052.frckrawler.util.Util;

import java.util.Map;

import rx.Observable;

/**
 * Created by adam on 6/14/15.
 */
public class GameInfoFragment extends ListViewFragment<Map<String, String>, KeyValueListSubscriber> {
    public static final String GAME_ID = "GAME_ID";

    private Game mGame;

    public static GameInfoFragment newInstance(long game_id) {
        Bundle args = new Bundle();
        args.putLong(GAME_ID, game_id);
        GameInfoFragment fragment = new GameInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame = dbManager.getGamesTable().load(getArguments().getLong(GAME_ID));
        setHasOptionsMenu(true);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends Map<String, String>> getObservable() {
        return dbManager.gameInfo(mGame);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_delete_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                buildDeleteDialog().show();
                break;
            case R.id.menu_edit:
                buildEditDialog().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog buildDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Game?");
        builder.setMessage("Are you sure you want to delete this game?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            new DeleteGameTask(getActivity(), mGame, true).execute();
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

    private AlertDialog buildEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AppCompatEditText name = new AppCompatEditText(getActivity());
        name.setText(mGame.getName());
        int padding = Util.getPixelsFromDp(getActivity(), 16);
        name.setPadding(padding, padding, padding, padding);
        builder.setView(name);
        builder.setTitle("Edit Game");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            mGame.setName(name.getText().toString());
            mGame.update();
            ((BaseActivity) getActivity()).setActionBarSubtitle(mGame.getName());
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }
}
