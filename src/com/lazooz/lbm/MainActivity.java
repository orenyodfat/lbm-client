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
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.OfflineActivities;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends MyActionBarActivity  {
	
	private Timer ShortPeriodTimer;
	private TextView mDistanceTV;
	private TextView mZoozBalTV;

	private ImageButton mAddFriendsBtn;
	private ImageButton mShakeBtn;
	private ProgressBar mCriticalMassPB;
	private GPSTracker mGPS;
	private LocationManager mLocationManager;
	private TextView mFriendsTV;
	private TextView mShakeTV;
	private ImageButton mDistanceBtn;
	private ImageButton mZoozBalBtn;
	private FrameLayout mCriticalMassFrame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState, R.layout.activity_main_new1);
		//setContentView(R.layout.activity_main);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));

		OfflineActivities.getInstance(this).transmitDataToServer();

		startService(new Intent(this, LbmService.class));
		
		mDistanceTV = (TextView)findViewById(R.id.main_distance_tv);
		mDistanceTV.setText("0.0");
		mZoozBalTV = (TextView)findViewById(R.id.main_zoz_balance_tv);
		mZoozBalTV.setText("0.0");
		mFriendsTV = (TextView)findViewById(R.id.main_friends_tv);
		mFriendsTV.setText("0.0");
		mShakeTV = (TextView)findViewById(R.id.main_shake_tv);
		mShakeTV.setText("0.0");
		
		mAddFriendsBtn = (ImageButton)findViewById(R.id.main_friends_btn);
		mAddFriendsBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainAddFriendsActivity.class);
				startActivity(intent);
				
			}
		});
		
		mShakeBtn = (ImageButton)findViewById(R.id.main_shake_btn);
		mShakeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainShakeActivity.class);
				startActivity(intent);			
			}
		});
		
		mDistanceBtn = (ImageButton)findViewById(R.id.main_distance_btn);
		mDistanceBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainDistanceActivity.class);
				startActivity(intent);
			}
		});
		
		mZoozBalBtn = (ImageButton)findViewById(R.id.main_zoz_balance_btn);
		mZoozBalBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainZoozActivity.class);
				startActivity(intent);
			}
		});
		
		
		
		mCriticalMassPB = (ProgressBar)findViewById(R.id.main_critical_mass_pb);
		mCriticalMassFrame = (FrameLayout)findViewById(R.id.main_critical_mass_frame);
		
		mCriticalMassFrame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
				intent.putExtra("URL", "http://lazooz.org");
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
		ShortPeriodTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 3*1000);

		startOnDayScheduler();
		
		
		
		MySharedPreferences.getInstance().setStage(this, MySharedPreferences.STAGE_MAIN);
		
		getUserKeyDataAsync();
		
		
		
	}


	@Override
	protected void onResume() {
		super.onResume();
		checkGPS();
	}
	
	
	private void checkGPS() {
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		
		if (!isGPSEnabled && isNetworkEnabled)
			Utils.showSettingsAlert(this, getString(R.string.gps_message_no_gps_yes_net));
		else if (!isGPSEnabled && !isNetworkEnabled)
			Utils.showSettingsAlert(this, getString(R.string.gps_message_no_gps_no_net));
		else if (isGPSEnabled && !isNetworkEnabled)
			Utils.showSettingsAlert(this, getString(R.string.gps_message_yes_gps_no_net));
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

		MySharedPreferences msp = MySharedPreferences.getInstance();
		ServerData sd = msp.getServerData(this);
		
		float distanceFromServer = sd.getDistanceFloat();
		float distanceLocal = msp.getLocalDistance(this);
		float distanceTotal = distanceFromServer + distanceLocal;
		float distanceKMf = distanceTotal / 1000;
		float distanceMf = distanceTotal % 1000;

		int distanceKMd = (int)distanceKMf;
		int distanceMd = (int)distanceMf;
		
		int localDist = (int)distanceLocal;
		
		//mDistanceTV.setText(String.format("%dkm  %dm , l=%d", distanceKMd, distanceMd, localDist));
		//mDistanceTV.setText(String.format("%dkm  %dm", distanceKMd, distanceMd));
		mDistanceTV.setText(String.format("%.1f", distanceKMf));

		
		//mDistanceTV.setText(sd.getDistance());
		float f = Float.valueOf(sd.getZoozBalance());
		mZoozBalTV.setText(String.format("%.2f", f));
		
		
		int numInvitedContacts = msp.getNumInvitedContacts(this);
		int numShakedUsers = msp.getNumShakedUsers(this);
		
		mFriendsTV.setText(numInvitedContacts+"");
		mShakeTV.setText(numShakedUsers +"");		
	
	}
	

	

	
	
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

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
		        	String theMessage = data.getStringExtra("MESSAGE");
		        	String s = MySharedPreferences.getInstance().getRecommendUserList(this).toString();
		            Log.e("aaa", s);
		            sendFriendRecommendToServerAsync(theMessage);
		        }
		        if (resultCode == RESULT_CANCELED) {
		            //Write your code if there's no result
		        }
		    }
	}

	
	
	
	
	private void sendFriendRecommendToServerAsync(String theMessage){
		FriendRecommendToServer friendRecommendToServer = new FriendRecommendToServer();
		friendRecommendToServer.execute(theMessage);
	}
	
	private class FriendRecommendToServer extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(MainActivity.this);
          	String theMessage = params[0];
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				JSONArray dataList = msp.getRecommendUserList(MainActivity.this);
				
				bServerCom.setFriendRecommend(msp.getUserId(MainActivity.this), msp.getUserSecret(MainActivity.this), dataList.toString(), theMessage);
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
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(MainActivity.this);
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
						String potentialZoozBalance = jsonReturnObj.getString("potential_zooz_balance");
						String distance = jsonReturnObj.getString("zooz_distance_balance");
						String serverVer = jsonReturnObj.getString("server_version");
						boolean isDistanceAchievement = Utils.yesNoToBoolean(jsonReturnObj.getString("is_distance_achievement"));
						String walletNum = jsonReturnObj.getString("wallet_num");
						int numShakedUsers = jsonReturnObj.getInt("num_shaked_users");
						int numInvitedContacts = jsonReturnObj.getInt("num_invited_contacts");
						String userId = jsonReturnObj.getString("user_id");
						
						
						
						MySharedPreferences.getInstance().saveDataFromServer1(MainActivity.this, zoozBalance, potentialZoozBalance, distance, 
								isDistanceAchievement, serverVer, walletNum, numShakedUsers, numInvitedContacts, userId);
						
						
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
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(MainActivity.this);
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}


	

	
	
}
