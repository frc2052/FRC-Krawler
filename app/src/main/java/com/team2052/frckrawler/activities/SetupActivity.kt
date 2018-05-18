package com.team2052.frckrawler.activities

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import com.team2052.frckrawler.core.common.Constants
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.SetupFragmentAdapter
import com.team2052.frckrawler.data.firebase.FirebaseUtil
import com.team2052.frckrawler.core.bluetooth.BluetoothHelper
import com.team2052.frckrawler.views.DisableSwipeViewPager

/**
 * @author Adam
 * @since 12/13/2014.
 */
class SetupActivity : AppCompatActivity(), View.OnClickListener {
    @BindView(R.id.view_pager) lateinit var pager: DisableSwipeViewPager
    @BindView(R.id.welcome_next_page) lateinit var welcomeNextButton: View
    @BindView(R.id.bluetooth_next_page) lateinit var bluetoothNextButton: View
    @BindView(R.id.enable_bluetooth_button) lateinit var enableBluetoothButton: Button
    @BindView(R.id.scout_card) lateinit var scoutCard: CardView
    @BindView(R.id.server_card) lateinit var serverCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseUtil.getFirebaseDatabase()

        val sharedPreferences = getSharedPreferences(Constants.PREFS_FILE_NAME, 0)

        if (sharedPreferences.getBoolean(PREF_SETUP, false)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_setup)
        ButterKnife.bind(this)
        pager = findViewById<DisableSwipeViewPager>(R.id.view_pager)
        setupViews()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.grey_900)
        }
    }

    private fun setupViews() {
        //Setup view_pager
        pager.setEnableSwipe(false)
        pager.offscreenPageLimit = 2
        pager.adapter = SetupFragmentAdapter(this)


        //Setup on clicks
        scoutCard.setOnClickListener(this)
        serverCard.setOnClickListener(this)
        welcomeNextButton.setOnClickListener(this)
        bluetoothNextButton.setOnClickListener(this)
        enableBluetoothButton.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && BluetoothHelper.isBluetoothEnabled()) {
            setupFinished()
        } else {
            pager.goToNextPage()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.welcome_next_page -> {
                Log.i(LOG_TAG, BluetoothHelper.isBluetoothEnabled().toString())
                val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                } else {
                    if (BluetoothHelper.isBluetoothEnabled()) {
                        setupFinished()
                    } else {
                        pager.goToNextPage()
                    }
                }
            }
            R.id.bluetooth_next_page, R.id.enable_bluetooth_button -> {
                if (!BluetoothHelper.hasBluetoothAdapter())
                    setupFinished()
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            R.id.server_card, R.id.scout_card -> setupFinished()
        }
    }

    fun setupFinished() {
        val preferences = getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
        val edit = preferences.edit()
        edit.putBoolean(PREF_SETUP, true)
        edit.apply()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            setupFinished()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val PREF_SETUP = "PREF_LOADED"
        private val REQUEST_ENABLE_BT = 1
        private val LOG_TAG = "SetupActivity"
    }
}
