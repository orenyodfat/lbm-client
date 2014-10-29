package com.lazooz.lbm.businessClasses;

import android.content.Context;

import com.lazooz.lbm.R;
import com.lazooz.lbm.utils.Utils;

public class ServerData {
	private String mDistance;
	private String mZoozBalance;
	private String mPotZoozBalance;
	private boolean mIsDistanceAchievement;
	private long mTimeStamp;
	
	public ServerData() {
		// TODO Auto-generated constructor stub
	}

	public String getDistance() {
		return mDistance;
	}

	public float getDistanceFloat() {
		return Float.valueOf(mDistance);
	}

	public void setDistance(String distance) {
		mDistance = distance;
	}

	public String getZoozBalance() {
		return mZoozBalance;
	}

	public String getPotentialZoozBalance() {
		return mPotZoozBalance;
	}
	
	public void setZoozBalance(String zoozBalance) {
		mZoozBalance = zoozBalance;
	}
	public void setPotentialZoozBalance(String potZoozBalance) {
	
		mPotZoozBalance = potZoozBalance; 
	}
	public boolean isDistanceAchievement() {
		return mIsDistanceAchievement;
	}

	public void setIsDistanceAchievement(boolean isDistanceAchievement) {
		mIsDistanceAchievement = isDistanceAchievement;
	}

	public long getTimeStamp() {
		return mTimeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		mTimeStamp = timeStamp;
	}

}
