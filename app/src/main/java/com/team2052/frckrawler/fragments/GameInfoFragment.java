package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.crash.FirebaseCrash;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.subscribers.KeyValueListSubscriber;

import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        mGame = rxDbManager.getGamesTable().load(getArguments().getLong(GAME_ID));
        setHasOptionsMenu(true);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends Map<String, String>> getObservable() {
        return rxDbManager.gameInfo(mGame);
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
                new MaterialDialog.Builder(getContext())
                        .title(R.string.delete_game)
                        .positiveColorRes(R.color.red_800)
                        .positiveText(R.string.delete)
                        .negativeText(R.string.cancel)
                        .content(R.string.delete_game_message)
                        .onPositive((materialDialog, dialogAction) -> {
                            Observable.just(mGame)
                                    .map(game -> {
                                        rxDbManager.getGamesTable().delete(game);
                                        return game;
                                    })
                                    .observeOn(Schedulers.computation())
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .subscribe(onNext -> {
                                    }, onError -> {
                                        onError.printStackTrace();
                                        FirebaseCrash.report(onError);
                                    }, () -> getActivity().finish());
                        })
                        .show();
                break;
            case R.id.menu_edit:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.edit_game)
                        .input(getString(R.string.game_name), mGame.getName(), false, (materialDialog, charSequence) -> {
                            mGame.setName(charSequence.toString());
                            mGame.update();
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
