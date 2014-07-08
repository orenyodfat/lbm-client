package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class WifiData {

	private String mName;
	private double mStrength;
	private String mSSID;

	public WifiData(JSONObject jsonObj) {
		try {
			mName = jsonObj.getString("wifi_name");
			mStrength = jsonObj.getDouble("wifi_strength");
			mSSID = jsonObj.getString("wifi_ssid");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		try {
			retObj.put("wifi_name", mName);
			retObj.put("wifi_strength", mStrength);
			retObj.put("wifi_ssid", mSSID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	public String getName() {
		return mName;
	}


	public void setName(String name) {
		mName = name;
	}


	public double getStrength() {
		return mStrength;
	}


	public void setStrength(double strength) {
		mStrength = strength;
	}


	public String getSSID() {
		return mSSID;
	}


	public void setSSID(String sSID) {
		mSSID = sSID;
	}
}
