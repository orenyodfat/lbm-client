package com.lazooz.lbm.utils;


import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class OfflineActivities {

	
	private static OfflineActivities instance = null;
	
	private Context mContext;
	
	public static OfflineActivities getInstance(Context context) {
	      if(instance == null) {
	         instance = new OfflineActivities(context);
	      }
	      return instance;
	   }
	
	public static void removeInstance() {
	      instance = null;
	   }
	
	public OfflineActivities(Context context){    // constructor
		 mContext = context;
	}
	

	
	public void setExceptionData(String theException){
		
		try {
			SharedPreferences exepData = mContext.getSharedPreferences("offlineException", Context.MODE_MULTI_PROCESS);
			SharedPreferences.Editor editor = exepData.edit();
			editor.putString("theException", theException);
			editor.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void clearExceptionData(){
		try {
			SharedPreferences exepData = mContext.getSharedPreferences("offlineException", Context.MODE_MULTI_PROCESS);
			SharedPreferences.Editor editor = exepData.edit();
			editor.clear();
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public void transmitDataToServer(){
			SharedPreferences offlineExpSP = mContext.getSharedPreferences("offlineException", Context.MODE_MULTI_PROCESS);

			String theExp = offlineExpSP.getString("theException", "");
			if(theExp.equals(""))
				return;

			
			OfflineException offlineExp = new OfflineException();
			offlineExp.execute(theExp);
	}
	
	
	
		
		
	private class OfflineException extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
				String serverMessage = "";
				
				try {
		          	String excepData = params[0];
		          	
			    	ServerCom bServerCom = new ServerCom(mContext);

			    	String vercode = "";
					try {
						int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
						vercode = Integer.toString(versionCode);
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					MySharedPreferences msp = MySharedPreferences.getInstance();
					
					bServerCom.setClientException(msp.getUserId(mContext), msp.getUserSecret(mContext), "VersionCode=" + vercode + "  +++++++ " + excepData);
					  
					JSONObject jsonReturnObj = bServerCom.getReturnObject();
					serverMessage = "";
					
					try {
						if (jsonReturnObj != null)
							serverMessage = jsonReturnObj.getString("message");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return serverMessage;
			}
			
			
			
			@Override
		    protected void onPostExecute(String result) {
				
				try {
					if (result.equals("success")){
						clearExceptionData();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
		    protected void onPreExecute() {
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		
	}
	
	
	
	
	
}
