package com.lazooz.lbm.utils;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import com.lazooz.lbm.BuildConfig;
import com.lazooz.lbm.R;
import com.lazooz.lbm.SplashActivity;
import com.lazooz.lbm.businessClasses.TelephonyData;
import com.lazooz.lbm.preference.MySharedPreferences;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.TextView;

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
			AlertDialog ad = new AlertDialog.Builder(context).create();
			ad.setTitle(title);
			ad.setMessage(message);
			ad.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {}
			});
			ad.setCanceledOnTouchOutside(false);
			ad.show();
		} catch (Exception e) {
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
		String lcl = telMgr.getNetworkCountryIso().toUpperCase();
		if (lcl.equals("")){
				Locale current = context.getResources().getConfiguration().locale;
				//lcl = current.getISO3Country();
				lcl = current.getCountry();
		}
	    return lcl;
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

	public static void sendNotifications(Context cntxt, int icon, String tickerText, 
			String notifTitle, String notifText, Intent notifIntent, boolean withSound){
		final int MY_NOTIFICATION_ID = 1;
		NotificationManager notificationManager;
		Notification myNotification;
		notificationManager =(NotificationManager)cntxt.getSystemService(Context.NOTIFICATION_SERVICE);
		myNotification = new Notification(icon, tickerText, System.currentTimeMillis());
		Context context = cntxt.getApplicationContext();
		String notificationTitle = notifTitle;
		String notificationText = notifText;
		PendingIntent pendingIntent  = PendingIntent.getActivity(cntxt, 0, notifIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		if (withSound)
			myNotification.defaults |= Notification.DEFAULT_SOUND;
		myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNotification.setLatestEventInfo(context,notificationTitle, notificationText, pendingIntent);
		notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
	}

	public static Notification createNotificationsOngoing(Context cntxt, int icon, String tickerText, 
		String notifTitle, String notifText, Intent notifIntent){

		final int MY_NOTIFICATION_ID = 2;
		NotificationManager notificationManager;
		Notification myNotification;
		notificationManager =(NotificationManager)cntxt.getSystemService(Context.NOTIFICATION_SERVICE);
		myNotification = new Notification(icon, tickerText, System.currentTimeMillis());
		Context context = cntxt.getApplicationContext();
		String notificationTitle = notifTitle;
		String notificationText = notifText;
		//Intent myIntent = new Intent();
		PendingIntent pendingIntent = null;
		if (notifIntent != null){
			pendingIntent  = PendingIntent.getActivity(cntxt, 0, notifIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		//myNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		myNotification.setLatestEventInfo(context,notificationTitle, notificationText, pendingIntent);
		return myNotification;
	}

	public static void sendNotifcationsOngoing(Context cntxt, int icon, String tickerText, 
		String notifTitle, String notifText, Intent notifIntent){
		final int MY_NOTIFICATION_ID = 2;
		NotificationManager notificationManager;
		Notification myNotification;
		notificationManager =(NotificationManager)cntxt.getSystemService(Context.NOTIFICATION_SERVICE);
		myNotification = new Notification(icon, tickerText, System.currentTimeMillis());
		Context context = cntxt.getApplicationContext();
		String notificationTitle = notifTitle;
		String notificationText = notifText;
		//Intent myIntent = new Intent();
		PendingIntent pendingIntent = null;
		if (notifIntent != null){
			pendingIntent  = PendingIntent.getActivity(cntxt, 0, notifIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		//myNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		myNotification.setLatestEventInfo(context,notificationTitle, notificationText, pendingIntent);
		notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
	}

	public static void CancleNotificationsOngoing(Context cntxt){
		final int MY_NOTIFICATION_ID = 2;
		NotificationManager notificationManager;
		notificationManager =(NotificationManager)cntxt.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(MY_NOTIFICATION_ID);
	}
	

	public static void playSound(Context cntxt, int rawVal){
		/*
		if (BuildConfig.DEBUG){
			MediaPlayer mpTada = null ;
			mpTada = MediaPlayer.create(cntxt, rawVal);
			mpTada.setVolume(1.0f, 1.0f);
			mpTada.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.release();
					}
				});
			mpTada.start();
		}*/
	}

	public static void playSound1(Context cntxt, int rawVal){
		MediaPlayer mpTada = null ;
		mpTada = MediaPlayer.create(cntxt, rawVal);
		mpTada.setVolume(1.0f, 1.0f);
		mpTada.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
		mpTada.start();
		 
	}
	
    public static void restartApp(Context context){
    	MySharedPreferences.getInstance().clearAll(context);
    	Intent mStartActivity = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(context.getApplicationContext().getPackageName());
    	int mPendingIntentId = 123456;
    	PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
    	AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
    	System.exit(0);
    }

    public static void setTitleColor(Activity activity, int color){
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView titleTV = (TextView)activity.findViewById(titleId); 
		titleTV.setTextColor(color);

    }
 
    
    public static void displayConnectionError(Activity activity, final DialogInterface.OnClickListener lsnr){
    	try {
			if(haveNetworkConnection(activity)){
			 	AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
			 	alertDialog.setTitle("No Connection");
			 	alertDialog.setMessage("Failed to connect to La'Zooz server, please try again later.");
			    alertDialog.setCanceledOnTouchOutside(false);
			    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			    	@Override
			        public void onClick(DialogInterface dialog, int which) {
			    		if (lsnr != null) 
			    			lsnr.onClick(dialog, which);
			    	}
			    });
			    
			    alertDialog.show();
			}
			else {
			 	AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
			 	alertDialog.setTitle("No Internet Connection");
			 	alertDialog.setMessage("Please connect your device to the internet and restart the application");
			    alertDialog.setCanceledOnTouchOutside(false);
			    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			    	@Override
			        public void onClick(DialogInterface dialog, int which) {
			    		if (lsnr != null)
			    			lsnr.onClick(dialog, which);
			    	}
			    });
			    
			    alertDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	public static double getMinValueFromList(List<double[]> values) {
		double min=Double.MAX_VALUE, max=Double.MIN_VALUE;
		for (double[] ds : values) {
		    for (double d : ds) {
		        if (d > max) max=d;
		        if (d < min ) min=d;
		    }
		}
		return min;
	}
	
	public static double getMaxValueFromList(List<double[]> values) {
		double min=Double.MAX_VALUE, max=Double.MIN_VALUE;
		for (double[] ds : values) {
		    for (double d : ds) {
		        if (d > max) max=d;
		        if (d < min ) min=d;
		    }
		}
		return max;
	}
	
	public static String addDays(double xValue, String initialDate) {
		//String initialDate="2014-9-28";//can take any date in current format   
		//SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd" );
		String convertedDate = null;
		Calendar cal = Calendar.getInstance();    
		try {
			cal.setTime(dateFormat.parse(initialDate));
			cal.add( Calendar.DATE, ((int)xValue));    
			
			Format formatter = new SimpleDateFormat("dd MMM yy");
			convertedDate=formatter.format(cal.getTime()); 
			System.out.println("Date increase by one.."+convertedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return convertedDate;
	}
	
 
    public static final Date fromString( String spec ) {
        try {
            return dateFormat.parse( spec );
        } catch( ParseException dfe ) {
            return invalidDate;
        }
    }
    
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final Date invalidDate = new Date(0);

	
	  public static void activateSavingLogcatToFile(Context context, boolean isAll) {    
	        String fileName = "lazooz_logcat_"+System.currentTimeMillis()+".txt";
	        
	        File sdCard = Environment.getExternalStorageDirectory() ;
	        File dir = new File (sdCard.getAbsolutePath() + "/lazooz_logs");
	        dir.mkdirs();
	        File outputFile = new File(dir, fileName);
	        
	        /*
	        
	        File cacheDir = context.getExternalCacheDir();
	        if (cacheDir == null)
	        	cacheDir = context.getCacheDir();
	        if(!cacheDir.isDirectory()) 
	            cacheDir.mkdirs();
	        
	        
	        File outputFile = new File(cacheDir,fileName);*/
	        Log.e("ZOOZ", outputFile.getAbsolutePath());
	        try {
				@SuppressWarnings("unused")
				Process process;
				if (isAll)
					process = Runtime.getRuntime().exec("logcat -v time -f "+outputFile.getAbsolutePath());
				else
					process = Runtime.getRuntime().exec("logcat -v time -f "+outputFile.getAbsolutePath() + " ZOOZ:I System.err:* AndroidRuntime:E *:F");
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

	    
	    
		public static String readLogs(){
			// Don't forget to add to the manifest: <uses-permission android:name="android.permission.READ_LOGS" />
		    String res = "";
			try {
			      Process process = Runtime.getRuntime().exec("logcat -d");
			      BufferedReader bufferedReader = new BufferedReader(
			      new InputStreamReader(process.getInputStream()));

			      StringBuilder log=new StringBuilder();
			      String line;
			      while ((line = bufferedReader.readLine()) != null) {
			        log.append(line);
			      }
			      res =  log.toString();
			    } 
		    catch (IOException e) {
			    }
			return res;
		
		}
	
}
