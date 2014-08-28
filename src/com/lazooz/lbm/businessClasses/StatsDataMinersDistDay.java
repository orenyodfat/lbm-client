package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lazooz.lbm.R;
import com.lazooz.lbm.utils.Utils;

public class StatsDataMinersDistDay {

	private int mDay;
	private int mDistance;
	

	public StatsDataMinersDistDay() {
	}

	
	public StatsDataMinersDistDay(JSONObject jsonObj){
		try {
			mDay = jsonObj.getInt("day");
			mDistance = jsonObj.getInt("distance");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
			retObj.put("day", mDay);
			retObj.put("distance", mDistance);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	

	public String getDayInWeek(Context context) {
		String[] dayArray = context.getResources().getStringArray(R.array.days_array);
		return dayArray[mDay];
	}

	public String getDayInMonth(Context context) {
		return mDay + "";
	}
	


	public int getDay() {
		return mDay;
	}


	public void setDay(int day) {
		mDay = day;
	}


	public int getDistance() {
		return mDistance;
	}


	public void setDistance(int distance) {
		mDistance = distance;
	}


	
	
	
}
