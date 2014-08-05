package com.lazooz.lbm.businessClasses;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.lazooz.lbm.utils.Utils;

public class LocationData {

	private double mLatitude; 
	private double mLongitude;
	private long mTimestamp;
	private double mAccuracy;
	
	private String mMCC;
	private String mMNC;
	private int mCID;
	private int mLAC;
	
	private boolean mHasWifiData;
	private ArrayList<WifiData> mWifiDataList;
	private boolean mHasLocationData;
	private boolean mHasBluetoothData;
	private ArrayList<BluetoothData> mBluetoothDataList;
	
	public LocationData() {
		mWifiDataList = new ArrayList<WifiData>();
		mBluetoothDataList = new ArrayList<BluetoothData>();
		mHasWifiData = false;
		mHasBluetoothData = false;
		mHasLocationData = false;
	}
	
	public LocationData(JSONObject jsonObj) {
		try {
			JSONArray ja;
			mWifiDataList = null;
			mBluetoothDataList = null;
			
			mLatitude = jsonObj.getDouble("lat");
			mLongitude = jsonObj.getDouble("long");
			mTimestamp = jsonObj.getLong("location_time");
			mAccuracy = jsonObj.getDouble("location_accuracy");		
			mMCC = jsonObj.getString("mcc");
			mMNC = jsonObj.getString("mnc");
			mCID = jsonObj.getInt("cid");
			mLAC = jsonObj.getInt("lac");
			mHasWifiData = Utils.yesNoToBoolean(jsonObj.getString("is_wifi"));
			mHasLocationData = Utils.yesNoToBoolean(jsonObj.getString("is_location"));
			mHasBluetoothData = Utils.yesNoToBoolean(jsonObj.getString("is_bt"));

			if((mHasWifiData) && (jsonObj.has("wifi_obj_list"))){
				ja = jsonObj.getJSONArray("wifi_obj_list");
				mWifiDataList = new ArrayList<WifiData>();
				
				for (int i=0; i<ja.length(); i++){
					JSONObject jo = (JSONObject)ja.get(i);
					WifiData wd = new WifiData(jo);
					mWifiDataList.add(wd);
				}
			}
				
			if((mHasBluetoothData) && (jsonObj.has("bt_obj_list"))){
				ja = jsonObj.getJSONArray("bt_obj_list");
				mBluetoothDataList = new ArrayList<BluetoothData>();
				
				for (int i=0; i<ja.length(); i++){
					JSONObject jo = (JSONObject)ja.get(i);
					BluetoothData bd = new BluetoothData(jo);
					mBluetoothDataList.add(bd);
				}
			}
				
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		JSONObject obj; 
 		int size,i;
 		JSONArray jsArray;
		try {
			retObj.put("lat", mLatitude);
			retObj.put("long", mLongitude);
			retObj.put("location_time", mTimestamp);
			retObj.put("location_accuracy", mAccuracy);
			retObj.put("mcc", mMCC);
			retObj.put("mnc", mMNC);
			retObj.put("cid", mCID);
			retObj.put("lac", mLAC);
			retObj.put("is_wifi", Utils.booleanToYesNo(mHasWifiData));
			retObj.put("is_bt", Utils.booleanToYesNo(mHasBluetoothData));
			retObj.put("is_location", Utils.booleanToYesNo(mHasLocationData));
			

 			if (mHasWifiData && (mWifiDataList != null)){
 				jsArray = new JSONArray();
 				size = mWifiDataList.size();
 				for(i=0; i<size;i++){
 					obj = mWifiDataList.get(i).toJSON();
 					jsArray.put(obj);
 				}
 				retObj.put("wifi_obj_list", jsArray);
 			}
 			
 			if (mHasBluetoothData && (mBluetoothDataList != null)){
 				jsArray = new JSONArray();
 				size = mBluetoothDataList.size();
 				for(i=0; i<size;i++){
 					obj = mBluetoothDataList.get(i).toJSON();
 					jsArray.put(obj);
 				}
 				retObj.put("bt_obj_list", jsArray);
 			}
 			
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}
	
	
	
	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}

	public double getAccuracy() {
		return mAccuracy;
	}

	public void setAccuracy(double accuracy) {
		mAccuracy = accuracy;
	}

	public String getMCC() {
		return mMCC;
	}

	public void setMCC(String val) {
		mMCC = val;
	}

	public String getMNC() {
		return mMNC;
	}

	public void setMNC(String val) {
		mMNC = val;
	}

	public int getCID() {
		return mCID;
	}

	public void setCID(int cID) {
		mCID = cID;
	}

	public int getLAC() {
		return mLAC;
	}

	public void setLAC(int lAC) {
		mLAC = lAC;
	}

	public boolean hasWifiData() {
		return mHasWifiData;
	}

	public void setHasWifiData(boolean hasWifiData) {
		mHasWifiData = hasWifiData;
	}

	

	public boolean hasBluetoothData() {
		return mHasBluetoothData;
	}

	public void setHasBluetoothData(boolean hasBluetoothData) {
		mHasBluetoothData = hasBluetoothData;
	}

	

	public ArrayList<WifiData> getWifiDataList() {
		return mWifiDataList;
	}

	public void setWifiDataList(ArrayList<WifiData> wifiDataList) {
		mWifiDataList = wifiDataList;
	}

	public ArrayList<BluetoothData> getBluetoothDataList() {
		return mBluetoothDataList;
	}

	public void setBluetoothDataList(ArrayList<BluetoothData> bluetoothDataList) {
		mBluetoothDataList = bluetoothDataList;
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long timestamp) {
		mTimestamp = timestamp;
	}

	public void setTelephonyData(TelephonyData td) {
		mMCC = td.getMMC();
		mMNC = td.getMNC();
		mCID = td.getCID();
		mLAC = td.getLAC();
	}

	public boolean hasLocationData() {
		return mHasLocationData;
	}

	public void setHasLocationData(boolean hasLocationData) {
		mHasLocationData = hasLocationData;
	}

	public float distanceBetween(LocationData locationData){
		float[] results = null;
		try {
			Location.distanceBetween(locationData.getLatitude(), locationData.getLongitude(), getLatitude(), getLongitude(), results);
			return results[0];
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
}
