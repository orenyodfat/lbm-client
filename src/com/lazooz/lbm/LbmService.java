package com.lazooz.lbm;




import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.internal.mf;
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
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
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
	//public static final int GPS_MIN_TIME_LOCATION_UPDATE_LOW = 5*60*1000; // 5 min
	public static final int GPS_MIN_DISTANCE_LOCATION_UPDATE = 30; // meter
	
	public static final String FILE_TAG = "ZOOZ";
	
	private Timer ShortPeriodTimer;
	private Timer LongPeriodTimer;
	private LocationData mLocationData;
	private LocationManager mLocationManager;
	private boolean noGPSNotifSent = false;
	public int mSendingDataToServer = 0;
	private NoSpeedTimer mNoSpeedTimer;
	private TelephonyDataTracker mTelephonyDataTracker;
	private boolean mIsListenToGPSProvider;
	private boolean mIsListenToGPSProviderFromCellChange = false;
	private boolean mSoundStart = false;
	private boolean mIsListenToGPSProviderFromNetChange = false;
	private boolean mIsRequestLocationUpdateFirstTime = true;
	private boolean mWifiWasEnabled;
	private WifiTracker mWifiTracker;
	private NetworkLocationListener mNetworkLocationListener;
	private GPSLocationListener mGPSLocationListener;
	private boolean mReadingSensorsNow = false;
	private long mLastReadSensorsTime = 0;
	private boolean mWifiWasSetOnByMe = false;
	private boolean mWifiWasInit = false;
	private String mPotentialZoozBalance = "0.0";
	private String mPrevPotentialZoozBalance = "0.0";
	private Runnable runnable;
	
	private Handler handler; 
	
	
	public LbmService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isLocationEnabled(){
		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		//boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return isGPSEnabled;// && isNetworkEnabled;
	}
	
	private Location getLocation(){
		Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		if (location == null)
//			location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		return location;
	}
	
	@Override
	public void onDestroy()
	{
		//Utils.playSound1(LbmService.this, R.raw.status_no_service);
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    //Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		//Utils.activateSavingLogcatToFile(this, false);

		mNetworkLocationListener = new NetworkLocationListener();
		mGPSLocationListener = new GPSLocationListener();
		
		//Utils.playSound1(LbmService.this, R.raw.status_avail);
		mIsListenToGPSProvider = false;
		
		Log.i(FILE_TAG, "SERVICE STARTED");
		
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mIsRequestLocationUpdateFirstTime = true;
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE_HIGHT, GPS_MIN_DISTANCE_LOCATION_UPDATE, mNetworkLocationListener);
		mTelephonyDataTracker = new TelephonyDataTracker(this);
		
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!isNetworkEnabled)
			mTelephonyDataTracker.requestCellUpdates(this);
		
		

		
		/*
		ShortPeriodTimer = new Timer();
		TimerTask twoSecondsTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryShortPeriod();				
				}
			};*/
		//ShortPeriodTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 2*60*1000);
		
       /*
		LongPeriodTimer = new Timer();
		TimerTask oneMinTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryLongPeriod();				
				}
			};
		LongPeriodTimer.scheduleAtFixedRate(oneMinTimerTask, 60*1000, 2*60*1000);
		*/
		runnable = new Runnable() {
			   @Override
			   public void run() {
			      /* do what you need to do */
				  checkEveryLongPeriod();
			      /* and here comes the "trick" */
			      handler.postDelayed(this, 1000*60);
			   }
			};
			
		handler = new Handler();
		handler.postDelayed(runnable, 1000*60);
		
		
		
		
		
		startOnDayScheduler();
		
		listenToContactsChanges();
		
		
		
		
		 
		    
		
		
		//myStartForeground();
		
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
	
	
	

	    
	/*

	protected void checkEveryShortPeriod() {
		readSensors();
		
	}
	*/

	private void displayNotifGPSDialog(){
		Intent intent = new Intent(this, GPSNotifDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	protected void checkEveryLongPeriod() {
		
	//	MySharedPreferences msp = MySharedPreferences.getInstance();
		
	//	Utils.playSound1(LbmService.this, R.raw.drop_coin_10);
		
		sendDataToServerAsync();
		isLiveAsync();
		try {
			if ((Integer.parseInt(mPotentialZoozBalance)-Integer.parseInt(mPrevPotentialZoozBalance))>=1)
			{
				 Utils.playSound1(LbmService.this, R.raw.drop_coin_10);
				 mPrevPotentialZoozBalance = mPotentialZoozBalance;
			}
		} catch (Exception e) {
		}
		
	}
	
		
	private void isLiveAsync(){
		String jString = "";
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (isNetworkEnabled){
			Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			JSONObject jObj = new JSONObject();
			try {
				jObj.put("lat", location.getLatitude());
				jObj.put("long", location.getLongitude());

				if (location.hasAccuracy())
					jObj.put("location_accuracy", location.getAccuracy());
				
				if (location.hasSpeed())
					jObj.put("location_speed", location.getSpeed());		
				
				jString = jObj.toString();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
		}

		IsLive isLive = new IsLive();
		isLive.execute(jString);
	}
	
	


	private class IsLive extends AsyncTask<String, Void, String> {

		private int mNotifNum;
		private int mSrvrMinBuildNum;
		private int mSrvrCurrentBuildNum;


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(LbmService.this);
          	String locationString = params[0];
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				String publicKey = MySharedPreferences.getInstance().getPublicKey(LbmService.this); 
				bServerCom.isLive(msp.getUserId(LbmService.this), msp.getUserSecret(LbmService.this), locationString, publicKey);
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
				{
					mSendingDataToServer = 0;
					return "";
				}
				
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
					//	//Utils.playSound(LbmService.this, R.raw.server_sent);
						MySharedPreferences.getInstance().commitReadCursor(LbmService.this);
						String zoozBalance = jsonReturnObj.getString("zooz");
						String potentialZoozBalance = jsonReturnObj.getString("potential_zooz_balance");
						String distance = jsonReturnObj.getString("distance");
						boolean isDistanceAchievement = Utils.yesNoToBoolean(jsonReturnObj.getString("is_distance_achievement"));
						boolean prevIsDistanceAchievement = MySharedPreferences.getInstance().isDistanceAchievement(LbmService.this);						

						mPotentialZoozBalance = potentialZoozBalance;
						MySharedPreferences.getInstance().saveDataFromServerService(LbmService.this, zoozBalance, potentialZoozBalance, distance, isDistanceAchievement);
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
			
			mSendingDataToServer = 0;
			return serverMessage;
		}
		
		@Override
		protected void onPostExecute(String result) {
			mSendingDataToServer = 0;
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
			mSendingDataToServer = 1;
			
		}
	}
	
	
	private void initWifi(){
		
		mWifiWasInit  = true;
		mWifiWasSetOnByMe = false;
		mWifiWasEnabled = false;

		mWifiTracker = new WifiTracker(this);
		
		mWifiTracker.setWifiListener(new WifiTracker.wifiListener() {
			@Override
			public void onFinishScan(ArrayList<WifiData> connections) {
				mLocationData.setWifiDataList(connections);
				mLocationData.setHasWifiData(true);
				readTelephonyData();
				SaveLocationData();
			}
		});
		
		if (mWifiTracker.isWifiEnabled()){
			mWifiWasEnabled = true;
			return;
		}
		else{
			mWifiWasEnabled = false;
			mWifiTracker.setWifiEnabled();
			/*
			for(int i = 0; i<10; i++){ // loop up to 2 sec
				Utils.wait(200);
				if (mWifiTracker.isWifiEnabled())
					break;
			}
			*/
			
			if (mWifiTracker.isWifiEnabled()){
				mWifiWasSetOnByMe = true;
			}
		}
		
		
	}

	private void readWifi(){
		//Utils.playSound(this, R.raw.read_wf);
		
		if (mWifiTracker.isWifiEnabled()){
			if (!mWifiTracker.scan()){ // scan failed, the onFinishScan will not be called
				mLocationData.setHasWifiData(false);
				readTelephonyData();
				SaveLocationData();
			}
		}
		else{
			mLocationData.setHasWifiData(false);
			readTelephonyData();
			SaveLocationData();
		}
	}
	
	
	/*private void readBT(){
		//Utils.playSound(this, R.raw.read_bt);
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
			
		
		
		
	}*/
	
	

	
	private void readTelephonyData(){
		TelephonyData td = Utils.getTelephonyData(this);
		mLocationData.setTelephonyData(td);
	}
	
	private void readSensors(Location location){
		if (mReadingSensorsNow){
			Log.i(FILE_TAG, "read Sensors request while not finish reading prev");
			return;
		}
		
		//mReadingSensorsNow = true;
		long currentTime = System.nanoTime();
		
		if ((mLastReadSensorsTime > 0) && (currentTime - mLastReadSensorsTime < 9*1000*1000)){
			
			Log.i(FILE_TAG, "read Sensors less then 9 sec apart, mLastReadSensorsTime:" + 
					Utils.getDateTimeFromMilli(mLastReadSensorsTime) + 
					" currentTime: "+ 
					Utils.getDateTimeFromMilli(currentTime));
			// Utils.playSound1(LbmService.this,R.raw.no_accuracy);
			
			return;
		}
		
		mReadingSensorsNow = true;
		mLastReadSensorsTime = currentTime;
		
		
		Log.i(FILE_TAG, "read Sensors");
		//Utils.playSound(this, R.raw.read_sensors);
		mLocationData = new LocationData();
		
		mLocationData.setTimestamp(System.currentTimeMillis());
		if (location != null){
			mLocationData.setHasLocationData(true);
			mLocationData.setLatitude(location.getLatitude());
			mLocationData.setLongitude(location.getLongitude());
			if(location.hasAccuracy())
				mLocationData.setAccuracy(location.getAccuracy());
			if(location.hasSpeed())
				mLocationData.setSpeed(location.getSpeed());
			mLocationData.setRoute(MySharedPreferences.getInstance().getRoute(this));
		}
		else
			mLocationData.setHasLocationData(false);
		
		readWifi();
		
		
		
	}
	
	private void SaveLocationData(){
		
		MySharedPreferences.getInstance().saveLocationData(this, mLocationData);
		/*
		Log.i(FILE_TAG, "save location data locally");
		Log.i(FILE_TAG, "data: " + mLocationData.toJSON().toString());
		*/
		//Utils.playSound1(this, R.raw.save);
		mReadingSensorsNow = false;
	}
	/*
	private void readGPSData(){
		mLocationData.setTimestamp(System.currentTimeMillis());
		Location location = getLocation();
		if (location != null){
			mLocationData.setHasLocationData(true);
			mLocationData.setLatitude(location.getLatitude());
			mLocationData.setLongitude(location.getLongitude());
			if(location.hasAccuracy())
				mLocationData.setAccuracy(location.getAccuracy());
			if(location.hasSpeed())
				mLocationData.setSpeed(location.getSpeed());
			mLocationData.setRoute(MySharedPreferences.getInstance().getRoute(this));
		}
		else
			mLocationData.setHasLocationData(false);
		
		MySharedPreferences.getInstance().saveLocationData(this, mLocationData);
		Log.i(FILE_TAG, "save location data locally");
		Log.i(FILE_TAG, "data: " + mLocationData.toJSON().toString());
		//Utils.playSound(this, R.raw.save);
		mReadingSensorsNow = false;
	}
	*/

	
	
	
	private void start1MinNoSpeedTimer(){
		if (mNoSpeedTimer != null){
			mNoSpeedTimer.cancel();
			mNoSpeedTimer = null;
		}
		mNoSpeedTimer = new NoSpeedTimer(1000*60, 1000);
		mNoSpeedTimer.startNow1();
	}
	
	private void start5MinNoSpeedTimer(){
		if (mNoSpeedTimer != null){
			mNoSpeedTimer.cancel();
			mNoSpeedTimer = null;
		}
		mNoSpeedTimer = new NoSpeedTimer(1000*60*5, 1000);
		mNoSpeedTimer.startNow1();
	}

	public class NoSpeedTimer extends CountDownTimer {
		private boolean mIsActive = false;
		public NoSpeedTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

		public void startNow1(){
			if (mIsActive)
				  cancel();
			start();
			mIsActive = true;
		}
		
		public boolean isActive(){
			return mIsActive;
		}
		
		public void setIsActive(boolean isActive){
			mIsActive = isActive;
		}
		
		
       @Override
        public void onFinish() {
    	   Log.i(FILE_TAG, "onFinish GPS Times");
    	   //Utils.playSound(LbmService.this, R.raw.timer_end);
    	   
    	   finishTimerProcess();
			
       }

        @Override
        public void onTick(long millisUntilFinished) {
        }
        
        
       
	}

	

	private void finishTimerProcess(){
		 mWifiWasInit = false;
  	   
  	   if (!mWifiWasEnabled && mWifiWasSetOnByMe)
  		   mWifiTracker.setWifiDisabled();
  	   
  	   mNoSpeedTimer.setIsActive(false);
  	   mLocationManager.removeUpdates(mGPSLocationListener);
  	   mIsRequestLocationUpdateFirstTime = true;
  	   mIsListenToGPSProvider = false;
  	    mIsRequestLocationUpdateFirstTime = true;
  	    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE_HIGHT, GPS_MIN_DISTANCE_LOCATION_UPDATE, mNetworkLocationListener);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!isNetworkEnabled)
			mTelephonyDataTracker.requestCellUpdates(LbmService.this);
		if (mSoundStart)
		{			
		// Utils.playSound1(LbmService.this,R.raw.potential_zooz_mining_ended);
		 mSoundStart =false;
		}
	}

	@Override
	public void onCellChanged(int newCellID) {
		Log.i(FILE_TAG, "onCellChanged");
		//Utils.playSound(this, R.raw.cell_change);
		boolean ChargerConnectivityMode = MySharedPreferences.getInstance().getChargerConnectivityMode(this);
		boolean MiningEnabledMode = MySharedPreferences.getInstance().getMiningEnabledMode(this);
		if (!mIsListenToGPSProvider){
			if (MiningEnabledMode && ((!ChargerConnectivityMode) || (ChargerConnectivityMode && Utils.isPowerCableConnected(this)))){
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE_HIGHT, GPS_MIN_DISTANCE_LOCATION_UPDATE, mGPSLocationListener);
				start1MinNoSpeedTimer();
				mIsListenToGPSProvider = true;
				mIsListenToGPSProviderFromCellChange =true;
				mTelephonyDataTracker.removeUpdates();
				MySharedPreferences.getInstance().promoteRoute(this);
//				Utils.playSound1(this, R.raw.potential_zooz_mining_stared);
				
			}
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
		//else
			//Log.i(FILE_TAG, "onLocationChanged network");
		
		

//		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		boolean ChargerConnectivityMode = MySharedPreferences.getInstance().getChargerConnectivityMode(LbmService.this);
		boolean MiningEnabledMode = MySharedPreferences.getInstance().getMiningEnabledMode(LbmService.this);
		
		if (!mIsListenToGPSProvider){
			if (MiningEnabledMode && (!ChargerConnectivityMode || (ChargerConnectivityMode && Utils.isPowerCableConnected(LbmService.this)))){
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME_LOCATION_UPDATE_HIGHT, GPS_MIN_DISTANCE_LOCATION_UPDATE, mGPSLocationListener);
				mIsListenToGPSProvider = true;
				Log.i(FILE_TAG, "requestLocationUpdates GPS_PROVIDER");
				MySharedPreferences.getInstance().promoteRoute(LbmService.this);
				start1MinNoSpeedTimer();
				mLocationManager.removeUpdates(mNetworkLocationListener);
				
				mIsListenToGPSProviderFromNetChange = true;
				//Utils.playSound1(LbmService.this, R.raw.potential_zooz_mining_stared);
			}
		}

		/*
		if (!isGPSEnabled && isNetworkEnabled){ // if location from network and gps is off - check to display notif dialog
			if(MySharedPreferences.getInstance().shouldDisplayGPSNotif(LbmService.this))
				displayNotifGPSDialog();
		}*/
		
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(FILE_TAG, "network onProviderDisabled - " + provider);
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
		
		
		////Utils.playSound(LbmService.this, R.raw.disable_provider_net);
		mTelephonyDataTracker.requestCellUpdates(LbmService.this);

		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(FILE_TAG, "network onProviderEnabled - " + provider);
		////Utils.playSound(LbmService.this, R.raw.enable_provider_net);
		mTelephonyDataTracker.removeUpdates();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.OUT_OF_SERVICE){
			//Utils.playSound(LbmService.this, R.raw.status_no_service);
			Log.i(FILE_TAG, "network onStatusChanged - " + provider + ", OUT_OF_SERVICE");
			if (!mIsListenToGPSProvider)
				mTelephonyDataTracker.requestCellUpdates(LbmService.this);
		}
		else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE){
			//Utils.playSound(LbmService.this, R.raw.status_no_service_temp);
			Log.i(FILE_TAG, "network onStatusChanged - " + provider + ", TEMPORARILY_UNAVAILABLE");
			if (!mIsListenToGPSProvider)
				mTelephonyDataTracker.requestCellUpdates(LbmService.this);
		}
		else if (status == LocationProvider.AVAILABLE){
			//Utils.playSound(LbmService.this, R.raw.status_avail);
			Log.i(FILE_TAG, "network onStatusChanged - " + provider + ", AVAILABLE");
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
			boolean ChargerConnectivityMode = MySharedPreferences.getInstance().getChargerConnectivityMode(LbmService.this);
			boolean MiningEnabledMode = MySharedPreferences.getInstance().getMiningEnabledMode(LbmService.this);
			if ((!MiningEnabledMode)||(ChargerConnectivityMode && !Utils.isPowerCableConnected(LbmService.this))){
				if (mReadingSensorsNow)
					return;
				else {		
					if (mNoSpeedTimer.isActive())
						mNoSpeedTimer.cancel();
					finishTimerProcess();
					return;
				}
			}
			
			if (mReadingSensorsNow)
			{
				//Utils.playSound1(LbmService.this, R.raw.read_sensors);
				return;
			}
			
			
			//Utils.playSound(LbmService.this, R.raw.gps);
			if (mSendingDataToServer>0){
				mSendingDataToServer++;
				/*
				Log.i(FILE_TAG, "GPS onLocationChanged during sending data to server");
				if (mSendingDataToServer >= 6)
				{
					Utils.playSound1(LbmService.this, R.raw.send_data_6);
				}
				else
				 Utils.playSound1(LbmService.this, R.raw.server_sent);
				 */
				return;
			}


			if (location.hasSpeed()){
				Log.i(FILE_TAG, "GPS location hasSpeed");
				float speed = location.getSpeed();
				if ((speed > 2.7)||(mNoSpeedTimer.isActive())){   // 2.7m/s = 10km/h  enter if over 10km/s or the 5 min timer is active
				
					if (speed > 2.7){  // over 10 km/s start-over the timer
						if (location.hasAccuracy()){
							if (location.getAccuracy()<= 25){ //if gps is on - read sensors 				
								Log.i(FILE_TAG, "GPS Speed Over 10 kms");
								//Utils.playSound1(LbmService.this, R.raw.ten_kms);
								if ((mSoundStart== false)&&(mIsListenToGPSProviderFromCellChange||mIsListenToGPSProviderFromNetChange))
								{ 
									/* This is done here..because cell change might happend on idle state..and can cause to false gps mining start*/
									/* It need to ba handle*/
									Utils.playSound1(LbmService.this, R.raw.potential_zooz_mining_stared);
									mSoundStart = true;
									if (mIsListenToGPSProviderFromCellChange)
									 mIsListenToGPSProviderFromCellChange = false;
									if (mIsListenToGPSProviderFromNetChange)
										mIsListenToGPSProviderFromNetChange = false;
									
								}
								start5MinNoSpeedTimer();
							
								if (!mWifiWasInit)
									initWifi();
								else
								{
									if (mWifiWasEnabled ==false)
									{
										if (mWifiTracker.isWifiEnabled()){
											mWifiWasSetOnByMe = true;
										}
									}
								}
						
								
								readSensors(location);
							}
							/*
							else if(location.getAccuracy()> 100){  				
								Utils.playSound1(LbmService.this, R.raw.accuracy_over_100);
							} 
							else if(location.getAccuracy()> 50){  				
								Utils.playSound1(LbmService.this, R.raw.accuracy_over_50);
							} 
							*/
							else if(location.getAccuracy()> 25){  				
							//	Utils.playSound1(LbmService.this, R.raw.accuracy_over_25);
							}
							/*
						}
						else
							Utils.playSound1(LbmService.this, R.raw.no_accuracy);
					}
					else
						Utils.playSound1(LbmService.this, R.raw.less_then_ten);	
				}
				else
					Utils.playSound1(LbmService.this, R.raw.less_then_ten);				
			}
			else
				Utils.playSound1(LbmService.this, R.raw.less_then_ten);
				*/
						}
						}
					}
				}
				
			
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(FILE_TAG, "GPS onProviderDisabled - " + provider);
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
			Log.i(FILE_TAG, "GPS onProviderEnabled - " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (status == LocationProvider.OUT_OF_SERVICE){
				//Utils.playSound(LbmService.this, R.raw.status_no_service);
				Log.i(FILE_TAG, "GPS onStatusChanged - " + provider + ", OUT_OF_SERVICE");
			}
			else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE){
				//Utils.playSound(LbmService.this, R.raw.status_no_service_temp);
				Log.i(FILE_TAG, "GPS onStatusChanged - " + provider + ", TEMPORARILY_UNAVAILABLE");
			}
			else if (status == LocationProvider.AVAILABLE){
				//Utils.playSound(LbmService.this, R.raw.status_avail);
				//Log.i(FILE_TAG, "GPS onStatusChanged - " + provider + ", AVAILABLE");
			}
			
		}
   }
		   
  
}
