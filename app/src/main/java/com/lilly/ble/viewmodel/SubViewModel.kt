package com.lilly.ble.viewmodel

import android.bluetooth.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import com.lilly.ble.*
import com.lilly.ble.util.Event
import java.util.*

class SubViewModel(private val subBleRepository: SubBleRepository) : ViewModel() {

    val statusTxt: LiveData<String>
        get() = subBleRepository.fetchStatusText().asLiveData(viewModelScope.coroutineContext)
    val readTxt: LiveData<String>
        get() = subBleRepository.fetchReadText().asLiveData(viewModelScope.coroutineContext)


    //ble adapter
    private val bleAdapter: BluetoothAdapter?
        get() = subBleRepository.bleAdapter


    val requestEnableBLE : LiveData<Event<Boolean>>
        get() = subBleRepository.requestEnableBLE
    val listUpdate : LiveData<Event<ArrayList<BluetoothDevice>?>>
        get() = subBleRepository.listUpdate

    val _isScanning: LiveData<Event<Boolean>>
        get() = subBleRepository.isScanning
    var isScanning = ObservableBoolean(false)
    val _isConnect: LiveData<Event<Boolean>>
        get() = subBleRepository.isConnect
    var isConnect = ObservableBoolean(false)

    /**
     *  Start BLE Scan
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun onClickScan(){
        subBleRepository.startScan()
    }
    fun onClickDisconnect(){
        subBleRepository.disconnectGattServer()
    }
    fun connectDevice(bluetoothDevice: BluetoothDevice){
        subBleRepository.connectDevice(bluetoothDevice)
    }

    fun onClickWrite(){

        val cmdBytes = ByteArray(2)
        cmdBytes[0] = 1
        cmdBytes[1] = 2

        subBleRepository.writeData(cmdBytes)

    }
}