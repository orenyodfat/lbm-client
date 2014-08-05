package com.lazooz.lbm.businessClasses;

public class ServerData {
	private String mDistance;
	private String mZoozBalance;
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

	public void setZoozBalance(String zoozBalance) {
		mZoozBalance = zoozBalance;
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
