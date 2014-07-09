package com.lazooz.lbm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.lazooz.lbm.businessClasses.WifiData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class WifiTracker {


	
	private WifiManager mWifiManager;
	private WifiReceiver mWifiReceiver;
	private Context mContext;
	
	public interface wifiListener {
		void onFinishScan(ArrayList<WifiData> connections);
	}
	
	private wifiListener mWifiListener;
	
	public void setWifiListener(wifiListener lsnr){
		mWifiListener = lsnr;
	}
	
    //StringBuilder sb = new StringBuilder();

	public WifiTracker(Context context) {
		mContext = context;
		
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

		//if(!mWifiManager.isWifiEnabled())
			 // mWifiManager.setWifiEnabled(true);
		
		
		
		
        
        
	}

	public void setWifiEnabled(){
		mWifiManager.setWifiEnabled(true);
	}
	
	public boolean isWifiEnabled(){
		return mWifiManager.isWifiEnabled();
	}
	
	public void scan(){
		mWifiReceiver = new WifiReceiver();
		mContext.registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		mWifiManager.startScan();
	}
	
	
	
	class WifiReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
        	
        	mContext.unregisterReceiver(mWifiReceiver);
        	//intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            ArrayList<WifiData> connections = new ArrayList<WifiData>(); 
            
           
            List<ScanResult> wifiList;
            wifiList = mWifiManager.getScanResults();
            for(int i = 0; i < wifiList.size(); i++)
            {
            	WifiData wd = new WifiData();
            	wd.setSSID(wifiList.get(i).SSID);
            	wd.setBSSID(wifiList.get(i).BSSID);
            	wd.setCapabilities(wifiList.get(i).capabilities);
            	wd.setFrequency(wifiList.get(i).frequency);

                connections.add(wd);
            }
            
            if (mWifiListener != null)
            	mWifiListener.onFinishScan(connections);

        }
    }
	
	
	
	public static boolean isWifiApEnabled(Context context)
	{
	    WifiManager wifimanager= (WifiManager)context.getSystemService(context.WIFI_SERVICE);       
	    try
	    {
	        Method method =wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
	        method.setAccessible(true);
	        return (Boolean) method.invoke(wifimanager);
	    }
	    catch (Throwable ignored)
	    {
	    }
	    return false;
	}
	
	public static boolean setWifiApEnabled(Context context){
	    WifiManager wifimanager= (WifiManager)context.getSystemService(context.WIFI_SERVICE);
	    WifiConfiguration wificonfiguration = null;
	    
	    try 
	    {  
	        Method method=wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);                 
	        method.invoke(wifimanager, wificonfiguration, true);
	        return true;
	    } 
	    catch (Exception e) 
	    {
	        e.printStackTrace();
	    } 
	    return false;
	  }
	
	
}
