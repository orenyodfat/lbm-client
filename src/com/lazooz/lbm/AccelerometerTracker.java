package com.lazooz.lbm;



import java.util.Timer;
import java.util.TimerTask;

import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class AccelerometerTracker implements SensorEventListener {

	public interface AccelerometerListener {
	    public void onAccelerationChanged(float x, float y, float z);
	    public void onShake(float force);
	}

	private AccelerometerListener mListener;
	
	private static float threshold  = 15.0f; 
	private static int interval     = 200;

	private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
	private long mShakeTimestamp;
	private long mNowTimestamp;
	private int mShakeCount;
	
	private SensorManager mSensorManager;
	private Sensor mSensorAccelerometer;
    
	private long now = 0;
    private long timeDiff = 0;
    private long lastUpdate = 0;
    private long lastShake = 0;

    private float x = 0;
    private float y = 0;
    private float z = 0;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;
    private float force = 0;
	
    private Context mContext;
    private Timer ShortPeriodTimer;
    
	public AccelerometerTracker(Context context) {
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(context));
		
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
/*
        ShortPeriodTimer = new Timer();
		TimerTask twoSecondsTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryShortPeriod();				
				}
			};
		ShortPeriodTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 1000);
		ShortPeriodTimer.cancel();
*/
        
	}


	
	public void release(){ 
		mSensorManager.unregisterListener(this);
	}


	protected void checkEveryShortPeriod() {
		
	}






	@Override
	public void onSensorChanged(SensorEvent event) {
		// use the event timestamp as reference
        // so the manager precision won't depends 
        // on the AccelerometerListener implementation
        // processing time
        now = event.timestamp;

        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        
        if (lastUpdate == 0) {
            lastUpdate = now;
            lastShake = now;
            lastX = x;
            lastY = y;
            lastZ = z;
            //Toast.makeText(mContext,"No Motion detected", Toast.LENGTH_SHORT).show();
             
        } 
        else {
            timeDiff = now - lastUpdate;
            
            if (timeDiff > 0) { 
                
                /*force = Math.abs(x + y + z - lastX - lastY - lastZ) 
                            / timeDiff;*/
                force = Math.abs(x + y + z - lastX - lastY - lastZ);
                 
                if (Float.compare(force, threshold) >0 ) {
                    //Toast.makeText(Accelerometer.getContext(), 
                    //(now-lastShake)+"  >= "+interval, 1000).show();
                     
                    if (now - lastShake >= interval) { 
                        
                    	checkSomeShakeInASec();
                    	
                        // trigger shake event
                    	//if (mListener != null)
                    		//mListener.onShake(force);
                    }
                    else
                    {
                        //Toast.makeText(mContext,"No Motion detected", Toast.LENGTH_SHORT).show();
                         
                    }
                    lastShake = now;
                }
                lastX = x;
                lastY = y;
                lastZ = z;
                lastUpdate = now; 
            }
            else
            {
                //Toast.makeText(mContext,"No Motion detected", Toast.LENGTH_SHORT).show();
                 
            }
        }
        // trigger change event
        if (mListener != null)
        	mListener.onAccelerationChanged(x, y, z);
    }


        
        
		


	private void checkSomeShakeInASec() {
		mNowTimestamp = System.currentTimeMillis();
		
		if(mShakeTimestamp == 0){
			Log.e("SHAKE", "mShakeTimestamp == 0");
			mShakeTimestamp = mNowTimestamp;
			mShakeCount++;
		}
		else{
			if (mNowTimestamp - mShakeTimestamp < 1500){
				Log.e("SHAKE", "mNowTimestamp-mShakeTimestamp < 1500");
				mShakeCount++;
				Log.e("SHAKE", "mShakeCount "+ mShakeCount);
				if (mShakeCount > 5){
					Log.e("SHAKE", "real shake");
					if (mListener != null)
                		mListener.onShake(force);
					mShakeTimestamp = 0;
					mShakeCount = 0;
				}
			}
			else{
				Log.e("SHAKE", "now-mShakeTimestamp > 1000");
				Log.e("SHAKE", "now-mShakeTimestamp-"+(now-mShakeTimestamp) +"");
				mShakeTimestamp = 0;
				mShakeCount = 0;
			}
		}
		
	}






	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}






	public AccelerometerListener getListener() {
		return mListener;
	}






	public void setListener(AccelerometerListener listener) {
		mListener = listener;
	}
}
