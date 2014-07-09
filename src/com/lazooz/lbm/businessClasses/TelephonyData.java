package com.lazooz.lbm.businessClasses;

public class TelephonyData {

	private String mMMC;
	private String mMNC;
	private int mCID;
	private int mLAC;
	
	public TelephonyData() {
		mCID = 0;
		mLAC = 0;
	}

	public String getMMC() {
		return mMMC;
	}

	public void setMMC(String mMC) {
		mMMC = mMC;
	}

	public String getMNC() {
		return mMNC;
	}

	public void setMNC(String mNC) {
		mMNC = mNC;
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

}
