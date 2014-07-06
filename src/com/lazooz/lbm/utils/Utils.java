package com.lazooz.lbm.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class Utils {



    public static int getVersionCode(Context context){
    	PackageManager manager = context.getPackageManager();
    	PackageInfo info = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
	    	return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
	    	return -1;
		}
    }
	
	
	public static String getNowTimeInGMT(){
		String outputText = "";
		
		try {


			
			DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date now = new Date();
			outputText = outputFormat.format(now);

		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 
		return outputText;
	}
    
    
	public static String getTimeInGMT(Date theDate){
		String outputText = "";	
		try {
			DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			outputText = outputFormat.format(theDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputText;
	}
	
	
	
	public static int getCellId(Context context){
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
	       
	    String networkOperator = telephonyManager.getNetworkOperator();
	    String mcc = networkOperator.substring(0, 3);
	    String mnc = networkOperator.substring(3);
	       
	    int cid = cellLocation.getCid();
	    int lac = cellLocation.getLac();
	    
	    return cid;
	}
	
	
}
