package com.android.deviceinfo.details

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import com.android.deviceinfo.utils.Helper

object BuildDetails {

    fun getDeviceInfoList(): List<Pair<String, String>> {
        val deviceInfoList = ArrayList<Pair<String, String>>()

        deviceInfoList.add("BOARD" to Build.BOARD)
        deviceInfoList.add("BOOTLOADER" to Build.BOOTLOADER)
        deviceInfoList.add("BRAND" to Build.BRAND)
        deviceInfoList.add("CPU_ABI (Deprecated)" to Build.CPU_ABI)
        deviceInfoList.add("CPU_ABI2 (Deprecated)" to Build.CPU_ABI2)
        deviceInfoList.add("DEVICE" to Build.DEVICE)
        deviceInfoList.add("DISPLAY" to Build.DISPLAY)
        deviceInfoList.add("FINGERPRINT" to Build.FINGERPRINT)
        deviceInfoList.add("HARDWARE" to Build.HARDWARE)
        deviceInfoList.add("HOST" to Build.HOST)
        deviceInfoList.add("ID" to Build.ID)
        deviceInfoList.add("MANUFACTURER" to Build.MANUFACTURER)
        deviceInfoList.add("MODEL" to Build.MODEL)

        // API Level 31 required for some fields
        if (SDK_INT >= Build.VERSION_CODES.S) {
            deviceInfoList.add("ODM_SKU" to Build.ODM_SKU)
            deviceInfoList.add("SKU" to Build.SKU)
            deviceInfoList.add("SOC_MANUFACTURER" to Build.SOC_MANUFACTURER)
            deviceInfoList.add("SOC_MODEL" to Build.SOC_MODEL)
        }

        deviceInfoList.add("PRODUCT" to Build.PRODUCT)
        deviceInfoList.add("RADIO (Deprecated)" to Build.RADIO)
        deviceInfoList.add("SERIAL (Deprecated)" to Build.SERIAL)
        deviceInfoList.add("SUPPORTED_32_BIT_ABIS" to Build.SUPPORTED_32_BIT_ABIS.joinToString(", "))
        deviceInfoList.add("SUPPORTED_64_BIT_ABIS" to Build.SUPPORTED_64_BIT_ABIS.joinToString(", "))
        deviceInfoList.add("SUPPORTED_ABIS" to Build.SUPPORTED_ABIS.joinToString(", "))
        deviceInfoList.add("TAGS" to Build.TAGS)
        deviceInfoList.add("TIME" to Helper.convertTime(Build.TIME))
        deviceInfoList.add("TYPE" to Build.TYPE)
        deviceInfoList.add("USER" to Build.USER)

        return deviceInfoList
    }


    fun getVersionInfoList(): List<Pair<String, String>> {
        val versionInfoList = ArrayList<Pair<String, String>>()

        versionInfoList.add("BASE_OS" to (Build.VERSION.BASE_OS ?: "Not Available"))
        versionInfoList.add("CODENAME" to Build.VERSION.CODENAME)
        versionInfoList.add("INCREMENTAL" to Build.VERSION.INCREMENTAL)
        if (SDK_INT >= Build.VERSION_CODES.S) {
            versionInfoList.add("MEDIA_PERFORMANCE_CLASS" to Build.VERSION.MEDIA_PERFORMANCE_CLASS.toString())
            versionInfoList.add("RELEASE_OR_CODENAME" to Build.VERSION.RELEASE_OR_CODENAME)
        }
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            versionInfoList.add("RELEASE_OR_PREVIEW_DISPLAY" to Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY)
        }
        versionInfoList.add("SDK (Deprecated)" to Build.VERSION.SDK)
        versionInfoList.add("PREVIEW_SDK_INT" to Build.VERSION.PREVIEW_SDK_INT.toString())
        versionInfoList.add("RELEASE" to Build.VERSION.RELEASE)
        versionInfoList.add("SDK_INT" to Build.VERSION.SDK_INT.toString())

        // API 23+ check for SECURITY_PATCH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            versionInfoList.add("SECURITY_PATCH" to Build.VERSION.SECURITY_PATCH)
        }

        if (SDK_INT >= Build.VERSION_CODES.Q) {
            val partitionName = Build.Partition.PARTITION_NAME_SYSTEM
            versionInfoList.add("Partition Name" to partitionName)
        }
        return versionInfoList
    }

}