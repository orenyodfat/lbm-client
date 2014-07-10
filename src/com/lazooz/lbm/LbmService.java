package com.lazooz.lbm;



import java.util.ArrayList;
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

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

public class LbmService extends Service implements LocationListener{

	private Timer ShortPeriodTimer;
	private Timer LongPeriodTimer;
	private LocationData mLocationData;
	private GPSTracker mGPSTracker;
	
	public LbmService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    //Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
		
		//Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		mGPSTracker = GPSTracker.getInstance(this);
		mGPSTracker.setOnLocationListener(this);

		
		
		ShortPeriodTimer = new Timer();
		TimerTask twoSecondsTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryShortPeriod();				
				}
			};
		ShortPeriodTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 20*1000);
		

		LongPeriodTimer = new Timer();
		TimerTask oneMinTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryLongPeriod();				
				}
			};
		LongPeriodTimer.scheduleAtFixedRate(oneMinTimerTask, 60*1000, 2*60*1000);
		
		
		
		
		
		
		return Service.START_STICKY;
	}

	protected void checkEveryShortPeriod() {
		readSensors();
		
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
				
				bServerCom.setLocation(msp.getUserId(LbmService.this), msp.getUserSecret(LbmService.this), dataList.toString());
				jsonReturnObj = bServerCom.getReturnObject();
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
			
			if (result.equals("success_distance_achieved")){
				//start congrats activity
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			
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
		
		if (mGPSTracker.isGPSEnabled()){
			mLocationData.setHasLocationData(true);
			mLocationData.setLatitude(mGPSTracker.getLatitude());
			mLocationData.setLongitude(mGPSTracker.getLongitude());
			mLocationData.setAccuracy(mGPSTracker.getAccuracy());
		}
		else
			mLocationData.setHasLocationData(false);
		
		MySharedPreferences.getInstance().saveLocationData(this, mLocationData);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	
}
