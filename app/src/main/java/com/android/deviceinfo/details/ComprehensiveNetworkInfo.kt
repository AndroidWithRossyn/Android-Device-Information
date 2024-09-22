package com.android.deviceinfo.details

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Locale

class ComprehensiveNetworkInfo(private val context: Context) {

    // Callback interface
    interface NetworkChangeCallback {
        fun onNetworkAvailable(networkType: String)
        fun onNetworkLost()
    }

    private val TAG = "ComprehensiveNetworkInfo"
    private val DEFAULT_IP_ADDRESS = "0.0.0.0"

    private var callback: NetworkChangeCallback? = null
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // NetworkRequest to listen for changes
    private val networkRequest =
        NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            callback?.onNetworkAvailable(getNetworkType())
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            callback?.onNetworkLost()
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, capabilities)
            Log.i("NetworkChangeListener", "Network Capabilities Changed: ${getNetworkType()}")
        }


    }

    // Method to get current network type
    private fun getNetworkType(): String {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return when {
            networkCapabilities == null -> "No Network Connected"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Connected to Cellular Data"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Connected to Wi-Fi"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Connected via Bluetooth"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Connected to Ethernet"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "Connected via VPN"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> "Connected via Wi-Fi Aware"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) -> "Connected via LoWPAN"
            // Add more types if necessary
            else -> "Unknown Network Type"
        }
    }

    // Method to register the network callback
    fun registerNetworkCallback(callback: NetworkChangeCallback) {
        this.callback = callback
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    // Method to unregister the network callback
    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        callback = null
    }

    fun networkInfo(): List<Pair<String, String>> {
        val networkInfoList = ArrayList<Pair<String, String>>()
        networkInfoList.add("Device IP Address" to getAllIpAddresses())
        val (ipv4, ipv6) = getIPAddresses()
        ipv6?.let {
            networkInfoList.add("Ipv6 Address" to ipv6)
        }
        ipv4?.let {
            networkInfoList.add("IPv4 Address" to ipv4)
        }

        return networkInfoList
    }

    // Method to get all IP addresses
    private fun getAllIpAddresses(): String {
        val activeNetwork = connectivityManager.activeNetwork
        val linkProperties = connectivityManager.getLinkProperties(activeNetwork)

        Log.d(TAG, "Getting IP Addresses")

        return linkProperties?.let {
            val linkAddresses = it.linkAddresses

            if (linkAddresses.isNotEmpty()) {
                Log.d(TAG, "LinkAddresses: $linkAddresses")

                val ipAddresses = linkAddresses.map { linkAddress ->
                    linkAddress.address.hostAddress
                }

                if (ipAddresses.isNotEmpty()) {
                    ipAddresses.joinToString(", \n")
                } else {
                    DEFAULT_IP_ADDRESS
                }
            } else {
                Log.d(TAG, "No link addresses found.")
                DEFAULT_IP_ADDRESS
            }
        } ?: run {
            Log.d(TAG, "Link properties are null.")
            DEFAULT_IP_ADDRESS
        }
    }

    // Method to get IPv4 and IPv6 addresses
    private fun getIPAddresses(): Pair<String?, String?> {
        var ipv4Address: String? = null
        var ipv6Address: String? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val linkProperties = connectivityManager.getLinkProperties(network)
                linkProperties?.linkAddresses?.forEach { linkAddress ->
                    val inetAddress = linkAddress.address
                    if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress) {
                        ipv4Address = inetAddress.hostAddress
                    } else if (!inetAddress.isLoopbackAddress) {
                        ipv6Address = inetAddress.hostAddress
                    }
                }
            }
        } else {
            // Fallback for older Android versions
            @Suppress("DEPRECATION") val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifiManager.isWifiEnabled) {
                @Suppress("DEPRECATION") val wifiInfo = wifiManager.connectionInfo
                val ipAddress = wifiInfo.ipAddress
                ipv4Address = String.format(
                    Locale.getDefault(),
                    "%d.%d.%d.%d",
                    ipAddress and 0xff,
                    ipAddress shr 8 and 0xff,
                    ipAddress shr 16 and 0xff,
                    ipAddress shr 24 and 0xff
                )
            }
        }

        // If WiFi IP is not available, try to get IP from network interfaces
        if (ipv4Address == null || ipv4Address == DEFAULT_IP_ADDRESS) {
            try {
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface = networkInterfaces.nextElement()
                    val inetAddresses = networkInterface.inetAddresses
                    while (inetAddresses.hasMoreElements()) {
                        val inetAddress = inetAddresses.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            if (inetAddress is Inet4Address) {
                                ipv4Address = inetAddress.hostAddress
                            } else {
                                ipv6Address = inetAddress.hostAddress
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Pair(ipv4Address, ipv6Address)
    }

}