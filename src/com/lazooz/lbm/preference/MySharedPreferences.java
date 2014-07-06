package com.lazooz.lbm.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
	private static MySharedPreferences instance = null;
	private String mProductId;
	private String mToken;

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
	
	
	
	
	public void saveActivationData(Context context)
	{
		
		SharedPreferences spData = context.getSharedPreferences("AppData",Context.MODE_MULTI_PROCESS);
	    SharedPreferences.Editor editor = spData.edit();
	    
	    editor.putString("productId", mProductId);
	    editor.putString("token", mToken);
	    	    
	    editor.commit();
		
	}
	
	
	public String getProductId() {
		return mProductId;
	}

	public void setProductId(String productId) {
		this.mProductId = productId;
	}

	public String getToken() {
		return mToken;
	}



	public void setToken(String token) {
		this.mToken = token;
	}


	
}
