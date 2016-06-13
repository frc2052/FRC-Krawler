package com.team2052.frckrawler.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.team2052.frckrawler.Constants;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.SetupFragmentAdapter;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.views.DisableSwipeViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Adam
 * @since 12/13/2014.
 */
public class SetupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PREF_SETUP = "PREF_LOADED";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String LOG_TAG = "SetupActivity";
    @BindView(R.id.view_pager)
    protected DisableSwipeViewPager pager;
    @BindView(R.id.welcome_next_page)
    protected View welcomeNextButton;
    @BindView(R.id.bluetooth_next_page)
    protected View bluetoothNextButton;
    @BindView(R.id.enable_bluetooth_button)
    protected Button enableBluetoothButton;
    @BindView(R.id.scout_card)
    protected CardView scoutCard;
    @BindView(R.id.server_card)
    protected CardView serverCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);

        if (sharedPreferences.getBoolean(PREF_SETUP, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);
        setupViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && BluetoothUtil.isBluetoothEnabled()) {
            setupFinished();
        } else {
            pager.goToNextPage();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcome_next_page:
                Log.i(LOG_TAG, String.valueOf(BluetoothUtil.isBluetoothEnabled()));
                int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    pager.goToNextPage();
                }
                break;
            case R.id.bluetooth_next_page:
            case R.id.enable_bluetooth_button:
                if (!BluetoothUtil.hasBluetoothAdapter())
                    setupFinished();
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
        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(PREF_SETUP, true);
        edit.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            setupFinished();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
