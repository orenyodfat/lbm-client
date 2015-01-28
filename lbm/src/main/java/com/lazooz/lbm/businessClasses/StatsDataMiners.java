package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lazooz.lbm.R;
import com.lazooz.lbm.utils.Utils;

public class StatsDataMiners {

	private int mDay;
	private int mCount;
	

	public StatsDataMiners() {
	}

	
	public StatsDataMiners(JSONObject jsonObj){
		try {
			mDay = jsonObj.getInt("day");
			mCount = jsonObj.getInt("count");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
			retObj.put("day", mDay);
			retObj.put("count", mCount);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	public int getDay() {
		return mDay;
	}
/*
	public String getDay(Context context) {
		String[] dayArray = context.getResources().getStringArray(R.array.months_array);
		return dayArray[mDay];
	}*/

	public void setMonth(int day) {
		mDay = day;
	}


	public int getCount() {
		return mCount;
	}


	public void setCount(int count) {
		mCount = count;
	}


	
	
	
}
