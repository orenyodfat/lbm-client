package com.lazooz.lbm;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.haarman.supertooltips.ToolTip;
import com.haarman.supertooltips.ToolTipRelativeLayout;
import com.haarman.supertooltips.ToolTipView;
import com.lazooz.lbm.components.MyProgBar;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

public class MapShowLocationActivity extends ActionBarActivity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener, LocationListener{

	private Button nextBtn;
	private GoogleMap map;
	private boolean mWasInMission;
	private Marker mLastMarker;
	private Button mToolTipButton;
	private ToolTipView mToolTipView;
	private ToolTipRelativeLayout mToolTipFrameLayout;
	private boolean mMapWasInit;
	private LocationManager mLocationManager;
	private MyProgBar mMyProgBar;
	private boolean mIsAccuraccyAccomplished;
	private boolean mAsAccuraccy;
	private String mLastMsg = "";
	
	public static final String FILE_TAG = "ZOOZ";
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 8; // 
	
	@Override
	protected void onStart() {
		super.onStart();
		if (map==null)
			initMap();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_map_show_location);
		
		Utils.activateSavingLogcatToFile(this, false);
		Log.i(FILE_TAG, "LOG CREATED");
		
		mWasInMission = getIntent().getBooleanExtra("MISSION_GPS_ON", false);
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		}
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		
		//mMapAccuracyTV = (TextView)findViewById(R.id.map_accuracy_tv);
		mMyProgBar = (MyProgBar)findViewById(R.id.myProgBar2);
		mMyProgBar.setMaxVal(440);
		
		mToolTipFrameLayout = (ToolTipRelativeLayout) findViewById(R.id.tooltipframelayout);
		
		mToolTipButton = (Button)findViewById(R.id.tooltip_btn);
		mToolTipButton.setOnClickListener(this);
		
		mMapWasInit = false;
		mIsAccuraccyAccomplished = false;
		mAsAccuraccy = false;
		
		nextBtn = (Button)findViewById(R.id.map_show_loc_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MySharedPreferences.getInstance().setStage(MapShowLocationActivity.this, MySharedPreferences.STAGE_REG_INIT);
				startActivity(new Intent(MapShowLocationActivity.this, RegistrationActivity.class));
				finish();			
			}
		});
		
		nextBtn.setVisibility(View.INVISIBLE);
		
		initMap();
		
		MySharedPreferences.getInstance().setStage(this, MySharedPreferences.STAGE_MAP);
		
		
		
		 new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
	            	if (!mAsAccuraccy)
	                addPurpleToolTipView(getString(R.string.identifying_your_location));
	            }
	        }, 1000);
		
		
	}
	
	private void initMap(){
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		map = mapFragment.getMap();
        
        if (map!=null){
    		map.setMyLocationEnabled(true);
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
    			
    			@Override
    			public void onMyLocationChange(Location location) {
    				if (location != null){
    					updateAccuracy((int)location.getAccuracy());
    					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
    					Log.i("ZOOZ", "MAP_SW google map location change: " + "LAT:"+ ll.latitude + " LON: " + ll.longitude + " ACC:" + (int)location.getAccuracy());
    					setMapLocation(location);
    					
    				}
    				
    			}
    		});
			setMapInitLocation(getLocation());
		 }
	}
	
    protected void updateAccuracy(int accuracy) {
    	mAsAccuraccy = true;
    	if (accuracy < 25){
    		if (!mIsAccuraccyAccomplished)
    			addPurpleToolTipView(getString(R.string.successfuly_located));
    		mIsAccuraccyAccomplished = true;
    		nextBtn.setVisibility(View.VISIBLE);
    	}
    	
    	mMyProgBar.setPrgress(accuracy);
        mMyProgBar.invalidate();
        
        if (!mIsAccuraccyAccomplished)
        	addPurpleToolTipView(getString(R.string.improve_your_location));
		
	}

	private void addPurpleToolTipView(String msg) {
/*    	
    	mToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip()
                        .withContentView(LayoutInflater.from(this).inflate(R.layout.custom_tooltip, null))
                        .withColor(getResources().getColor(R.color.holo_purple)), mToolTipButton);
    	mToolTipView.setOnToolTipViewClickedListener(this);

*/    	
		if (mLastMsg.equals(msg))
			return;
		
		mLastMsg = msg;
		if (mToolTipView != null) {
			mToolTipView.removeAllViews();
			mToolTipView.invalidate();
		}
		mToolTipView = null;
    	mToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip()
                         .withText(msg)
                         .withColor(getResources().getColor(R.color.holo_green_light)), mToolTipButton);
    	mToolTipView.setOnToolTipViewClickedListener(this);
    }
	
    
   
    
	private void setMapInitLocation(Location location){
		if (location != null){
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			
			Log.i("ZOOZ", "MAP_SW set map init location: " + "LAT:"+ ll.latitude + " LON: " + ll.longitude + " ACC:" + (int)location.getAccuracy());
		    
			map.getUiSettings().setZoomControlsEnabled(false);
			
	    	
	    	map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
				
				@Override
				public void onMapLoaded() {
					CameraPosition cp = map.getCameraPosition();					
					CameraPosition cameraPosition = new CameraPosition.Builder()
				    .target(cp.target)      // Sets the center of the map to Mountain View
				    .zoom(18)                   // Sets the zoom
				    .bearing(cp.bearing)                // Sets the orientation of the camera to east
				    .tilt(50)                   // Sets the tilt of the camera to 30 degrees
				    .build();                   // Creates a CameraPosition from the builder
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000, null);
					mMapWasInit = true;
					//map.animateCamera(CameraUpdateFactory.zoomTo(18), 3000, null);
					
				}
			});

	    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 11));		
	    	
		    updateAccuracy((int)location.getAccuracy());
		    
		}
		
	}
    
	
	private void setMapLocation(Location location){
		if (location != null){
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			
			Log.i("ZOOZ", "MAP_SW set map location: " + "LAT:"+ ll.latitude + " LON: " + ll.longitude + " ACC:" + (int)location.getAccuracy());
			
			if (mLastMarker != null)
				mLastMarker.remove();
			//float currentZoom = map.getCameraPosition().zoom;

			
			
			CameraPosition cp = map.getCameraPosition();					
			CameraPosition cameraPosition = new CameraPosition.Builder()
		    .target(ll)      // Sets the center of the map to Mountain View
		    .zoom(18)                   // Sets the zoom
		    .bearing(cp.bearing)                // Sets the orientation of the camera to east
		    .tilt(50)                   // Sets the tilt of the camera to 30 degrees
		    .build();                   // Creates a CameraPosition from the builder
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000, null);
			
			
			
			
			
			
			
			
			
	    	//map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 18));				
		    map.getUiSettings().setZoomControlsEnabled(false);
		    
		    updateAccuracy((int)location.getAccuracy());
            //mMapAccuracyTV.setText(location.getAccuracy()+"");
            //mMapAccuracyTV.invalidate();
		}
		
	}

	 @Override
	    public void onToolTipViewClicked(ToolTipView toolTipView) {
	            mToolTipView = null;
	    }

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i("ZOOZ", "MAP_SW onLocationChanged: " + "LAT:"+ location.getLatitude() + " LON: " + location.getLongitude() + " ACC:" + (int)location.getAccuracy());
		if (mMapWasInit){
			Log.i("ZOOZ", "MAP_SW onLocationChanged, map was init");
			if (location.getProvider().equals(LocationManager.GPS_PROVIDER)){
				Log.i("ZOOZ", "MAP_SW sonLocationChanged from GPS_PROVIDER");
				setMapLocation(location);
			}
			else
				Log.i("ZOOZ", "MAP_SW sonLocationChanged from NETWORK - do nothing");
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i("ZOOZ", "MAP_SW onProviderDisabled: " +  provider);
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i("ZOOZ", "MAP_SW onProviderEnabled: " +  provider);
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i("ZOOZ", "MAP_SW onStatusChanged: " +  provider + " status:" + status);
	}

	private Location getLocation(){
		if (isGPSEnabled())
			return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		else
			return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}

	
	
	private boolean isGPSEnabled(){
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
			
	private boolean isNetworkEnabled(){
		return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}


	
	@Override
	protected void onResume() {
		//Log.i("ZOOZ", "MAP_SW onResume, start listen to location");
		if(mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		}
		//mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		super.onResume();
	}
	
	
	@Override
	protected void onPause() {
		Log.i("ZOOZ", "MAP_SW onPause, stop listen to location");
		mLocationManager.removeUpdates(this);
		super.onPause();
	}
	
	
	
}
