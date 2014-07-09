package com.lazooz.lbm.preference;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.businessClasses.LocationData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class MySharedPreferences {
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
	
	
	
	
}
