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
import com.lazooz.lbm.businessClasses.StatsDataMinersDistDayList;
import com.lazooz.lbm.businessClasses.TelephonyData;
import com.lazooz.lbm.businessClasses.TelephonyDataTracker;
import com.lazooz.lbm.businessClasses.TelephonyDataTracker.OnTelephonyDataListener;
import com.lazooz.lbm.businessClasses.UserNotification;
import com.lazooz.lbm.businessClasses.UserNotificationList;
import com.lazooz.lbm.businessClasses.WifiData;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.OfflineActivities;
import com.lazooz.lbm.utils.Utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

public class LbmService extends Service implements OnTelephonyDataListener{

	public static final int GPS_MIN_TIME_LOCATION_UPDATE_HIGHT = 10*1000; // 10 sec
	public static final int GPS_MIN_TIME_LOCATION_UPDATE_LOW = 5*60*1000; // 5 min
	public static final int GPS_MIN_DISTANCE_LOCATION_UPDATE = 30; // meter
	
	public static final String FILE_TAG = "ZOOZ";
	
	private Timer ShortPeriodTimer;
	private Timer LongPeriodTimer;
	private LocationData mLocationData;
	//private GPSTracker mGPSTracker;
	private LocationManager mLocationManager;
	private boolean noGPSNotifSent = false;
	public boolean mSendingDataToServer = false;
	private NoSpeedTimer mNoSpeedTimer;
	private TelephonyDataTracker mTelephonyDataTracker;
	private boolean mIsListenToGPSProvider;
	private boolean mIsRequestLocationUpdateFirstTime = true;
	private boolean mWifiWasEnabled;
	private WifiTracker mWifiTracker;
	private NetworkLocationListener mNetworkLocationListener;
	private GPSLocationListener mGPSLocationListener;
	
	
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
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		Utils.activateSavingLogcatToFile(this, false);

		mNetworkLocationListener = new NetworkLocationListener();
		mGPSLocationListener = new GPSLocationListener();
		
		
		mIsListenToGPSProvider = false;
		
