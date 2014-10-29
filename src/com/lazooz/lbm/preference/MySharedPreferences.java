package com.lazooz.lbm.preference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.CongratulationsRegActivity;
import com.lazooz.lbm.IntroActivity;
import com.lazooz.lbm.MainActivity;
import com.lazooz.lbm.MapShowLocationActivity;
import com.lazooz.lbm.R;
import com.lazooz.lbm.RegistrationActivity;
import com.lazooz.lbm.SplashActivity;
import com.lazooz.lbm.businessClasses.Contact;
import com.lazooz.lbm.businessClasses.LocationData;
import com.lazooz.lbm.businessClasses.ServerData;
import com.lazooz.lbm.businessClasses.UserNotification;
import com.lazooz.lbm.businessClasses.UserNotificationList;
import com.lazooz.lbm.utils.Utils;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.util.Log;

public class MySharedPreferences {
	
	public final static int STAGE_NEVER_RUN = 0;
	public final static int STAGE_INTRO = 1;
	public final static int STAGE_MAP = 2;
	public final static int STAGE_REG_INIT = 3;
	public final static int STAGE_REG_CELL_SENT = 4;
	public final static int STAGE_REG_CELL_SENT_OK = 5;
	public final static int STAGE_REG_CONF_SENT = 6;
	public final static int STAGE_REG_CONF_SENT_OK = 7;
	public final static int STAGE_REG_CONGRATS = 8;
	public final static int STAGE_MAIN = 9;
	
	private static MySharedPreferences instance = null;

	protected MySharedPreferences() {
	      
	   }
	
	public static MySharedPreferences getInstance() {
	      if(instance == null) {
	         instance = new MySharedPreferences();
	      }
	      return instance;
	   }
	
	public static void removeInstance() {
	      instance = null;
	   }
	
	public static Editor putDouble(final Editor edit, final String key, final double value) {
		   return edit.putLong(key, Double.doubleToRawLongBits(value));
	}

