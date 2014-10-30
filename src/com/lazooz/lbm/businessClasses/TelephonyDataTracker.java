package com.lazooz.lbm.businessClasses;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellInfoGsm;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;



public class TelephonyDataTracker {

	private static OnTelephonyDataListener mOnTelephonyDataListener;
	private Timer mTimer;
	private TelephonyManager mTelephonyManager;
	private static int mLastCid = 0xFFFF;

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
		GsmCellLocation gsmCellLocation;
		CdmaCellLocation cdmaCellLocation;
		int CallState = mTelephonyManager.getCallState();
		int  DataState = mTelephonyManager.getDataState();

		try {
			CellLocation cellLocation = (CellLocation)mTelephonyManager.getCellLocation();
			if (cellLocation != null){
				int cellId = 0;
				int level;
				
				if(cellLocation instanceof GsmCellLocation) {
					gsmCellLocation = (GsmCellLocation)cellLocation;
					cellId = gsmCellLocation.getCid();
					// for example value of first element
		
				}
				else if(cellLocation instanceof CdmaCellLocation) {
					cdmaCellLocation = (CdmaCellLocation)cellLocation;
					cellId = cdmaCellLocation.getBaseStationId();
				}
                cellId &=0xFFFF;
                
                if (mLastCid == 0xFFFF) /*First Time*/
				{
                	 mLastCid = cellId;
				}
                else if ((cellId!=-1)&& (mLastCid  != cellId)){
					
					 mLastCid = cellId;
					 Message msg = handler.obtainMessage();
				     handler.sendMessage(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 private final static Handler handler = new Handler() {
	        public void handleMessage(Message msg) {
            	  try {
            		  mOnTelephonyDataListener.onCellChanged(mLastCid);
            	  }catch (Exception e) {
            		  e.printStackTrace();
            	  }
	        }
	 };
	
	public void removeUpdates(){
		if (mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
	}
}
