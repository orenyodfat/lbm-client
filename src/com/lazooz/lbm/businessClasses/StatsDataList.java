package com.lazooz.lbm.businessClasses;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class StatsDataList {
	private List<StatsData> mList;
	private double[] distDoubleArray;
	private double[] zoozDoubleArray;
	private double minValZooz, maxValZooz;
	private double minValDist, maxValDist;

	
	public StatsDataList(JSONArray arrayList) {
	
		mList = new ArrayList<StatsData>();
		
		for (int i=0; i<arrayList.length(); i++) {
		    try {
		    	JSONObject obj = (JSONObject)arrayList.get(i);
		    	StatsData sd = new StatsData(obj);
		    	mList.add(sd);
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
		
		minValZooz = 0;
		maxValZooz = 0;
		minValDist = 0;
		maxValDist = 0;
		double dist;
		double zooz;
		distDoubleArray = new double[mList.size()];
		zoozDoubleArray = new double[mList.size()];
		int i=0;
		for(StatsData point : mList){
			dist = Double.valueOf(point.getDistance());
			zooz = Double.valueOf(point.getZooz());
			distDoubleArray[i++] = dist;
			zoozDoubleArray[i++] = zooz;
			minValZooz = Math.min(minValZooz, dist);
			maxValZooz = Math.max(maxValZooz, dist);
			minValDist = Math.min(minValDist, dist);
			maxValDist = Math.max(maxValDist, dist);
		}

		
		
		
	}

	public List<StatsData> getStatsDataList() {
		return mList;
	}

	public double getMinValZooz() {
		return minValZooz;
	}

	public void setMinValZooz(double minValZooz) {
		this.minValZooz = minValZooz;
	}

	public double getMaxValZooz() {
		return maxValZooz;
	}

	public void setMaxValZooz(double maxValZooz) {
		this.maxValZooz = maxValZooz;
	}

	public double getMinValDist() {
		return minValDist;
	}

	public void setMinValDist(double minValDist) {
		this.minValDist = minValDist;
	}

	public double getMaxValDist() {
		return maxValDist;
	}

	public void setMaxValDist(double maxValDist) {
		this.maxValDist = maxValDist;
	}

	public double[] getDistDoubleArray() {
		return distDoubleArray;
	}

	public double[] getZoozDoubleArray() {
		return zoozDoubleArray;
	}

	public List<StatsData> getList() {
		return mList;
	}

	


}
