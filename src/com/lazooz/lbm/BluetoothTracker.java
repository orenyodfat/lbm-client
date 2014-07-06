package com.lazooz.lbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;

public class BluetoothTracker {

    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private BluetoothReceiver mBluetoothReceiver;
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
    
	public BluetoothTracker(Context context) {
		//mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
       // mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		// Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        
        mBluetoothReceiver = new BluetoothReceiver();
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        
        context.registerReceiver(mBluetoothReceiver, filter);
    }

	
	public void scan(){
		 if (mBtAdapter.isDiscovering()) {
	            mBtAdapter.cancelDiscovery();
	        }
	         // Request discover from BluetoothAdapter
	        mBtAdapter.startDiscovery();
	}
	
	
	
	class BluetoothReceiver extends BroadcastReceiver
    {
		
		
		
		
		
        public void onReceive(Context c, Intent intent)
        {
        	String action = intent.getAction();
        	if(BluetoothDevice.ACTION_FOUND.equals(action)) {
        		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        		btDeviceList.add(device);
        		Log.e("blue", "Device: " + device.getName() + ", " + device);
   	     	}
        	else if(BluetoothDevice.ACTION_UUID.equals(action)) {
        		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
   	         	Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
   	         	for (int i=0; i<uuidExtra.length; i++) {
   	         	Log.e("blue", "Device: " + device.getName() + ", " + device + ", Service: " + uuidExtra[i].toString());
   	         	}
        	}
        	
        	else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
        		Log.e("blue", "Discovery Started...");
   	        } 
        	
        	else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        		Log.e("blue", "Discovery Finished");
  
   	             Iterator<BluetoothDevice> itr = btDeviceList.iterator();
   	             while (itr.hasNext()) {
   	               // Get Services for paired devices
   	               BluetoothDevice device = itr.next();
   	               Log.e("blue", "Getting Services for " + device.getName() + ", " + device.getAddress());
   	               //if(!device.fetchUuidsWithSdp()) {
   	            	//Log.e("blue", "SDP Failed for " + device.getName());
   	               //}
   	             }
        	}
        }
    }
}
