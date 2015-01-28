package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class WifiData {

	private String mBSSID;
	private String mSSID;
	private String mCapabilities;
	private int mFrequency;

	public WifiData() {
		
	}
	
	public WifiData(JSONObject jsonObj) {
		try {
			mBSSID = jsonObj.getString("wifi_bssid");
			mSSID = jsonObj.getString("wifi_ssid");
			mCapabilities = jsonObj.getString("wifi_capabilities");
			mFrequency = jsonObj.getInt("wifi_frequency");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		try {
			retObj.put("wifi_bssid", mBSSID);
			retObj.put("wifi_ssid", mSSID);
			retObj.put("wifi_capabilities", mCapabilities);
			retObj.put("wifi_frequency", mFrequency);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	




	public String getSSID() {
		return mSSID;
	}


	public void setSSID(String sSID) {
		mSSID = sSID;
	}

	public String getBSSID() {
		return mBSSID;
	}

	public void setBSSID(String bSSID) {
		mBSSID = bSSID;
	}

	public String getCapabilities() {
		return mCapabilities;
	}

	public void setCapabilities(String capabilities) {
		mCapabilities = capabilities;
	}

	public int getFrequency() {
		return mFrequency;
	}

	public void setFrequency(int frequency) {
		mFrequency = frequency;
	}
}
