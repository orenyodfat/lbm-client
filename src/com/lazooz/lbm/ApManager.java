package com.lazooz.lbm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class ApManager {

	public ApManager() {
		// TODO Auto-generated constructor stub
	}
	//check whether wifi hotspot on or off
	public static boolean isApOn(Context context)
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

	//turn on/off wifi hotspot as toggle
	public static boolean configApState(Context context){
	    WifiManager wifimanager= (WifiManager)context.getSystemService(context.WIFI_SERVICE);
	    WifiConfiguration wificonfiguration = null;
	    
	    try 
	    {  
	        if(isApOn(context)){
	            //turn off whether wifi is on
	            wifimanager.setWifiEnabled(false);
	        }               
	        Method method=wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);                 
	        method.invoke(wifimanager, wificonfiguration, !isApOn(context));
	        return true;
	    } 
	    catch (NoSuchMethodException e) 
	    {
	        e.printStackTrace();
	    } 
	    catch (IllegalArgumentException e) 
	    {
	        e.printStackTrace();
	    } 
	    catch (IllegalAccessException e) 
	    {
	        e.printStackTrace();
	    } 
	    catch (InvocationTargetException e) 
	    {
	        e.printStackTrace();
	    }
	    return false;
	  }
}
