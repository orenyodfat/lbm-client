package com.lazooz.lbm;




import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.businessClasses.ServerData;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.Utils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends MyActionBarActivity implements LocationListener {
	
	private Timer ShortPeriodTimer;
	private TextView mDistanceTV;
	private TextView mZoozBalTV;

	private Button mAddFriendsBtn;
	private Button mShakeBtn;
	private ProgressBar mCriticalMassPB;
	private GPSTracker mGPS;
	private LocationManager mLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState, R.layout.activity_main_new);
		//setContentView(R.layout.activity_main);

		
		startService(new Intent(this, LbmService.class));
		
		mDistanceTV = (TextView)findViewById(R.id.main_distance_tv);
		mDistanceTV.setText("0.0");
		mZoozBalTV = (TextView)findViewById(R.id.main_zoz_balance_tv);
		mZoozBalTV.setText("0.0");
		
		
		mAddFriendsBtn = (Button)findViewById(R.id.main_add_friends_btn);
		mAddFriendsBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainAddFriendsActivity.class);
				startActivity(intent);
				
			}
		});
		
		mShakeBtn = (Button)findViewById(R.id.main_shake_btn);
		mShakeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainShakeActivity.class);
				startActivity(intent);			
			}
		});
		
		mDistanceTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainDistanceActivity.class);
				startActivity(intent);
			}
		});
		
		mZoozBalTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainZoozActivity.class);
				startActivity(intent);
			}
		});
		
		
		
		mCriticalMassPB = (ProgressBar)findViewById(R.id.main_critical_mass_pb);
		mCriticalMassPB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
				startActivity(intent);
			}
		});
		
		
		
		 final Handler guiHandler = new Handler();
		 final Runnable guiRunnable = new Runnable() {
		      public void run() {
		         UpdateGUI();
		      }
		   };
		
		
		ShortPeriodTimer = new Timer();
		TimerTask twoSecondsTimerTask = new TimerTask() {
				@Override
				public void run() {
					guiHandler.post(guiRunnable);				
				}
			};
		ShortPeriodTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 10*1000);

		startOnDayScheduler();
		
		
		
		MySharedPreferences.getInstance().setStage(this, MySharedPreferences.STAGE_MAIN);
		
		getUserKeyDataAsync();
		
		handleGPS();
		/*
		mGPS = GPSTracker.getInstance(this);
		mGPS.setOnLocationListener(this);
		
		if (!mGPS.isGPSEnabled()){
			mGPS.showSettingsAlert();
		}
		
		mGPS.getLocation();*/
	}

	
	private void handleGPS() {
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 10, this);
		
		String theMessage = "hey";
		
		if (!isGPSEnabled)
			Utils.showSettingsAlert(this, theMessage);
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
	
	
	protected void UpdateGUI() {
		ServerData sd = MySharedPreferences.getInstance().getServerData(this);
		mDistanceTV.setText(sd.getDistance());
		mZoozBalTV.setText(sd.getZoozBalance());
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (mProgBar != null)
				mProgBar.setVisibility(View.GONE);
		    if (requestCode == 1) {
		        if(resultCode == RESULT_OK){
		        	String fromActivity = data.getStringExtra("ACTIVITY");
		        	String s = MySharedPreferences.getInstance().getRecommendUserList(this).toString();
		            Log.e("aaa", s);
		            sendFriendRecommendToServerAsync(s);
		        }
		        if (resultCode == RESULT_CANCELED) {
		            //Write your code if there's no result
		        }
		    }
	}

	
	
	
	
	private void sendFriendRecommendToServerAsync(String data){
		FriendRecommendToServer friendRecommendToServer = new FriendRecommendToServer();
		friendRecommendToServer.execute(data);
	}
	
	private class FriendRecommendToServer extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(MainActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				JSONArray dataList = msp.getRecommendUserList(MainActivity.this);
				
				bServerCom.setFriendRecommend(msp.getUserId(MainActivity.this), msp.getUserSecret(MainActivity.this), dataList.toString());
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
				Toast.makeText(MainActivity.this, "Friend List Sent", Toast.LENGTH_LONG).show();
				startActivity(new Intent(MainActivity.this, CongratulationsGetFriendsActivity.class));
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}

	
	private void getUserKeyDataAsync(){
		GetUserKeyData getUserKeyData = new GetUserKeyData();
		getUserKeyData.execute();
	}
	
	private class GetUserKeyData extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(MainActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				bServerCom.getUserKeyData(msp.getUserId(MainActivity.this), msp.getUserSecret(MainActivity.this));
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
						String zoozBalance = jsonReturnObj.getString("zooz_balance");
						String distance = jsonReturnObj.getString("zooz_distance_balance");
						boolean isDistanceAchievement = Utils.yesNoToBoolean(jsonReturnObj.getString("is_distance_achievement"));
						
						MySharedPreferences.getInstance().saveDataFromServer(MainActivity.this, zoozBalance, distance, isDistanceAchievement);
						
						
						
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
				UpdateGUI();
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}


	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(this, "onLocationChanged", Toast.LENGTH_LONG).show();
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "onProviderDisabled", Toast.LENGTH_LONG).show();
		if(provider.equals("")){
			
		}
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "onProviderEnabled", Toast.LENGTH_LONG).show();
		if(provider.equals("")){
			
		}
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this, "onStatusChanged " + status, Toast.LENGTH_LONG).show();
		if (status == 2){
			
		}
		
	}

	
	
}
