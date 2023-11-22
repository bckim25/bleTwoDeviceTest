package com.lilly.ble.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lilly.ble.SubBleRepository
import com.lilly.ble.util.Event
import java.util.ArrayList

class SubViewModel(private val subBleRepository: SubBleRepository) : ViewModel() {

    val statusTxt: LiveData<String>
        get() = subBleRepository.fetchStatusText().asLiveData(viewModelScope.coroutineContext)
    val readTxt: LiveData<String>
        get() = subBleRepository.fetchReadText().asLiveData(viewModelScope.coroutineContext)


    val readTxt2: LiveData<String>
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

    val _isConnect2: LiveData<Event<Boolean>>
        get() = subBleRepository.isConnect
    var isConnect2 = ObservableBoolean(false)


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

    fun setTest() {
        Log.d("%%%","터치 테스트")
    }





    fun onClickWrite(){

        val cmdBytes = ByteArray(2)
        cmdBytes[0] = 1
        cmdBytes[1] = 2

        subBleRepository.writeData(cmdBytes)

    }
}