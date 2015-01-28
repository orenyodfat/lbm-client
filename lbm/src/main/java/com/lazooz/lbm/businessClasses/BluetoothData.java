package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothData {

	private String mAddress;
	private String mName;

	public BluetoothData() {
		
	}
	public BluetoothData(JSONObject jsonObj) {
		try {
			mAddress = jsonObj.getString("bt_address");
			mName = jsonObj.getString("bt_name");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		try {
			retObj.put("bt_address", mAddress);
			retObj.put("bt_name", mName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	


	public String getAddress() {
		return mAddress;
	}


	public void setAddress(String address) {
		mAddress = address;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
}
