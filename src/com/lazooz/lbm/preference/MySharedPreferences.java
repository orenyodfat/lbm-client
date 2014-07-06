package com.lazooz.lbm.preference;

import android.content.Context;
import android.content.SharedPreferences;

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

	
}
