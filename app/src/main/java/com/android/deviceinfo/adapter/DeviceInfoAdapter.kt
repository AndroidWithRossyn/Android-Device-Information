package com.android.deviceinfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.deviceinfo.R

class DeviceInfoAdapter(private val deviceInfoList: List<Pair<String, String>>) :
    RecyclerView.Adapter<DeviceInfoAdapter.DeviceInfoViewHolder>() {

    class DeviceInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeTextView: TextView = view.findViewById(R.id.typeTextView)
        val valueTextView: TextView = view.findViewById(R.id.valueTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceInfoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.device_info_item, parent, false)
        return DeviceInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceInfoViewHolder, position: Int) {
        val (type, value) = deviceInfoList[position]
        holder.typeTextView.text = type
        holder.valueTextView.text = value
    }

    override fun getItemCount(): Int = deviceInfoList.size
}
