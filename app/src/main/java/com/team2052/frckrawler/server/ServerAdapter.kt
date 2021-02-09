package com.team2052.frckrawler.server

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.team2052.frckrawler.databinding.DeviceRowBinding


    //turn object passed into a view

    class ServerAdapter(
    ) : RecyclerView.Adapter<ServerAdapter.DeviceViewHolder>() {

        private var devices: List<String> = listOf("Banana", "Testing, Testing", "Can anyone hear me???")

        class DeviceViewHolder(val binding: DeviceRowBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DeviceRowBinding.inflate(inflater, parent, false)
            return DeviceViewHolder(binding)
        }

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            holder.binding.deviceView.text = devices[position]
        }

        override fun getItemCount() = devices.size

        fun setDevices(devices: List<String>) {
            this.devices = devices
            notifyDataSetChanged()
        }

    }