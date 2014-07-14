package com.lazooz.lbm.preference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.CongratulationsRegActivity;
import com.lazooz.lbm.IntroActivity;
import com.lazooz.lbm.MainActivity;
import com.lazooz.lbm.MapShowLocationActivity;
import com.lazooz.lbm.MissionDrive100Activity;
import com.lazooz.lbm.RegistrationActivity;
import com.lazooz.lbm.businessClasses.Contact;
import com.lazooz.lbm.businessClasses.LocationData;
import com.lazooz.lbm.businessClasses.ServerData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
	public final static int STAGE_DRIVE100 = 9;
	public final static int STAGE_MAIN_NO_DRIVE100 = 10;
	public final static int STAGE_DRIVE100_CONGRATS = 11;
	public final static int STAGE_MAIN_NO_GET_FRIENDS = 12;
	public final static int STAGE_GET_FRIENDS_CONGRATS = 13;
	public final static int STAGE_MAIN = 14;
	
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
			return MissionDrive100Activity.class;
		case STAGE_DRIVE100:
			return MainActivity.class;
		case STAGE_MAIN_NO_DRIVE100:
			return MainActivity.class;
		case STAGE_DRIVE100_CONGRATS:
			return MainActivity.class;
		case STAGE_MAIN_NO_GET_FRIENDS:
			return MainActivity.class;
		case STAGE_GET_FRIENDS_CONGRATS:
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
	
	
	public void saveLocationData(Context context, LocationData ld){
		SharedPreferences spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);
		int writeCursor = spData.getInt("WriteCursor", 0);
		writeCursor++;
		
		Editor editor = spData.edit();
		
		editor.putString("LocationDataList_" + writeCursor, ld.toJSON().toString());
		editor.putInt("WriteCursor", writeCursor);
		editor.commit();
	}

	
	public JSONArray getLocationDataList(Context context){
		SharedPreferences spData = context.getSharedPreferences("LocationData",Context.MODE_MULTI_PROCESS);
		int writeCursor = spData.getInt("WriteCursor", 1);
		int commitedReadCursor = spData.getInt("CommitedReadCursor", 1);
		JSONArray jsArray = new JSONArray();
		try {
			Log.e("MSP", "read from: " + commitedReadCursor + " to: " + writeCursor);
			for (int i=commitedReadCursor; i<=writeCursor; i++){
				String locationDataString = spData.getString("LocationDataList_" + i, "");
				JSONObject obj = new JSONObject(locationDataString);
				jsArray.put(obj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		spData.edit().putInt("ReadCursor", writeCursor).commit();
		
		return jsArray;
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
		sd.setDistance(spData.getString("Distance", "0.0"));
		sd.setIsDistanceAchievement(spData.getBoolean("IsDistanceAchievement", false));
		
		return sd;
	}
	
	
	
	public void saveDataFromServer(Context context, String zoozBalance, String distance, boolean isDistanceAchievement) {
		SharedPreferences spData = context.getSharedPreferences("ServerData",Context.MODE_MULTI_PROCESS);
		Editor editor = spData.edit();
		editor.putString("ZoozBalance", zoozBalance);
		editor.putString("Distance", distance);
		editor.putBoolean("IsDistanceAchievement", isDistanceAchievement);
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

	
}
