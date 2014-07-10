package com.lazooz.lbm.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.lazooz.lbm.businessClasses.TelephonyData;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
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
	
	
	
	public static TelephonyData getTelephonyData(Context context){

		TelephonyData td = new TelephonyData();
	    
		try {
			TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
			
			
			
			String networkOperator = telephonyManager.getNetworkOperator();
			if (!networkOperator.equals("")){
				td.setMMC(networkOperator.substring(0, 3)); //mobile country code
				td.setMNC(networkOperator.substring(3)); //mobile network code 
			}
			   
			if (cellLocation != null){
				td.setCID(cellLocation.getCid());//gsm cell id
				td.setLAC(cellLocation.getLac());// gsm location area code
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return td;
	}
	
	public static void messageToUser(Context context, String title, String message){
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(title);
			builder.setMessage(message);
			builder.setPositiveButton(context.getString(android.R.string.ok), null);
			builder.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String booleanToYesNo(boolean value){
		return (value) ? "yes" : "no";
	}
	
	public static boolean yesNoToBoolean(String value){
		return value.equalsIgnoreCase("yes");		
	}
	
	public static String getCurrentLocale(Context context){
		TelephonyManager    telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    return telMgr.getNetworkCountryIso().toUpperCase();
	}
	
	public static void wait(int milisec){
		try {
			Thread.sleep(milisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void beep(){
		  final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
		  tg.startTone(ToneGenerator.TONE_PROP_BEEP);
	}

}
