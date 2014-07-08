package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothData {

	private String mUUID;
	private String mAddress;

	public BluetoothData(JSONObject jsonObj) {
		try {
			mUUID = jsonObj.getString("bt_uuid");
			mAddress = jsonObj.getString("bt_address");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		try {
			retObj.put("bt_uuid", mUUID);
			retObj.put("bt_address", mAddress);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	public String getUUID() {
		return mUUID;
	}


	public void setUUID(String uUID) {
		mUUID = uUID;
	}


	public String getAddress() {
		return mAddress;
	}


	public void setAddress(String address) {
		mAddress = address;
	}
}
