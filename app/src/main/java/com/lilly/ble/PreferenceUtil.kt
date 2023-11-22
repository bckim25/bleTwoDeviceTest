package com.lilly.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    fun getBoolean(key:String, defValue: Boolean) : Boolean {
        return prefs.getBoolean(key,defValue)
    }

    fun setBoolean(key: String, str: Boolean) {
        prefs.edit().putBoolean(key, str).apply()
    }


    fun saveBluetoothDevice(key: String, bluetoothDevice: BluetoothDevice?) {
        val editor = prefs.edit()
        if (bluetoothDevice != null) {
            val deviceString = bluetoothDevice.toString()
            editor.putString(key, deviceString)
        } else {
            editor.remove(key)
        }
        editor.apply()
    }

    fun getBluetoothDevice(key: String): BluetoothDevice? {
        val deviceString = prefs.getString(key, null)
        return if (deviceString != null) {
            // Reconstruct the BluetoothDevice from the string representation
            BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceString)
        } else {
            null
        }
    }

}