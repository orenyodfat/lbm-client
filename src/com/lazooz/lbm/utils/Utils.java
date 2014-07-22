package com.lazooz.lbm.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import com.lazooz.lbm.R;
import com.lazooz.lbm.businessClasses.TelephonyData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class Utils {



   
	
	
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
	public static String md5ThePHPWay(byte[] s) throws NoSuchAlgorithmException {
	    String result = "";    
		MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
	        md.update(s);
	        BigInteger hash = new BigInteger(1, md.digest());
	        result = hash.toString(16);
	        while(result.length() < 32) { //40 for SHA-1
	            result = "0" + result;
	        }
	        
	        return result;
	}
	
	public static byte[] compress(String string) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
	    GZIPOutputStream gos = new GZIPOutputStream(os);
	    gos.write(string.getBytes("UTF-8"));
	    gos.close();
	    byte[] compressed = os.toByteArray();
	    os.close();
	    return compressed;
	}
	
	
	public static String getMyPhoneNum(Context context){
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String myNum = tMgr.getLine1Number();
//		if (myNum.equals(""))
//			myNum = tMgr.getSubscriberId();
		return myNum;
	}
	public static boolean haveNetworkConnection(Context context) {

	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean connected = networkInfo != null && networkInfo.isAvailable() &&
                networkInfo.isConnected();
        return connected;
        
   
	}

	/**
     * Uses static final constants to detect if the device's platform version is Gingerbread or
     * later.
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb or
     * later.
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or
     * later.
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Uses static final constants to detect if the device's platform version is ICS or
     * later.
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    
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
   
    public static String getVersionName(Context context){
    	PackageManager manager = context.getPackageManager();
    	PackageInfo info = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
	    	return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
	    	return "";
		}
    }

    
	public static void freezOrientation(Activity activity){
		if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			//activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		else 
			//activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
	}
	public static void unFreezOrientation(Activity activity){
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	
	
	
	public static void showSettingsAlert(final Context context, String theMessage){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
   	 
        // Setting Dialog Title
        alertDialog.setTitle(context.getString(R.string.gps_activate_gps_title));
 
        // Setting Dialog Message
        alertDialog.setMessage(theMessage);
 
        // On pressing Settings button
        alertDialog.setPositiveButton(context.getString(R.string.gps_activate_gps_setting), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	context.startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

	
	
	
}
