package com.team2052.frckrawler.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.SetupFragmentAdapter;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.views.DisableSwipeViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 * @since 12/13/2014.
 */
public class SetupActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PREF_SETUP = "PREF_LOADED";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String LOG_TAG = "SetupActivity";
    @InjectView(R.id.view_pager)
    protected DisableSwipeViewPager pager;
    @InjectView(R.id.welcome_next_page)
    protected View welcomeNextButton;
    @InjectView(R.id.bluetooth_next_page)
    protected View bluetoothNextButton;
    @InjectView(R.id.enable_bluetooth_button)
    protected Button enableBluetoothButton;
    @InjectView(R.id.scout_card)
    protected CardView scoutCard;
    @InjectView(R.id.server_card)
    protected CardView serverCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);

        if (sharedPreferences.getBoolean(PREF_SETUP, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_setup);
        ButterKnife.inject(this);
        setupViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }

    private void setupViews() {
        //Setup view_pager
        pager.setEnableSwipe(false);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new SetupFragmentAdapter(this));


        //Setup on clicks
        scoutCard.setOnClickListener(this);
        serverCard.setOnClickListener(this);
        welcomeNextButton.setOnClickListener(this);
        bluetoothNextButton.setOnClickListener(this);
        enableBluetoothButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcome_next_page:
                Log.i(LOG_TAG, String.valueOf(BluetoothUtil.isBluetoothEnabled()));
                if (BluetoothUtil.isBluetoothEnabled()) {
                    pager.setCurrentItem(2);
                } else {
                    pager.goToNextPage();
                }
                break;
            case R.id.bluetooth_next_page:
            case R.id.enable_bluetooth_button:
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                break;
            case R.id.server_card:
            case R.id.scout_card:
                setupFinished();
                break;
        }
    }

    public void setupFinished() {
        SharedPreferences preferences = getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(PREF_SETUP, true);
        edit.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            pager.goToNextPage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