	public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
		return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
	}
	
	
	public Class<?> getNextActivity(Context context){
		int stage = getStage(context);

		switch (stage) {
		case STAGE_NEVER_RUN:
			return IntroActivity.class;
		case STAGE_INTRO:
			return MapShowLocationActivity.class;
		case STAGE_MAP:
			return RegistrationActivity.class;
		case STAGE_REG_INIT:
			return RegistrationActivity.class;
		case STAGE_REG_CELL_SENT:
			return RegistrationActivity.class;
		case STAGE_REG_CELL_SENT_OK:
			return RegistrationActivity.class;
		case STAGE_REG_CONF_SENT:
			return RegistrationActivity.class;
		case STAGE_REG_CONF_SENT_OK:
			return CongratulationsRegActivity.class;
		case STAGE_REG_CONGRATS:
			return MainActivity.class;
		case STAGE_MAIN:
			return MainActivity.class;
			

		default:
			return IntroActivity.class;
		}

	}
	
	
	
	
	
	public int getStage(Context context){
		SharedPreferences spData = context.getSharedPreferences("AppData", Context.MODE_MULTI_PROCESS);
		return spData.getInt("STAGE", STAGE_NEVER_RUN);		
	}
	
	public void setStage(Context context, int stage){
		SharedPreferences spData = context.getSharedPreferences("AppData", Context.MODE_MULTI_PROCESS);
		spData.edit().putInt("STAGE", stage).commit();
	}
	
	public void initFirstTime(Context context){
		SharedPreferences spData = context.getSharedPreferences("AppData", Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = spData.edit();
		editor.putBoolean("initFirstTime", true);
		editor.commit();
	}
	
	public boolean wasInitFirstTime(Context context){
		SharedPreferences spData = context.getSharedPreferences("AppData", Context.MODE_MULTI_PROCESS);
		return spData.getBoolean("initFirstTime", false);
	}
	
	
	
	public void clearAll(Context context){
		SharedPreferences spData = context.getSharedPreferences("RegData",Context.MODE_MULTI_PROCESS);
		spData.edit().clear().commit();

		spData = context.getSharedPreferences("AppData",Context.MODE_MULTI_PROCESS);
		spData.edit().clear().commit();
		
		spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);
		spData.edit().clear().commit();
		
		spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		spData.edit().clear().commit();


	}

	
	
	public String getUserId(Context context) {
		SharedPreferences spData = context.getSharedPreferences("RegData",Context.MODE_MULTI_PROCESS);
		return spData.getString("userId", "");
	}



	public String getUserSecret(Context context) {
		SharedPreferences spData = context.getSharedPreferences("RegData",Context.MODE_MULTI_PROCESS);
		return spData.getString("userSecret", "");
	}




	public void saveRegRequestId(Context context, String requestId) {
		SharedPreferences spData = context.getSharedPreferences("RegData",Context.MODE_MULTI_PROCESS);
		spData.edit().putString("requestId", requestId).commit();
	}

	public String getRegRequestId(Context context) {
		SharedPreferences spData = context.getSharedPreferences("RegData",Context.MODE_MULTI_PROCESS);
		return spData.getString("requestId", "");
	}

	public void saveActivationData(Context context, String userId, String userSecret) {
		SharedPreferences spData = context.getSharedPreferences("RegData",Context.MODE_MULTI_PROCESS);
	    SharedPreferences.Editor editor = spData.edit();
	    
	    editor.putString("userId", userId);
	    editor.putString("userSecret", userSecret);
	    	    
	    editor.commit();
		
	}
	
	public long getRoute(Context context){
		SharedPreferences spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);
		long route = spData.getLong("CurrentRoute", -1);
		if (route > -1)
			return route;
		
		long numTime = System.currentTimeMillis();
		spData.edit().putLong("CurrentRoute", numTime).commit();
		return numTime;
	}
	
	public void promoteRoute(Context context){
		SharedPreferences spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);

		long numTime = System.currentTimeMillis();
		spData.edit().putLong("CurrentRoute", numTime).commit();
	}
	
	public void saveLocationData(Context context, LocationData ld){
		SharedPreferences spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);
		Editor editor = spData.edit();
		
		//int commitedReadCursor = spData.getInt("CommitedReadCursor", 1);
		
		int writeCursor = spData.getInt("WriteCursor", 0);
		
		Log.i("ZOOZ", "saveLocationData writeCursor: " + writeCursor);
		
		float prevDistance = spData.getFloat("LocalDist", 0);
		double prevLong = getDouble(spData, "LocalLongPrev", 0);
		double prevLat = getDouble(spData, "LocalLatPrev", 0);
		
		Log.i("ZOOZ", "saveLocationData prevDistance: " + prevDistance);
		Log.i("ZOOZ", "saveLocationData prevLong: " + prevLong);
		Log.i("ZOOZ", "saveLocationData prevLat: " + prevLat);
		
		putDouble(editor, "LocalLongPrev", ld.getLongitude());
		putDouble(editor, "LocalLatPrev", ld.getLatitude());
		
		// handle local distance
		
		
		if (prevLong != 0){
			float res = 0;
			
			try {
				
				Location locationA = new Location("point A");
				locationA.setLatitude(prevLat);
				locationA.setLongitude(prevLong);

				Location locationB = new Location("point B");
				locationB.setLatitude(ld.getLatitude());
				locationB.setLongitude(ld.getLongitude());

				res = locationA.distanceTo(locationB);
				
				Log.i("ZOOZ", "saveLocationData distanceTo: " + res);
				
				if (res > 1000)
					res = 0;
				
			} catch (Exception e) {
				res = 0;
			}
			
			res = res + prevDistance;
			editor.putFloat("LocalDist", res);			
		}
		
		/*String prevLocationString = spData.getString("LocationDataList_"+ writeCursor, "");
		if (!prevLocationString.equals("")){
			try {
				JSONObject obj = new JSONObject(prevLocationString);
				LocationData prevLocation = new LocationData(obj);
				float dist = Math.abs(ld.distanceBetween(prevLocation));
				dist = prevDistance + dist;
				editor.putFloat("LocalDist", dist);
			} catch (JSONException e) {
			}
		}*/
		
		
		writeCursor++;
		
		
		editor.putString("LocationDataList_" + writeCursor, ld.toJSON().toString());
		editor.putInt("WriteCursor", writeCursor);
		
		editor.commit();
	}
	
	


	public float getLocalDistance(Context context){
		SharedPreferences spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);
		return spData.getFloat("LocalDist", 0);
	}
	
	public JSONArray getLocationDataList(Context context){
		SharedPreferences spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);
		boolean dataExist = false;
	
		int writeCursor = spData.getInt("WriteCursor", 0);
		if (writeCursor == 0)
			return null;
		
		int commitedReadCursor = spData.getInt("CommitedReadCursor", 1);
		JSONArray jsArray = new JSONArray();
		try {
			Log.e("MSP", "read from: " + commitedReadCursor + " to: " + writeCursor);
			for (int i=commitedReadCursor; i<=writeCursor; i++){
				dataExist = true;
				String locationDataString = spData.getString("LocationDataList_" + i, "");
				/*todo : Need to check here if the locationDataString is not "" - this throws exception */
				JSONObject obj = new JSONObject(locationDataString);
				jsArray.put(obj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		spData.edit().putInt("ReadCursor", writeCursor).commit();
		if (dataExist)
			return jsArray;
		else
			return null;
	}
	
	public void commitReadCursor(Context context){ // commit the read cursor
		SharedPreferences spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);
		int readCursor = spData.getInt("ReadCursor", 1);
		Editor editor = spData.edit();
		
		int prevCommitedReadCursor = spData.getInt("CommitedReadCursor", 1);
		
		editor.putInt("CommitedReadCursor", readCursor+1).commit();
		
		int currentCommitedReadCursor = spData.getInt("CommitedReadCursor", 1);
		currentCommitedReadCursor--;
		
		for (int i= prevCommitedReadCursor; i<= currentCommitedReadCursor; i++){
			editor.remove("LocationDataList_" + i );
		}
		
		editor.remove("LocalDist");
		
		
		editor.commit();
		
		Log.e("MSP", "delete from: " + prevCommitedReadCursor + " to: " + currentCommitedReadCursor);
	}

	public void addRecommendUser(Context context, Contact contactBean) {
			SharedPreferences spData = context.getSharedPreferences("RecommendUser",Context.MODE_MULTI_PROCESS);		
			Editor editor = spData.edit();
			editor.putString(contactBean.getKey(), contactBean.toJSON().toString());
			editor.commit();
			
	}
	
	public void removeRecommendUser(Context context, Contact contactBean) {
		SharedPreferences spData = context.getSharedPreferences("RecommendUser",Context.MODE_MULTI_PROCESS);
		spData.edit().remove(contactBean.getKey()).commit();
	}
	
	public void clearRecommendUsers(Context context) {
		SharedPreferences spData = context.getSharedPreferences("RecommendUser",Context.MODE_MULTI_PROCESS);
		spData.edit().clear().commit();
	}

	public boolean areThere3RecommendUser(Context context){
		SharedPreferences spData = context.getSharedPreferences("RecommendUser",Context.MODE_MULTI_PROCESS);
		Map<String,?> keys = spData.getAll();
		return (keys.size() >= 3);
	}

	public boolean areThereRecommendUser(Context context){
		SharedPreferences spData = context.getSharedPreferences("RecommendUser",Context.MODE_MULTI_PROCESS);
		Map<String,?> keys = spData.getAll();
		return (keys.size() > 0);
	}

	public int getNumOfRecommendUser(Context context){
		SharedPreferences spData = context.getSharedPreferences("RecommendUser",Context.MODE_MULTI_PROCESS);
		Map<String,?> keys = spData.getAll();
		return (keys.size());
	}

	public String getRecommendUserName(Context context){
		SharedPreferences spData = context.getSharedPreferences("RecommendUser",Context.MODE_MULTI_PROCESS);
		Map<String,?> keys = spData.getAll();
		try {
			if (keys.size() == 1){
				for(Map.Entry<String,?> entry : keys.entrySet()){
					JSONObject obj = new JSONObject(entry.getValue().toString());      
					Contact contact = new Contact(obj, "IL");
					return contact.getName();
				 }
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	
	public void saveDefaultMyName(Context context, String myName){
		SharedPreferences spData = context.getSharedPreferences("FriendsMessage",Context.MODE_MULTI_PROCESS);
		spData.edit().putString("MyName", myName).commit();		
	}
	
	public void saveDefaultFriendsMessage(Context context, String msg){
		SharedPreferences spData = context.getSharedPreferences("FriendsMessage",Context.MODE_MULTI_PROCESS);
		spData.edit().putString("Message", msg).commit();				
	}

	public String getDefaultMyName(Context context){
		SharedPreferences spData = context.getSharedPreferences("FriendsMessage",Context.MODE_MULTI_PROCESS);
		//return spData.getString("MyName", "John");		
		return spData.getString("MyName", "");
	}
	
	public String getDefaultFriendsMessage(Context context){
		SharedPreferences spData = context.getSharedPreferences("FriendsMessage",Context.MODE_MULTI_PROCESS);
		return spData.getString("Message", "");
	}
	
	
	public JSONArray getRecommendUserList(Context context){
		SharedPreferences spData = context.getSharedPreferences("RecommendUser",Context.MODE_MULTI_PROCESS);
		
		JSONArray jsArray = new JSONArray();
		JSONObject retObj = new JSONObject() ;
		
		Map<String,?> keys = spData.getAll();
		
		try {
			for(Map.Entry<String,?> entry : keys.entrySet()){
				jsArray.put(new JSONObject(entry.getValue().toString()));            
			 }


		} catch (JSONException e) {
			e.printStackTrace();
		}
		

		
		
		
		return jsArray;
	}
	
	public boolean isDistanceAchievement(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		
		return spData.getBoolean("IsDistanceAchievement", false);
		
	}
	
	public ServerData getServerData(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		ServerData sd = new ServerData();
		
		sd.setZoozBalance(spData.getString("ZoozBalance", "0.0"));
		sd.setPotentialZoozBalance(spData.getString("PotentialZoozBalance", "0.0"));
		sd.setDistance(spData.getString("Distance", "0.0"));
		sd.setIsDistanceAchievement(spData.getBoolean("IsDistanceAchievement", false));
		sd.setTimeStamp(spData.getLong("TimeStamp", 0));
		return sd;
	}
	
	public String getServerVersion(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		return spData.getString("ServerVersion", "");
	}
	
	public void saveDataFromServer1(Context context, String zoozBalance, String potentialZoozBalance, String distance, boolean isDistanceAchievement, String serverVer, 
			String walletNum, int numShakedUsers, int numInvitedContacts, int criticalMass, String dolarConvertionRate, String userId) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		Editor editor = spData.edit();
		editor.putString("ZoozBalance", zoozBalance);
		
		
		String pz = spData.getString("PotentialZoozBalance", "0.0");
		if (spData.getString("PotentialZoozBalance", "0.0").equalsIgnoreCase(potentialZoozBalance)==false)
			 Utils.playSound1(context, R.raw.drop_coin_10);
		editor.putString("PotentialZoozBalance", potentialZoozBalance);
		
		editor.putString("Distance", distance);
		editor.putString("ServerVersion", serverVer);
		editor.putBoolean("IsDistanceAchievement", isDistanceAchievement);
		
		editor.putString("WalletNum", walletNum);
		editor.putInt("NumShakedUsers", numShakedUsers);
		editor.putInt("NumInvitedContacts", numInvitedContacts);
		editor.putInt("CriticalMass", criticalMass);
		editor.putString("DolarConvertionRate", dolarConvertionRate);
		editor.putString("UserId", userId);
		
		
		editor.putLong("TimeStamp", System.currentTimeMillis());
		editor.commit();
	}
	
	public String getWalletNum(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		return spData.getString("WalletNum", "");
	}
	
	public String getDolarConvertionRate(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		return spData.getString("DolarConvertionRate", "");
	}

	public String getUserIdSD(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		return spData.getString("UserId", "");
	}

	public int getNumShakedUsers(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		return spData.getInt("NumShakedUsers", 0);
	}
	
	public int getNumInvitedContacts(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		return spData.getInt("NumInvitedContacts", 0);
	}

	public int getCriticalMass(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		return spData.getInt("CriticalMass", 0);
	}

	public void saveDataFromServerService(Context context, String zoozBalance, String potentialZoozBalance, String distance, boolean isDistanceAchievement) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		Editor editor = spData.edit();
		editor.putString("ZoozBalance", zoozBalance);
		editor.putString("PotentialZoozBalance", potentialZoozBalance);
		editor.putString("Distance", distance);
		editor.putBoolean("IsDistanceAchievement", isDistanceAchievement);
		editor.putLong("TimeStamp", System.currentTimeMillis());
		editor.commit();
	}
	
	public void saveContactsWithInstalledApp(Context context, JSONArray contacts) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		spData.edit().putString("ContactsWithInstalledApp", contacts.toString()).commit();
	}
	
	public List<String> getContactsWithInstalledApp(Context context) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		String s = spData.getString("ContactsWithInstalledApp", "");
		JSONArray contactsArray = null;
		try {
			contactsArray = new JSONArray(s);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<String> list = new ArrayList<String>();
		if (contactsArray != null){
			for (int i=0; i<contactsArray.length(); i++) {
			    try {
					list.add( contactsArray.getString(i) );
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	public void saveContactsRecommended(Context context, JSONArray contacts) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		spData.edit().putString("ContactsRecommended", contacts.toString()).commit();
	}
	
	public List<String> getContactsRecommended(Context context) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		String s = spData.getString("ContactsRecommended", "");
		JSONArray contactsArray = null;
		try {
			contactsArray = new JSONArray(s);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<String> list = new ArrayList<String>();
		if (contactsArray != null){
			for (int i=0; i<contactsArray.length(); i++) {
			    try {
					list.add( contactsArray.getString(i) );
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	
	

	public void saveScreenInfoText(Context context, JSONObject jsonObj) {
		SharedPreferences spData = context.getSharedPreferences("ScreenInfo",Context.MODE_MULTI_PROCESS);
		spData.edit().putString("ScreenInfoText", jsonObj.toString()).commit();		
	}
	
	

	public String getIntroScreenText(Context context) {
		SharedPreferences spData = context.getSharedPreferences("ScreenInfo",Context.MODE_MULTI_PROCESS);
		String res = "";
		String s = spData.getString("ScreenInfoText", "");
		JSONObject obj;
		try {
			obj = new JSONObject(s);
			res = obj.getString("intro_screen_text");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public String getDisclaimerScreenText(Context context) {
		SharedPreferences spData = context.getSharedPreferences("ScreenInfo",Context.MODE_MULTI_PROCESS);
		String res = "";
		String s = spData.getString("ScreenInfoText", "");
		JSONObject obj;
		try {
			obj = new JSONObject(s);
			res = obj.getString("disclaimer_screen_text");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public String getDisclaimerScreenTitle(Context context) {
		SharedPreferences spData = context.getSharedPreferences("ScreenInfo",Context.MODE_MULTI_PROCESS);
		String res = "";
		String s = spData.getString("ScreenInfoText", "");
		JSONObject obj;
		try {
			obj = new JSONObject(s);
			res = obj.getString("disclaimer_screen_headline_text");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return res;
	}

	
	public String getSecondScreenText(Context context) {
		SharedPreferences spData = context.getSharedPreferences("ScreenInfo",Context.MODE_MULTI_PROCESS);
		String res = "";
		String s = spData.getString("ScreenInfoText", "");
		JSONObject obj;
		try {
			obj = new JSONObject(s);
			res = obj.getString("second_step_screen_text");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	
	public String getBeforShakeText(Context context) {
		SharedPreferences spData = context.getSharedPreferences("ScreenInfo",Context.MODE_MULTI_PROCESS);
		String res = "";
		String s = spData.getString("ScreenInfoText", "");
		JSONObject obj;
		try {
			obj = new JSONObject(s);
			res = obj.getString("before_shake_screen_text");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return res;
	}

	
	public void setDefaultGPSNotif(Context context, boolean isDontShow, long currentTimeMillis) {
		SharedPreferences spData = context.getSharedPreferences("GPSNotif",Context.MODE_MULTI_PROCESS);
		Editor editor = spData.edit();
		editor.putBoolean("GPSNotifDontShow", isDontShow);
		editor.putLong("GPSNotifDontShowTime", currentTimeMillis);
		editor.commit();
	}
	
	public boolean shouldDisplayGPSNotif(Context context){
		SharedPreferences spData = context.getSharedPreferences("GPSNotif",Context.MODE_MULTI_PROCESS);
		boolean isDontShow = spData.getBoolean("GPSNotifDontShow", false);
		long timeStampMillis = spData.getLong("GPSNotifDontShowTime", 0);
		
		if (isDontShow) // user ask not to show
			return false;
		else {  // user did not ask not to show, show on the next day only
			long now = System.currentTimeMillis();
			if (now - timeStampMillis > 1000*60*60*24)
				return true;
			else
				return false;
		}
	}

	public void saveKeyPair(Context context, String privateKey, String publicKey) {
		SharedPreferences spData = context.getSharedPreferences("KeyPair",Context.MODE_MULTI_PROCESS);
		Editor editor = spData.edit();
		editor.putString("PrivateKey", privateKey);
		editor.putString("PublicKey", publicKey);
		editor.commit();		
	}
	
	public String getPublicKey(Context context) {
		SharedPreferences spData = context.getSharedPreferences("KeyPair",Context.MODE_MULTI_PROCESS);
		return spData.getString("PublicKey", "");
	}
	
	public String getPrivateKey(Context context) {
		SharedPreferences spData = context.getSharedPreferences("KeyPair",Context.MODE_MULTI_PROCESS);
		return spData.getString("PrivateKey", "");
	}

	public int getLastNotificationNum(Context context, int initDefault) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		int lastNotificationNum = spData.getInt("SavedNotifNum", -1);
		if (lastNotificationNum == -1){
			spData.edit().putInt("SavedNotifNum", initDefault).commit();
			return initDefault;
		}
		else
			return lastNotificationNum;
	}
	
	public void setLastNotificationNum(int notifNum, Context context) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		spData.edit().putInt("SavedNotifNum", notifNum).commit();
	}

	public UserNotificationList getNotificationsToDisplay(Context context) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		String s = spData.getString("DisplayedNotificationList", "");
		UserNotificationList unl;
			try {
				if (!s.equals("")){
					unl = new UserNotificationList(new JSONArray(s));
					spData.edit().putString("DisplayedNotificationList", "").commit();
					return unl;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
	}

	public void addNotificationToDisplayList(Context context, UserNotification notif) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		String s = spData.getString("DisplayedNotificationList", "");
		UserNotificationList unl;
		try {
			if (s.equals(""))
				unl = new UserNotificationList();
			else
				unl = new UserNotificationList(new JSONArray(s));
			
			unl.getNotifications().add(notif);
			spData.edit().putString("DisplayedNotificationList", unl.toJSON().toString()).commit();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void saveBuildNum(Context context, int srvrCurrentBuildNum, int srvrMinBuildNum) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		Editor editor = spData.edit();
		editor.putInt("CurrentBuildNum", srvrCurrentBuildNum);
		editor.putInt("MinBuildNum", srvrMinBuildNum);
		editor.commit();
	}
	
	public boolean isUnderMinBuildNum(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		int srvrMinBuildNum = spData.getInt("MinBuildNum", 0);
		int srvrCurrentBuildNum = spData.getInt("CurrentBuildNum", 0);
		int localBuildNum = Utils.getVersionCode(context);
		return (localBuildNum < srvrMinBuildNum);
	}

	public boolean isUnderCurrentBuildNum(Context context){
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		int srvrMinBuildNum = spData.getInt("MinBuildNum", 0);
		int srvrCurrentBuildNum = spData.getInt("CurrentBuildNum", 0);
		int localBuildNum = Utils.getVersionCode(context);
		return (localBuildNum < srvrCurrentBuildNum);
	}

	
	public boolean getChargerConnectivityMode(Context context) {
		SharedPreferences spData = context.getSharedPreferences("Settings",Context.MODE_MULTI_PROCESS);
		return spData.getBoolean("ChargerConnectivityMode", SettingsActivity.PREF_MINING_CHARGER_CONNECTIVITY_DEFAULT);
	}
	
	public boolean setChargerConnectivityMode(Context context, boolean mode) {
		SharedPreferences spData = context.getSharedPreferences("Settings",Context.MODE_MULTI_PROCESS);
		return spData.edit().putBoolean("ChargerConnectivityMode", mode).commit();
	}

	public boolean getMiningEnabledMode(Context context) {
		SharedPreferences spData = context.getSharedPreferences("Settings",Context.MODE_MULTI_PROCESS);
		return spData.getBoolean("MiningEnabledMode", SettingsActivity.PREF_MINING_MINING_ENAB_DIS_DEFAULT);
	}
	
	public boolean getSoundEnabledMode(Context context) {
		SharedPreferences spData = context.getSharedPreferences("Settings",Context.MODE_MULTI_PROCESS);
		return spData.getBoolean("SoundEnabledMode", SettingsActivity.PREF_SOUND_GESTERS_ENAB_DIS_DEFAULT);
	}
	
	public boolean setMiningEnabledMode(Context context, boolean mode) {
		SharedPreferences spData = context.getSharedPreferences("Settings",Context.MODE_MULTI_PROCESS);
		return spData.edit().putBoolean("MiningEnabledMode", mode).commit();
	}
	
	public boolean setSoundEnabledMode(Context context, boolean mode) {
		SharedPreferences spData = context.getSharedPreferences("Settings",Context.MODE_MULTI_PROCESS);
		return spData.edit().putBoolean("SoundEnabledMode", mode).commit();
	}

	public void activateGPSReminder(Context context, boolean isDay, boolean isWeek, boolean isNever) {
		SharedPreferences spData = context.getSharedPreferences("GPSReminder",Context.MODE_MULTI_PROCESS);
		Editor editor = spData.edit();
		
		if (isDay)
			editor.putLong("reminderTime", System.currentTimeMillis() + 24*60*60*1000); // 1 day
			//editor.putLong("reminderTime", System.currentTimeMillis() + 60*1000); // for testing
		else if (isWeek)
			editor.putLong("reminderTime", System.currentTimeMillis() + 7*24*60*60*1000); // 7 days
			//editor.putLong("reminderTime", System.currentTimeMillis() + 5*60*1000); // for testing
		else if (isNever)
			editor.putLong("reminderTime", System.currentTimeMillis() + 365*24*60*60*1000); // one year
		
		editor.commit();
	}
	
	public boolean hasTimePassedForGPSReminder(Context context){
		SharedPreferences spData = context.getSharedPreferences("GPSReminder",Context.MODE_MULTI_PROCESS);
		long reminderTime = spData.getLong("reminderTime", 0);
		return (System.currentTimeMillis() > reminderTime);
	}
	
	public boolean hasNetworkLocationReminderDisplayed(Context context){
		SharedPreferences spData = context.getSharedPreferences("GPSReminder",Context.MODE_MULTI_PROCESS);
		return spData.getBoolean("NetworkLocationReminderDisplayed", false);
	}
	
	public void setNetworkLocationReminderDisplay(Context context){
		SharedPreferences spData = context.getSharedPreferences("GPSReminder",Context.MODE_MULTI_PROCESS);
		spData.edit().putBoolean("NetworkLocationReminderDisplayed", true).commit();
	}

	public boolean getShowMyLocationSettings(Context context) {

		
		return false;
	}
	
}
