package com.lazooz.lbm.businessClasses;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class StatsDataMinersDistMonthList {
	private List<StatsDataMinersDistMonth> mList;
	private double[] dataDoubleArray;
	private double[] XDoubleArray;
	private double minVal, maxVal;

	
	public StatsDataMinersDistMonthList(JSONArray arrayList) {
	
		mList = new ArrayList<StatsDataMinersDistMonth>();
		
		for (int i=0; i<arrayList.length(); i++) {
		    try {
		    	JSONObject obj = (JSONObject)arrayList.get(i);
		    	StatsDataMinersDistMonth sd = new StatsDataMinersDistMonth(obj);
		    	mList.add(sd);
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
		
		minVal = 0;
		maxVal = 0;
		double data;
		dataDoubleArray = new double[mList.size()];
		XDoubleArray = new double[mList.size()];
		int i=0; int j=0;int k=0; 
		for(StatsDataMinersDistMonth point : mList){
			data = Double.valueOf(point.getDistance());
			dataDoubleArray[i++] = data;
			XDoubleArray[k++] = i;
			minVal = Math.min(minVal, data);
			maxVal = Math.max(maxVal, data);
		}

		
		
		
	}

	public List<StatsDataMinersDistMonth> getStatsDataList() {
		return mList;
	}

	public double getMinVal() {
		return minVal;
	}

	public void setMinVal(double minVal) {
		this.minVal = minVal;
	}

	public double getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(double maxVal) {
		this.maxVal = maxVal;
	}


	public double[] getDataDoubleArray() {
		return dataDoubleArray;
	}

	public List<StatsDataMinersDistMonth> getList() {
		return mList;
	}

	public double[] getXDoubleArray() {
		return XDoubleArray;
	}

	


}
