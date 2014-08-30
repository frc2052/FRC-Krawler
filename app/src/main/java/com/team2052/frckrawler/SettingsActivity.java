package com.team2052.frckrawler;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.fragment.server.GamesFragment;
import com.team2052.frckrawler.fragment.server.OptionsFragment;
import com.team2052.frckrawler.fragment.server.ServerFragment;

public class SettingsActivity extends BaseActivity {

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Fragment fragment = new GamesFragment();
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.container, fragment, "mainFragment").commit();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.action_server:
                fragment = new ServerFragment();
                if (menu != null) {
                    menu.findItem(R.id.add_game).setVisible(false);
                }
                setTitle("Server");
                break;
            case R.id.action_games:
                fragment = new GamesFragment();
                if (menu != null) {
                    menu.findItem(R.id.add_game).setVisible(true);
                }
                setTitle("Games");
                break;
            case R.id.action_options:
                fragment = new OptionsFragment();
                if (menu != null) {
                    menu.findItem(R.id.add_game).setVisible(false);
                }
                setTitle("Options");
                break;
        }
        if (fragment != null) {
            fragment.setRetainInstance(true);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.container, fragment, "mainFragment").commit();
        }
        return super.onOptionsItemSelected(item);
    }
}
