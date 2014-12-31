package com.team2052.frckrawler.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.SetupFragmentAdapter;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.LogHelper;
import com.team2052.frckrawler.view.DisableSwipeViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 * @since 12/13/2014.
 */
public class SetupActivity extends ActionBarActivity implements View.OnClickListener {
    public static final String PREF_SETUP = "PREF_LOADED";
    private static final int REQUEST_ENABLE_BT = 1;
    @InjectView(R.id.view_pager)
    protected DisableSwipeViewPager pager;
    @InjectView(R.id.welcome_next_page)
    protected View welcomeNextButton;
    @InjectView(R.id.bluetooth_next_page)
    protected View bluetoothNextButton;
    @InjectView(R.id.server_button)
    protected View serverButton;
    @InjectView(R.id.scout_button)
    protected View scoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);

        if (sharedPreferences.getBoolean(PREF_SETUP, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_setup);
        ButterKnife.inject(this);
        setupViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        super.onCreate(savedInstanceState);
    }

    private void setupViews() {
        //Setup view_pager
        pager.setSwipeEnabled(false);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new SetupFragmentAdapter(this));


        //Setup on clicks
        scoutButton.setOnClickListener(this);
        serverButton.setOnClickListener(this);
        welcomeNextButton.setOnClickListener(this);
        bluetoothNextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcome_next_page:
                LogHelper.info(String.valueOf(BluetoothUtil.isBluetoothEnabled()));
                if (BluetoothUtil.isBluetoothEnabled()) {
                    pager.setCurrentItem(2);
                } else {
                    pager.advanceToNextPage();
                }
                break;
            case R.id.scout_button:
                //TODO FINISH SETUP SCOUT
                //This is not finished, allow the tester to use the app after this screen
                setupFinished();
                break;
            case R.id.server_button:
                //TODO FINISH SETUP SERVER
                //This is not finished, allow the tester to use the app after this screen
                setupFinished();
                break;
            case R.id.bluetooth_next_page:
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            pager.advanceToNextPage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setupFinished() {

        SharedPreferences preferences = getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(PREF_SETUP, true);
        edit.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
