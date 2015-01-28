package com.lazooz.lbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.lazooz.lbm.WifiTracker.wifiListener;
import com.lazooz.lbm.businessClasses.BluetoothData;
import com.lazooz.lbm.businessClasses.WifiData;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

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
    private Context mContext;
    
	public interface bluetoothListener {
		void onFinishScan(ArrayList<BluetoothData> devices);
	}
	
	private bluetoothListener mBluetoothListener;
	
	public void setBluetoothListener(bluetoothListener lsnr){
		mBluetoothListener = lsnr;
	}
	
	
	public BluetoothTracker(Context context) {
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(context));
		//mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
       // mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		// Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        
        mBluetoothReceiver = new BluetoothReceiver();
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
       // filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        
        mContext.registerReceiver(mBluetoothReceiver, filter);
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
        		mContext.unregisterReceiver(mBluetoothReceiver);
        		ArrayList<BluetoothData> devices = new ArrayList<BluetoothData>(); 
        		
        		for(BluetoothDevice device : btDeviceList){
        			BluetoothData bd = new BluetoothData();
        			bd.setAddress(device.getAddress());
        			bd.setName(device.getName());
        			devices.add(bd);
        		}
        		
        		if (mBluetoothListener != null)
        			mBluetoothListener.onFinishScan(devices);
        		
        	}
        }
    }
	
	
	public static boolean setBluetooth(boolean enable) {
	    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    boolean isEnabled = bluetoothAdapter.isEnabled();
	    if (enable && !isEnabled) {
	        return bluetoothAdapter.enable(); 
	    }
	    else if(!enable && isEnabled) {
	        return bluetoothAdapter.disable();
	    }
	    // No need to change bluetooth state
	    return true;
	}
	
	public void setBluetoothEnabled(){
		mBtAdapter.enable();
	}
	
	public boolean isBluetoothEnabled(){
		return mBtAdapter.isEnabled();
	}
	
	
	
	
}
