package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lazooz.lbm.R;
import com.lazooz.lbm.utils.Utils;

public class StatsDataMiners {

	private int mMonth;
	private int mCount;
	

	public StatsDataMiners() {
	}

	
	public StatsDataMiners(JSONObject jsonObj){
		try {
			mMonth = jsonObj.getInt("month");
			mCount = jsonObj.getInt("count");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
			retObj.put("month", mMonth);
			retObj.put("count", mCount);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	public int getMonth() {
		return mMonth;
	}

	public String getMonth(Context context) {
		String[] monthArray = context.getResources().getStringArray(R.array.months_array);
		return monthArray[mMonth];
	}

	public void setMonth(int month) {
		mMonth = month;
	}


	public int getCount() {
		return mCount;
	}


	public void setCount(int count) {
		mCount = count;
	}


	
	
	
}
