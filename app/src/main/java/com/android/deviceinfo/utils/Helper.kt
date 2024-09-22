package com.android.deviceinfo.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Helper {
    fun convertTime(unixTime: Long): String {
        Log.d("convertTime", "convertTime: $unixTime")
        val date = Date(unixTime)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }
}