package com.lazooz.lbm.businessClasses;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;



public class TelephonyDataTracker {

	private OnTelephonyDataListener mOnTelephonyDataListener;
	private Timer mTimer;
	private TelephonyManager mTelephonyManager;
	private int mLastCid = 0;

	public TelephonyDataTracker(Context context){
		mTelephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	
	public interface OnTelephonyDataListener {
        public void onCellChanged(int newCellID);
    }
	
	
	public OnTelephonyDataListener getOnTelephonyDataListener() {
		return mOnTelephonyDataListener;
	}

	public void setOnTelephonyDataListener(OnTelephonyDataListener onTelephonyDataListener) {
		this.mOnTelephonyDataListener = onTelephonyDataListener;
	}
	
	
	
	public void requestCellUpdates(OnTelephonyDataListener onTelephonyDataListener){
		this.mOnTelephonyDataListener = onTelephonyDataListener;
		if (mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
		
		mTimer = new Timer();
		TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryPeriod();				
				}
			};
			mTimer.scheduleAtFixedRate(timerTask, 1000, 10*1000);

	}
	
	
	
	protected void checkEveryPeriod() {
		
		GsmCellLocation cellLocation = (GsmCellLocation)mTelephonyManager.getCellLocation();
		
		if (cellLocation != null){
			int cellId = cellLocation.getCid();
			if(mLastCid  != cellId){
				mLastCid = cellId;
				mOnTelephonyDataListener.onCellChanged(mLastCid);
			}
			
		}
	}

	public void removeUpdates(){
		if (mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
	}
}
