package com.lazooz.lbm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiTracker {

	private WifiManager mWifiManager;
	private WifiReceiver mWifiReceiver;
    StringBuilder sb = new StringBuilder();

	public WifiTracker(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		if(!mWifiManager.isWifiEnabled())
			  mWifiManager.setWifiEnabled(true);
		
		
		
		mWifiReceiver = new WifiReceiver();
        context.registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        
        
	}

	
	public void scan(){
		mWifiManager.startScan();
	}
	
	
	
	class WifiReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {

            ArrayList<String> connections=new ArrayList<String>();
            ArrayList<Float> Signal_Strenth= new ArrayList<Float>();

            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = mWifiManager.getScanResults();
            for(int i = 0; i < wifiList.size(); i++)
            {

                connections.add(wifiList.get(i).SSID);
            }


        }
    }
}
