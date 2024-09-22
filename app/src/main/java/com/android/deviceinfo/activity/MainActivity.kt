package com.android.deviceinfo.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.deviceinfo.details.BuildDetails.getDeviceInfoList
import com.android.deviceinfo.details.BuildDetails.getVersionInfoList
import com.android.deviceinfo.adapter.DeviceInfoAdapter
import com.android.deviceinfo.databinding.ActivityMainBinding
import com.android.deviceinfo.details.ComprehensiveNetworkInfo



class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivityTag"

    private lateinit var binding: ActivityMainBinding

    private lateinit var comprehensiveNetworkInfo: ComprehensiveNetworkInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.buildInfoRecyclerView.layoutManager = LinearLayoutManager(this)
        val deviceInfoList = getDeviceInfoList()
        binding.buildInfoRecyclerView.adapter = DeviceInfoAdapter(deviceInfoList)



        binding.versionInfoRecyclerView.layoutManager = LinearLayoutManager(this)
        val versionInfo = getVersionInfoList()
        binding.versionInfoRecyclerView.adapter = DeviceInfoAdapter(versionInfo)

        comprehensiveNetworkInfo = ComprehensiveNetworkInfo(this)
        comprehensiveNetworkInfo.registerNetworkCallback(object :
            ComprehensiveNetworkInfo.NetworkChangeCallback {
            override fun onNetworkAvailable(networkType: String) {
                runOnUiThread {
                    binding.networkInfoDetails.text = networkType
                }
            }

            override fun onNetworkLost() {
                runOnUiThread {
                    binding.networkInfoDetails.text = buildString {
                        append("Network Lost")
                    }
                }
            }

        })


        binding.networkInfoRecyclerView.layoutManager = LinearLayoutManager(this)
        val networkInfo = comprehensiveNetworkInfo.networkInfo()
        binding.networkInfoRecyclerView.adapter = DeviceInfoAdapter(networkInfo)

    }


    override fun onDestroy() {
        super.onDestroy()
        comprehensiveNetworkInfo.unregisterNetworkCallback()
    }
}