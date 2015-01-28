package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lazooz.lbm.R;
import com.lazooz.lbm.utils.Utils;

public class StatsDataMinersDistMonth {

	private int mMonth;
	private int mDistance;
	

	public StatsDataMinersDistMonth() {
	}

	
	public StatsDataMinersDistMonth(JSONObject jsonObj){
		try {
			mMonth = jsonObj.getInt("month");
			mDistance = jsonObj.getInt("distance");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
			retObj.put("month", mMonth);
			retObj.put("distance", mDistance);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	

	public String getMonth(Context context) {
		String[] monthArray = context.getResources().getStringArray(R.array.months_array);
		return monthArray[mMonth];
	}

	


	public int getMonth() {
		return mMonth;
	}


	public void setMonth(int month) {
		mMonth = month;
	}


	public int getDistance() {
		return mDistance;
	}


	public void setDistance(int distance) {
		mDistance = distance;
	}


	
	
	
}