		Log.i(FILE_TAG, "SERVICE STARTED");
		
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mIsRequestLocationUpdateFirstTime = true;
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE_HIGHT, GPS_MIN_DISTANCE_LOCATION_UPDATE, mNetworkLocationListener);
		mTelephonyDataTracker = new TelephonyDataTracker(this);
		
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!isNetworkEnabled)
			mTelephonyDataTracker.requestCellUpdates(this);
		
		
		//mGPSTracker = GPSTracker.getInstance(this);
		//mGPSTracker.setOnLocationListener(this);

		
		/*
		ShortPeriodTimer = new Timer();
		TimerTask twoSecondsTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryShortPeriod();				
				}
			};*/
		//ShortPeriodTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 2*60*1000);
		

		LongPeriodTimer = new Timer();
		TimerTask oneMinTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryLongPeriod();				
				}
			};
		LongPeriodTimer.scheduleAtFixedRate(oneMinTimerTask, 60*1000, 2*60*1000);
		
		
		mNoSpeedTimer = new NoSpeedTimer(1000*60*5, 1000);
		
		
		
		startOnDayScheduler();
		
		listenToContactsChanges();
		
		myStartForeground();
		
		return Service.START_STICKY;
	}
	
		
	private void listenToContactsChanges() {
		
		getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, false, new ContactsContentObserver());
		
	}

	
	private class ContactsContentObserver extends ContentObserver {

		public ContactsContentObserver() {
			super(new Handler());
		}
		
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			sendContactsToServerAsync(LbmService.this);
		}
	}
	
	
	private void sendContactsToServerAsync(Context context){
		
		ContactsToServer contactsToServer = new ContactsToServer(context);
		contactsToServer.execute();

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
		isLiveAsync();
	}
	
		
	private void isLiveAsync(){
		IsLive isLive = new IsLive();
		isLive.execute();
	}
	
	


	private class IsLive extends AsyncTask<String, Void, String> {

		private int mNotifNum;
		private int mSrvrMinBuildNum;
		private int mSrvrCurrentBuildNum;


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(LbmService.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				bServerCom.isLive(msp.getUserId(LbmService.this), msp.getUserSecret(LbmService.this));
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
						mNotifNum = jsonReturnObj.getInt("current_notification_num");
						mSrvrMinBuildNum = jsonReturnObj.getInt("min_build_num");
						mSrvrCurrentBuildNum = jsonReturnObj.getInt("current_build_num");
						MySharedPreferences.getInstance().saveBuildNum(LbmService.this, mSrvrCurrentBuildNum, mSrvrMinBuildNum);
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
			
			if (result.equals("success")){
				int lastNotifNum = MySharedPreferences.getInstance().getLastNotificationNum(LbmService.this, mNotifNum);
				if (mNotifNum > lastNotifNum)
					getUserNotificationsAsync(lastNotifNum);
			}

		}
			
		
		@Override
		protected void onPreExecute() {
			
		}


		
	}
	
	private void getUserNotificationsAsync(int fromNum){
		GetUserNotifications getUserNotifications = new GetUserNotifications();
		getUserNotifications.execute(fromNum);
	}
	
	
	private class GetUserNotifications extends AsyncTask<Integer, Void, String> {

		private UserNotificationList mUsetNotificationList;
		private Integer mFromNum;


		@Override
		protected String doInBackground(Integer... params) {
			
          	ServerCom bServerCom = new ServerCom(LbmService.this);
        	mFromNum = params[0];
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				bServerCom.getUserNotifications(msp.getUserId(LbmService.this), msp.getUserSecret(LbmService.this), mFromNum);
				jsonReturnObj = bServerCom.getReturnObject();
				
				JSONArray userNotifArray = jsonReturnObj.getJSONArray("notifications");
				Log.e("TAG", userNotifArray.toString());
				mUsetNotificationList = new UserNotificationList(userNotifArray);
				
				
				
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
			
			if (result.equals("success")){
				int maxNum = 0;
				for (int i = 0; i < mUsetNotificationList.getNotifications().size(); i++){
					UserNotification notif = mUsetNotificationList.getNotifications().get(i);
					if(notif.getNum() > maxNum)
						maxNum = notif.getNum(); 
					if (notif.isNotif())
						notif.displayNotifBar(LbmService.this);
					if (notif.isPopup()){
						MySharedPreferences.getInstance().addNotificationToDisplayList(LbmService.this, notif);
					}
					MySharedPreferences.getInstance().setLastNotificationNum(maxNum, LbmService.this);
					
				}
			}

		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
		
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
		          	Log.i(FILE_TAG, "send location data to server");
					byte[] dataCompressed = Utils.compress(dataList.toString());
					bServerCom.setLocationZip(msp.getUserId(LbmService.this), msp.getUserSecret(LbmService.this), dataCompressed);
					jsonReturnObj = bServerCom.getReturnObject();
				}
				else
					return "";
				
			} catch (Exception e1) {
				e1.printStackTrace();
				Log.e(FILE_TAG, "error sending location data to server " + e1.getMessage());
			}
        	
        	String serverMessage = "";
	
			try {
				if (jsonReturnObj == null)
					serverMessage = "ConnectionError";
				else {
					serverMessage = jsonReturnObj.getString("message");
					if (serverMessage.equals("success")){
						Utils.playSound(LbmService.this, R.raw.server_sent);
						MySharedPreferences.getInstance().commitReadCursor(LbmService.this);
						String zoozBalance = jsonReturnObj.getString("zooz");
						String distance = jsonReturnObj.getString("distance");
						boolean isDistanceAchievement = Utils.yesNoToBoolean(jsonReturnObj.getString("is_distance_achievement"));
						boolean prevIsDistanceAchievement = MySharedPreferences.getInstance().isDistanceAchievement(LbmService.this);						

						
						MySharedPreferences.getInstance().saveDataFromServer2(LbmService.this, zoozBalance, distance, isDistanceAchievement);
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
			/*
			if (result.equals("success_distance_achieved")){
				Utils.sendNotifications(LbmService.this, 
						R.drawable.ic_launcher, 
						"La'Zooz Notification", 
						"You have achieved 100 km", 
						"You have achieved 100 km", 
						new Intent(LbmService.this, MainActivity.class),
						true);
				
				//startActivity(new Intent(LbmService.this, CongratulationsDrive100Activity.class));

			}*/
		}
			
		
		@Override
		protected void onPreExecute() {
			mSendingDataToServer = true;
			
		}
	}
	
	
	

	private void readWifi(){
		mWifiTracker = new WifiTracker(this);
		mWifiTracker.setWifiListener(new WifiTracker.wifiListener() {
			@Override
			public void onFinishScan(ArrayList<WifiData> connections) {
				mLocationData.setWifiDataList(connections);
				mLocationData.setHasWifiData(true);
				if (!mWifiWasEnabled)
					mWifiTracker.setWifiDisabled();
				readTelephonyData();
				readGPSData();
			}
		});
		if (mWifiTracker.isWifiEnabled()){
			mWifiWasEnabled = true;
			mWifiTracker.scan();
		}
		else{
			mWifiWasEnabled = false;
			mWifiTracker.setWifiEnabled();
			Utils.wait(2000);
			if (mWifiTracker.isWifiEnabled())
				mWifiTracker.scan();
			else{
				mLocationData.setHasWifiData(false);
				readTelephonyData();
				readGPSData();
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
		Log.i(FILE_TAG, "read Sensors");
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
				mLocationData.setRoute(MySharedPreferences.getInstance().getRoute(this));
			}
			else
				mLocationData.setHasLocationData(false);
		}
		else
			mLocationData.setHasLocationData(false);
		
		MySharedPreferences.getInstance().saveLocationData(this, mLocationData);
		Log.i(FILE_TAG, "save location data locally");
		//Utils.playSound(this, R.raw.save);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//******************************************************************************************************
	//LocationListener
	//******************************************************************************************************
	/*
	
	@Override
	public void onLocationChanged(Location location) {
		if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){ // ignore first shoot after request, there was no real location change
			if (mIsRequestLocationUpdateFirstTime){
				Log.i(FILE_TAG, "onLocationChanged network first time");
				mIsRequestLocationUpdateFirstTime = false;
				return;
			}
			else
				Log.i(FILE_TAG, "onLocationChanged network");
		}
		
		Utils.playSound(this, R.raw.gps);
		if (mSendingDataToServer){
			Log.i(FILE_TAG, "onLocationChanged during sending data to server");
			return;
		}

		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (!mIsListenToGPSProvider){
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE_HIGHT, GPS_MIN_DISTANCE_LOCATION_UPDATE, mNetworkLocationListener);
			mIsListenToGPSProvider = true;
			Log.i(FILE_TAG, "turn on GPS_PROVIDER");
			MySharedPreferences.getInstance().promoteRoute(this);
			mNoSpeedTimer.startNow();
		}

		if (location.hasSpeed()){
			Log.i(FILE_TAG, "location hasSpeed");
			float speed = location.getSpeed();
			if ((speed > 2.7)||(mNoSpeedTimer.isActive())){   // 2.7m/s = 10km/h
			
				if (speed > 2.7){
					Log.i(FILE_TAG, "Speed Over 10 kms");
					Utils.playSound(this, R.raw.ten_kms);
					mNoSpeedTimer.startNow();
				}
				
				//Utils.playSound(this, R.raw.ten_kms);
				if (isGPSEnabled && location.hasAccuracy() && (location.getAccuracy()<= 25)){ //if gps is on - read sensors 				
					readSensors();
				}
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
			Utils.playSound(LbmService.this, R.raw.status_no_service);
			Log.i(FILE_TAG, "onStatusChanged - " + provider + ", OUT_OF_SERVICE");
			if (provider.equals(LocationManager.NETWORK_PROVIDER)){
				if (!mIsListenToGPSProvider)
					mTelephonyDataTracker.requestCellUpdates(this);
			}
		}
		else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE){
			Utils.playSound(LbmService.this, R.raw.status_no_service_temp);
			Log.i(FILE_TAG, "onStatusChanged - " + provider + ", TEMPORARILY_UNAVAILABLE");
			if (provider.equals(LocationManager.NETWORK_PROVIDER)){
				if (!mIsListenToGPSProvider)
					mTelephonyDataTracker.requestCellUpdates(this);
			}
		}
		else if (status == LocationProvider.AVAILABLE){
			Utils.playSound(LbmService.this, R.raw.status_avail);
			Log.i(FILE_TAG, "onStatusChanged - " + provider + ", AVAILABLE");
			if (provider.equals(LocationManager.NETWORK_PROVIDER))
				mTelephonyDataTracker.removeUpdates();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(FILE_TAG, "onProviderEnabled - " + provider);
		if (provider.equals(LocationManager.GPS_PROVIDER)){
			
		}
		else if (provider.equals(LocationManager.NETWORK_PROVIDER)){
			Utils.playSound(LbmService.this, R.raw.enable_provider_net);
			mTelephonyDataTracker.removeUpdates();
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(FILE_TAG, "onProviderDisabled - " + provider);
		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if ((!isGPSEnabled)&&(!isNetworkEnabled)){
			if (!noGPSNotifSent){
				noGPSNotifSent = true;
				Utils.sendNotifications(this, 
									R.drawable.ic_launcher, 
									"La'Zooz Notification", 
									"GPS is off", 
									"Location could not be establish.", 
									new Intent(this, MainActivity.class),
									true);
			}
		}
		
		
		if (provider.equals(LocationManager.GPS_PROVIDER)){
			
		}
		else if (provider.equals(LocationManager.NETWORK_PROVIDER)){
			Utils.playSound(LbmService.this, R.raw.disable_provider_net);
			mTelephonyDataTracker.requestCellUpdates(this);
		}
		
	}
*/
	
	
	public class NoSpeedTimer extends CountDownTimer {
		private boolean mIsActive = false;
		public NoSpeedTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

		public void startNow(){
			mIsActive = true;
			start();
		}
		
		public boolean isActive(){
			return mIsActive;
		}
		
		
       @Override
        public void onFinish() {
    	   Log.i(FILE_TAG, "onFinish GPS Times");
    	   Utils.playSound(LbmService.this, R.raw.timer_end);
    	   mIsActive = false;
    	   mLocationManager.removeUpdates(mGPSLocationListener);
    	   mIsRequestLocationUpdateFirstTime = true;
			mIsListenToGPSProvider = false;
			
			boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isNetworkEnabled)
				mTelephonyDataTracker.requestCellUpdates(LbmService.this);
			
       }

        @Override
        public void onTick(long millisUntilFinished) {
        }
        
        
       
	}




	@Override
	public void onCellChanged(int newCellID) {
		Log.i(FILE_TAG, "onCellChanged");
		Utils.playSound(this, R.raw.cell_change);
		mNoSpeedTimer.startNow();
		if (!mIsListenToGPSProvider){
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE_HIGHT, GPS_MIN_DISTANCE_LOCATION_UPDATE, mGPSLocationListener);
			mIsListenToGPSProvider = true;
			mTelephonyDataTracker.removeUpdates();
			MySharedPreferences.getInstance().promoteRoute(this);
		}		
	}

	
    private void myStartForeground(){
   	 Intent intent1 = new Intent(this, MainActivity.class);
 	    intent1.setAction(Intent.ACTION_VIEW);
 	    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 	    intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

 	    Notification notif = Utils.createNotificationsOngoing(getApplicationContext(), 
 	    		R.drawable.ic_stat_notif_logo, 
 	    		getString(R.string.forground_notif_ticker_text), 
 	    		getString(R.string.forground_notif_title), 
 	    		getString(R.string.forground_notif_text), 
 	    		intent1); 
 	    
 	   startForeground(1256, notif);
    	
   }
   private void myStopForeground(){
   	stopForeground(true);
  }	

   
