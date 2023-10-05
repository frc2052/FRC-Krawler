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
import android.widget.Toast;

import com.team2052.frckrawler.Constants;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.SetupFragmentAdapter;
import com.team2052.frckrawler.firebase.FirebaseUtil;
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
    private static final int REQUEST_S_BT_PERMISSION = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 0;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 3;
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
        FirebaseUtil.getFirebaseDatabase();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);

        if (sharedPreferences.getBoolean(PREF_SETUP, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);
        setupViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.grey_900));
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
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "We don't have permission! Grant the permission in device settings.", Toast.LENGTH_LONG).show();
            return;
        }

        if (requestCode == REQUEST_S_BT_PERMISSION) {
            requestEnableBluetooth();
        } else if (requestCode == REQUEST_STORAGE_PERMISSION || requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (BluetoothUtil.isBluetoothEnabled()) {
                setupFinished();
            } else {
                pager.goToNextPage();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcome_next_page:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
                } else {
                    if (BluetoothUtil.isBluetoothEnabled()) {
                        setupFinished();
                    } else {
                        pager.goToNextPage();
                    }
                }
                break;
            case R.id.bluetooth_next_page:
            case R.id.enable_bluetooth_button:
                if (!BluetoothUtil.hasBluetoothAdapter()) {
                    setupFinished();
                }
                if (BluetoothUtil.hasBluetoothPermission(this)) {
                    requestEnableBluetooth();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_S_BT_PERMISSION);
                }
                break;
            case R.id.server_card:
            case R.id.scout_card:
                setupFinished();
                break;
        }
    }

    private void requestEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
