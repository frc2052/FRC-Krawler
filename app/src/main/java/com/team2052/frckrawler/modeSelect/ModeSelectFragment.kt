package com.team2052.frckrawler.modeSelect

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.team2052.frckrawler.MainActivity
import com.team2052.frckrawler.R
import com.team2052.frckrawler.bluetooth.BluetoothManager
import com.team2052.frckrawler.bluetooth.configuration.BluetoothConfiguration
import com.team2052.frckrawler.bluetooth.configuration.server.BluetoothServerConfiguration
import com.team2052.frckrawler.databinding.ModeSelectFragmentBinding

class ModeSelectFragment : Fragment()
{
    private lateinit var viewModel: ModeSelectViewModel
    private var binding: ModeSelectFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = ModeSelectFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ModeSelectViewModel::class.java)

        // Bluetooth setup
        val bluetoothManager = BluetoothManager(requireContext())
        (activity as MainActivity).bluetoothManager = bluetoothManager

        // Newer Android devices require background location for bluetooth to work
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 0)
            }
        }
        bluetoothManager.setupBluetoothCapabilities((activity as MainActivity))

        // TODO: when ever the device mode is selected set the configuration accordingly (null for solo scouting)
        val configuration: BluetoothConfiguration? = bluetoothManager.setBluetoothConfiguration(BluetoothServerConfiguration())

        viewModel.state.observe(viewLifecycleOwner, ::render)
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        binding = null
    }

    // Render a new screen state
    private fun render(state: ModeSelectScreenState)
    {
        when (state) {
            is ModeSelectScreenState.Loading ->
            {

            }

            is ModeSelectScreenState.Error ->
            {
                setSampleText("Error")
            }

            is ModeSelectScreenState.Content ->
            {
                val list = ArrayList<String>();
                list.add("custom");
                list.add("passed");
                list.add("list");

                binding?.apply()
                {
                    remoteScoutExpandable.addSpinner("server", list)
                    serverExpandable.addSpinner("server", list)
                    serverExpandable.setContinueButtonListener()
                    {
                        findNavController().navigate(R.id.action_modeSelectFragment_to_serverFragment)
                    }
                    soloScoutingExpandable.addSpinner("server", list)
                }
            }
        }
    }

    private fun setSampleText(text: String)
    {
        binding?.apply()
        {
            text
        }
    }

}