/***********************************************************************************************************************************************/	
/******************************************       NetworkLocationListener          *************************************************************/
/***********************************************************************************************************************************************/	
   
   private class NetworkLocationListener implements LocationListener{

	@Override
	public void onLocationChanged(Location location) {
		
		if (mIsRequestLocationUpdateFirstTime){
			Log.i(FILE_TAG, "onLocationChanged network first time");
			mIsRequestLocationUpdateFirstTime = false;
			return;
		}
		else
			Log.i(FILE_TAG, "onLocationChanged network");
		
		Utils.playSound(LbmService.this, R.raw.gps);

		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (!mIsListenToGPSProvider){
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE_HIGHT, GPS_MIN_DISTANCE_LOCATION_UPDATE, mGPSLocationListener);
			mIsListenToGPSProvider = true;
			Log.i(FILE_TAG, "turn on GPS_PROVIDER");
			MySharedPreferences.getInstance().promoteRoute(LbmService.this);
			mNoSpeedTimer.startNow();
		}

		if (!isGPSEnabled && isNetworkEnabled){ // if location from network and gps is off - check to display notif dialog
			if(MySharedPreferences.getInstance().shouldDisplayGPSNotif(LbmService.this))
				displayNotifGPSDialog();
		}
		
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(FILE_TAG, "onProviderDisabled - " + provider);
		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if ((!isGPSEnabled)&&(!isNetworkEnabled)){
			if (!noGPSNotifSent){
				noGPSNotifSent = true;
				Utils.sendNotifications(LbmService.this, 
									R.drawable.ic_launcher, 
									"La'Zooz Notification", 
									"GPS is off", 
									"Location could not be establish.", 
									new Intent(LbmService.this, MainActivity.class),
									true);
			}
		}
		
		
		Utils.playSound(LbmService.this, R.raw.disable_provider_net);
		mTelephonyDataTracker.requestCellUpdates(LbmService.this);

		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(FILE_TAG, "onProviderEnabled - " + provider);
		Utils.playSound(LbmService.this, R.raw.enable_provider_net);
		mTelephonyDataTracker.removeUpdates();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.OUT_OF_SERVICE){
			Utils.playSound(LbmService.this, R.raw.status_no_service);
			Log.i(FILE_TAG, "onStatusChanged - " + provider + ", OUT_OF_SERVICE");
			if (!mIsListenToGPSProvider)
				mTelephonyDataTracker.requestCellUpdates(LbmService.this);
		}
		else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE){
			Utils.playSound(LbmService.this, R.raw.status_no_service_temp);
			Log.i(FILE_TAG, "onStatusChanged - " + provider + ", TEMPORARILY_UNAVAILABLE");
			if (!mIsListenToGPSProvider)
				mTelephonyDataTracker.requestCellUpdates(LbmService.this);
		}
		else if (status == LocationProvider.AVAILABLE){
			Utils.playSound(LbmService.this, R.raw.status_avail);
			Log.i(FILE_TAG, "onStatusChanged - " + provider + ", AVAILABLE");
			mTelephonyDataTracker.removeUpdates();
		}
	}
		
	   
   }

   /***********************************************************************************************************************************************/	
   /******************************************       GPSLocationListener              *************************************************************/
   /***********************************************************************************************************************************************/	
   
   
   
   
   private class GPSLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			
			
			Utils.playSound(LbmService.this, R.raw.gps);
			if (mSendingDataToServer){
				Log.i(FILE_TAG, "onLocationChanged during sending data to server");
				return;
			}

			boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			

			if (location.hasSpeed()){
				Log.i(FILE_TAG, "location hasSpeed");
				float speed = location.getSpeed();
				if ((speed > 2.7)||(mNoSpeedTimer.isActive())){   // 2.7m/s = 10km/h  enter if over 10km/s or the 5 min timer is active
				
					if (speed > 2.7){  // over 10 km/s start-over the timer
						Log.i(FILE_TAG, "Speed Over 10 kms");
						Utils.playSound(LbmService.this, R.raw.ten_kms);
						mNoSpeedTimer.startNow();
					}
					
					//Utils.playSound(this, R.raw.ten_kms);
					if (isGPSEnabled && location.hasAccuracy() && (location.getAccuracy()<= 25)){ //if gps is on - read sensors 				
						readSensors();
					}
				}
				
			}
			
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(FILE_TAG, "onProviderDisabled - " + provider);
			boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if ((!isGPSEnabled)&&(!isNetworkEnabled)){
				if (!noGPSNotifSent){
					noGPSNotifSent = true;
					Utils.sendNotifications(LbmService.this, 
										R.drawable.ic_launcher, 
										"La'Zooz Notification", 
										"GPS is off", 
										"Location could not be establish.", 
										new Intent(LbmService.this, MainActivity.class),
										true);
				}
			}
			
			

			
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i(FILE_TAG, "onProviderEnabled - " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (status == LocationProvider.OUT_OF_SERVICE){
				Utils.playSound(LbmService.this, R.raw.status_no_service);
				Log.i(FILE_TAG, "onStatusChanged - " + provider + ", OUT_OF_SERVICE");
			}
			else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE){
				Utils.playSound(LbmService.this, R.raw.status_no_service_temp);
				Log.i(FILE_TAG, "onStatusChanged - " + provider + ", TEMPORARILY_UNAVAILABLE");
			}
			else if (status == LocationProvider.AVAILABLE){
				Utils.playSound(LbmService.this, R.raw.status_avail);
				Log.i(FILE_TAG, "onStatusChanged - " + provider + ", AVAILABLE");
			}
			
		}
   }
		   
  
}
