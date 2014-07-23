package com.lazooz.lbm;




import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.businessClasses.BluetoothData;
import com.lazooz.lbm.businessClasses.LocationData;
import com.lazooz.lbm.businessClasses.TelephonyData;
import com.lazooz.lbm.businessClasses.WifiData;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LbmService extends Service implements LocationListener{

	public static final int GPS_MIN_TIME_LOCATION_UPDATE = 30*1000; // milisec
	public static final int GPS_MIN_DISTANCE_LOCATION_UPDATE = 30; // meter
	
	
	private Timer ShortPeriodTimer;
	private Timer LongPeriodTimer;
	private LocationData mLocationData;
	//private GPSTracker mGPSTracker;
	private LocationManager mLocationManager;
	private boolean noGPSNotifSent = false;
	public boolean mSendingDataToServer;
	public LbmService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isLocationEnabled(){
		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return isGPSEnabled && isNetworkEnabled;
	}
	
	private Location getLocation(){
		Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null)
			location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		return location;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    //Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
		
		//Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE, GPS_MIN_DISTANCE_LOCATION_UPDATE, this);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE, GPS_MIN_DISTANCE_LOCATION_UPDATE, this);

		
		
		
		//mGPSTracker = GPSTracker.getInstance(this);
		//mGPSTracker.setOnLocationListener(this);

		
		
		ShortPeriodTimer = new Timer();
		TimerTask twoSecondsTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryShortPeriod();				
				}
			};
		//ShortPeriodTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 2*60*1000);
		

		LongPeriodTimer = new Timer();
		TimerTask oneMinTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryLongPeriod();				
				}
			};
		LongPeriodTimer.scheduleAtFixedRate(oneMinTimerTask, 60*1000, 2*60*1000);
		
		
		
		
		
		
		startOnDayScheduler();
		
		return Service.START_STICKY;
	}
	
		
	private void startOnDayScheduler() {
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(this, AlarmOneDaySchedReciever.class);
		PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		Random r = new Random();
		int delay = r.nextInt(1000);
		
		//alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + delay, 24*60*60*1000, pintent);
		//alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + delay, 3*60*1000, pintent);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , 24*60*60*1000, pintent);

		
	}
	
	
	

	    
	

	protected void checkEveryShortPeriod() {
		readSensors();
		
	}

	private void displayNotifGPSDialog(){
		Intent intent = new Intent(this, GPSNotifDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	protected void checkEveryLongPeriod() {
		sendDataToServerAsync();
	}
	
		
	private void sendDataToServerAsync(){
		
		LocationDataToServer locationDataToServer = new LocationDataToServer();
		locationDataToServer.execute();

	}
	
	
	
	
	private class LocationDataToServer extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(LbmService.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				JSONArray dataList = msp.getLocationDataList(LbmService.this);
				if (dataList != null){
					byte[] dataCompressed = Utils.compress(dataList.toString());
					bServerCom.setLocationZip(msp.getUserId(LbmService.this), msp.getUserSecret(LbmService.this), dataCompressed);
					jsonReturnObj = bServerCom.getReturnObject();
				}
				else
					return "";
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        	
        	String serverMessage = "";
	
			try {
				if (jsonReturnObj == null)
					serverMessage = "ConnectionError";
				else {
					serverMessage = jsonReturnObj.getString("message");
					if (serverMessage.equals("success")){
						String zoozBalance = jsonReturnObj.getString("zooz");
						String distance = jsonReturnObj.getString("distance");
						boolean isDistanceAchievement = Utils.yesNoToBoolean(jsonReturnObj.getString("is_distance_achievement"));
						boolean prevIsDistanceAchievement = MySharedPreferences.getInstance().isDistanceAchievement(LbmService.this);						

						
						MySharedPreferences.getInstance().saveDataFromServer(LbmService.this, zoozBalance, distance, isDistanceAchievement);
						if (!prevIsDistanceAchievement && isDistanceAchievement){ // achieved distance
							serverMessage = "success_distance_achieved";
						}
							
						
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				serverMessage = "GeneralError";
			}
			
			
			return serverMessage;
		}
		
		@Override
		protected void onPostExecute(String result) {
			mSendingDataToServer = false;
			
			if (result.equals("success_distance_achieved")){
				startActivity(new Intent(LbmService.this, CongratulationsDrive100Activity.class));

			}
		}
			
		
		@Override
		protected void onPreExecute() {
			mSendingDataToServer = true;
			
		}
	}
	
	
	

	private void readWifi(){
		WifiTracker wifiTracker = new WifiTracker(this);
		wifiTracker.setWifiListener(new WifiTracker.wifiListener() {
			@Override
			public void onFinishScan(ArrayList<WifiData> connections) {
				mLocationData.setWifiDataList(connections);
				mLocationData.setHasWifiData(true);
				readBT();
			}
		});
		if (wifiTracker.isWifiEnabled())
			wifiTracker.scan();
		else{
			wifiTracker.setWifiEnabled();
			Utils.wait(2000);
			if (wifiTracker.isWifiEnabled())
				wifiTracker.scan();
			else{
				mLocationData.setHasWifiData(false);
				readBT();
			}
		}
		
	}
	
	
	private void readBT(){
		BluetoothTracker bluetoothTracker = new BluetoothTracker(this);
		bluetoothTracker.setBluetoothListener(new BluetoothTracker.bluetoothListener() {
			
			@Override
			public void onFinishScan(ArrayList<BluetoothData> devices) {
				mLocationData.setBluetoothDataList(devices);
				mLocationData.setHasBluetoothData(true);
				readTelephonyData();
				readGPSData();
			}
		});

		if (bluetoothTracker.isBluetoothEnabled())
			bluetoothTracker.scan();
		else{
			bluetoothTracker.setBluetoothEnabled();
			Utils.wait(2000);
			if (bluetoothTracker.isBluetoothEnabled())
				bluetoothTracker.scan();
			else{
				mLocationData.setHasBluetoothData(false);
				readTelephonyData();
				readGPSData();
			}
		}
			
		
		
		
	}
	
	

	
	private void readTelephonyData(){
		TelephonyData td = Utils.getTelephonyData(this);
		mLocationData.setTelephonyData(td);
	}
	
	private void readSensors(){
		mLocationData = new LocationData();		
		readWifi();
	}
	
	private void readGPSData(){
		mLocationData.setTimestamp(System.currentTimeMillis());
		if (isLocationEnabled()){
			mLocationData.setHasLocationData(true);
			Location location = getLocation();
			if (location != null){
				mLocationData.setLatitude(location.getLatitude());
				mLocationData.setLongitude(location.getLongitude());
				mLocationData.setAccuracy(location.getAccuracy());
			}
			else
				mLocationData.setHasLocationData(false);
		}
		else
			mLocationData.setHasLocationData(false);
		
		MySharedPreferences.getInstance().saveLocationData(this, mLocationData);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (mSendingDataToServer)
			return;

		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (location.hasSpeed()){
			float speed = location.getSpeed();
			if (speed > 2.7){   // 2.7m/s = 10km/h
				if (isGPSEnabled) //if gps is on - read sensors 				
					readSensors();
				else if (!isGPSEnabled && isNetworkEnabled){ // if location from network and gps is off - check to display notif dialog
					if(MySharedPreferences.getInstance().shouldDisplayGPSNotif(this))
						displayNotifGPSDialog();
				}
			}
			
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.OUT_OF_SERVICE){
			
		}
		else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE){
			
		}
		else if (status == LocationProvider.AVAILABLE){
			
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)){
			
		}
		else if (provider.equals(LocationManager.NETWORK_PROVIDER)){
			
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {

		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if ((!isGPSEnabled)&&(!isNetworkEnabled)){
			if (!noGPSNotifSent){
				noGPSNotifSent = true;
				Utils.sendNotifications(this, 
									R.drawable.ic_launcher, 
									"Lazooz Notification", 
									"GPS is off", 
									"Location could not be establish.", 
									new Intent(this, MainActivity.class),
									true);
			}
		}
		
		
		if (provider.equals(LocationManager.GPS_PROVIDER)){
			
		}
		else if (provider.equals(LocationManager.NETWORK_PROVIDER)){
			
		}
		
	}
	
	
}
