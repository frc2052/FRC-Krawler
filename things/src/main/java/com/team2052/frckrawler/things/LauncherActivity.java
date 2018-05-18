package com.team2052.frckrawler.things;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.things.bluetooth.BluetoothClassFactory;
import com.google.android.things.bluetooth.BluetoothConfigManager;
import com.google.android.things.bluetooth.BluetoothConnectionManager;
import com.google.android.things.bluetooth.BluetoothPairingCallback;
import com.google.android.things.bluetooth.PairingParams;

public class LauncherActivity extends Activity implements BluetoothPairingCallback {
    private BluetoothConnectionManager mBluetoothConnectionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothConfigManager manager = BluetoothConfigManager.getInstance();
        manager.setIoCapability(BluetoothConfigManager.IO_CAPABILITY_NONE);

        startService(new Intent(this, ServerService.class));
        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));

        mBluetoothConnectionManager = BluetoothConnectionManager.getInstance();
        mBluetoothConnectionManager.registerPairingCallback(mBluetoothPairingCallback);
    }

    private final BluetoothPairingCallback mBluetoothPairingCallback = new BluetoothPairingCallback() {
        @Override
        public void onPairingInitiated(BluetoothDevice bluetoothDevice, PairingParams pairingParams) {
            mBluetoothConnectionManager.finishPairing(bluetoothDevice);
        }
    };
}
