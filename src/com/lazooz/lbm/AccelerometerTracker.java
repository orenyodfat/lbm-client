package com.lazooz.lbm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class AccelerometerTracker implements SensorEventListener {

	public interface AccelerometerListener {
	    public void onAccelerationChanged(float x, float y, float z);
	    public void onShake(float force);
	}

	private AccelerometerListener mListener;
	
	private static float threshold  = 15.0f; 
	private static int interval     = 200;
	
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
	
	public AccelerometerTracker(Context context) {
		mContext = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        

        
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
            Toast.makeText(mContext,"No Motion detected", Toast.LENGTH_SHORT).show();
             
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
                         
                        // trigger shake event
                    	if (mListener != null)
                    		mListener.onShake(force);
                    }
                    else
                    {
                        Toast.makeText(mContext,"No Motion detected", 
                            Toast.LENGTH_SHORT).show();
                         
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
                Toast.makeText(mContext,"No Motion detected", Toast.LENGTH_SHORT).show();
                 
            }
        }
        // trigger change event
        if (mListener != null)
        	mListener.onAccelerationChanged(x, y, z);
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
