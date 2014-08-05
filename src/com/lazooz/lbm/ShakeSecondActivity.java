package com.lazooz.lbm;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lazooz.lbm.utils.Utils;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.os.Bundle;

public class ShakeSecondActivity extends ActionBarActivity {

	private GoogleMap map;
	private TextView mMainTextTV;
	private GPSTracker mGPSTracker;
	private Marker mLastMarker;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	private AccelerometerTracker mAccelerometerTracker;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake_second);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_shake);
		map = mapFragment.getMap();
		
		map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			
			@Override
			public void onMyLocationChange(Location location) {
				if (location != null){
					setMapLocation(location);
				}
				
			}
		});
		
		
		//map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		
		
		if (map!=null){
			MarkerOptions mo = new MarkerOptions();
			mGPSTracker = GPSTracker.getInstance(getApplicationContext());
			mGPSTracker.setOnLocationListener(new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLocationChanged(Location location) {
					
						
				
					
				}
			});
			
			setMapLocation(mGPSTracker.getLocation());
		    
		 }
		
		
		
		initShakeDetector();
		
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		initShakeDetector();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mAccelerometerTracker != null){
			mAccelerometerTracker.release();
			mAccelerometerTracker.setListener(null);
			mAccelerometerTracker = null;
		}
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private void initShakeDetector() {
		  // ShakeDetector initialization
		
		/*
      mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      mShakeDetector = new ShakeDetector();
      mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

          @Override
          public void onShake(int count) {
              
              
              	Utils.beep();
          }
      });
	*/
      
		mAccelerometerTracker = new AccelerometerTracker(this);
		mAccelerometerTracker.setListener(new AccelerometerTracker.AccelerometerListener() {
			
			@Override
			public void onShake(float force) {
				if (mAccelerometerTracker != null){
					Utils.playSound(ShakeSecondActivity.this, R.raw.shake);
					//playSound();
				}
			}
			
			@Override
			public void onAccelerationChanged(float x, float y, float z) {
				// TODO Auto-generated method stub
				
			}
		});

      
      
	}
	
	private MediaPlayer mpTada = null;
	
	private void playSound(){
		if (mpTada == null){
			 mpTada = MediaPlayer.create(this, R.raw.shake);
			 mpTada.setVolume(1.0f, 1.0f);
			 mpTada.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mpTada.release();
						mpTada = null;
					}
				});
			 mpTada.start();
		}
		 
	}
	
	private void setMapLocation(Location location){
		if (location != null){
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			
			if (mLastMarker != null)
				mLastMarker.remove();
			mLastMarker = map.addMarker(new MarkerOptions().position(ll).title("Your Location"));
			float currentZoom = map.getCameraPosition().zoom;

		    if (currentZoom < 15)
		    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));				
		    map.getUiSettings().setZoomControlsEnabled(false);
		    //map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
		    //CircleOptions co = new CircleOptions();
            //co.center(ll).radius(mGPSTracker.getAccuracy()).fillColor(Color.GRAY).strokeColor(Color.BLACK).strokeWidth(4.0f);
            //map.addCircle(co);
            
		}
		
	}



}
