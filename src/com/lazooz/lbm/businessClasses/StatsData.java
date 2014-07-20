package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.utils.Utils;

public class StatsData {

	private String mTime;
	private String mDistance;
	private String mZooz;
	

	public StatsData() {
	}

	
	public StatsData(JSONObject jsonObj){
		try {
			mTime = jsonObj.getString("time");
			mDistance = jsonObj.getString("distance");
			mZooz = jsonObj.getString("zooz");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
			retObj.put("time", mTime);
			retObj.put("distance", mDistance);
			retObj.put("zooz", mZooz);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	public String getTime() {
		return mTime;
	}


	public void setTime(String time) {
		mTime = time;
	}


	public String getDistance() {
		return mDistance;
	}


	public void setDistance(String distance) {
		mDistance = distance;
	}


	public String getZooz() {
		return mZooz;
	}


	public void setZooz(String zooz) {
		mZooz = zooz;
	}

	
	
	
	
	
}
