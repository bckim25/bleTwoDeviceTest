package com.lilly.ble.ui.main

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lilly.ble.PERMISSIONS
import com.lilly.ble.PreferenceUtil
import com.lilly.ble.R
import com.lilly.ble.REQUEST_ALL_PERMISSION
import com.lilly.ble.adapter.BleListAdapter
import com.lilly.ble.databinding.ActivityMainBinding
import com.lilly.ble.viewmodel.MainViewModel
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()
    private var adapter: BleListAdapter? = null
    var device2: BluetoothDevice? = null
    var device1: BluetoothDevice? = null

    private val prefs: PreferenceUtil by lazy {
        PreferenceUtil(this)
    }
    private val TAG = "$$$" +
            ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
        binding.viewModel = viewModel

        binding.rvBleList.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvBleList.layoutManager = layoutManager


        adapter = BleListAdapter()
        binding.rvBleList.adapter = adapter
/*        if(adapter != null) {
            Log.d(TAG, "adapter ===> ${adapter!!.itemView.toString()}")

        }*/
        adapter?.setItemClickListener(object : BleListAdapter.ItemClickListener {
            override fun onClick(view: View, device: BluetoothDevice?) {
                if (device != null) {
                    viewModel.connectDevice(device)
                    device2 = device
                    prefs.saveBluetoothDevice("device",device)
                }
            }
        })

        // check if location permission
        if (!hasPermissions(this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
        }

        initObserver(binding)


        adapter?.apply {
            binding.apply {

                button1.setOnClickListener {
                    Log.d(TAG,"버튼 1 터치")
                    device1?.let { viewModel?.connectDevice(it)}
                }
                button2.setOnClickListener {
                    Log.d(TAG,"버튼 2 터치")
                    device2?.let { viewModel?.connectDevice(it) }
                }
            }
        }


    }
    private fun initObserver(binding: ActivityMainBinding){
        viewModel.requestEnableBLE.observe(this, {
            it.getContentIfNotHandled()?.let {
                requestEnableBLE()
            }
        })
        viewModel.listUpdate.observe(this, {
            it.getContentIfNotHandled()?.let { scanResults ->
                adapter?.setItem(scanResults)

                Log.d(TAG,"initObserver ===> ${scanResults.toString()}")
                if(scanResults.size == 2){
                    binding.button1.text = scanResults[0].name
                    binding.button2.text = scanResults[1].name

                    device1 = scanResults[0]
                    device2 = scanResults[1]

                }
            }
        })


        viewModel._isScanning.observe(this,{
            it.getContentIfNotHandled()?.let{ scanning->
                viewModel.isScanning.set(scanning)
            }
        })
        viewModel._isConnect.observe(this,{
            it.getContentIfNotHandled()?.let{ connect->

                Log.d(TAG,"&&& 블루투스1 상태 ${connect}")
                var msg: String = if(connect) {
                    "블루투스1  연결되었습니다. ${connect}"
                }else{
                    "블루투스1  끊어졌습니다. ${connect}"
                }

                if(connect == false) {
                    Log.d(TAG,"블루투스 1 재접속 시도")
//                    device2?.let { it1 -> viewModel.connectDevice(it1) }
                    prefs?.getBluetoothDevice("device")?.let { it1 -> viewModel.connectDevice(it1) }

                }
                Toast.makeText(this,msg,Toast.LENGTH_LONG ).show()
                viewModel.isConnect.set(connect)
            }
        })

        viewModel._isConnect2.observe(this,{
            it.getContentIfNotHandled()?.let { connect->
                Log.d(TAG,"&&& 블루투스2 상태 ${connect}")
                var msg: String = if(connect) {
                    "블루투스2  연결되었습니다. ${connect}"
                }else{
                    "블루투스2  끊어졌습니다. ${connect}"
                }
                if(connect == false) {
                    Log.d(TAG,"블루투스 2 재접속 시도")
//                    device2?.let { it1 -> viewModel.connectDevice(it1) }
                    prefs?.getBluetoothDevice("device")?.let { it1 -> viewModel.connectDevice(it1) }

                }
                Toast.makeText(this,msg,Toast.LENGTH_LONG ).show()
                viewModel.isConnect2.set(connect)
            }

        })


        viewModel.statusTxt.observe(this,{

           binding.statusText.text = it

        })

        viewModel.readTxt.observe(this,{

           binding.txtRead.append(it)
            
            if ((binding.txtRead.measuredHeight - binding.scroller.scrollY) <=
                (binding.scroller.height + binding.txtRead.lineHeight)) {
                binding.scroller.post {
                    binding.scroller.smoothScrollTo(0, binding.txtRead.bottom)
                }
            }

        })

        viewModel.readTxt2.observe(this,{
            binding.txtRead2.append(it)
            if((binding.txtRead2.measuredHeight - binding.scroller2.scrollY) <=
                (binding.scroller2.height + binding.txtRead2.lineHeight)) {
                binding.scroller2.post {
                    binding.scroller2.smoothScrollTo(0, binding.txtRead2.bottom)
                }
            }
        })

    }
    override fun onResume() {
        super.onResume()
        // finish app if the BLE is not supported
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish()
        }
    }


    private val requestEnableBleResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            // do somthing after enableBleRequest
        }
    }

    /**
     * Request BLE enable
     */
    private fun requestEnableBLE() {
        val bleEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestEnableBleResult.launch(bleEnableIntent)
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }
    // Permission check
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}