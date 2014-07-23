package com.lazooz.lbm;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
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
import com.lazooz.lbm.preference.MySharedPreferences;

public class MapShowLocationActivity extends ActionBarActivity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener{

	private Button nextBtn;
	private GoogleMap map;
	private boolean mWasInMission;
	private GPSTracker mGPSTracker;
	private TextView mMapAccuracyTV;
	private Marker mLastMarker;
	private Button mToolTipButton;
	private ToolTipView mToolTipView;
	private ToolTipRelativeLayout mToolTipFrameLayout;
	
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_show_location);

		
		mWasInMission = getIntent().getBooleanExtra("MISSION_GPS_ON", false);
		
		mMapAccuracyTV = (TextView)findViewById(R.id.map_accuracy_tv);
		
		mToolTipFrameLayout = (ToolTipRelativeLayout) findViewById(R.id.tooltipframelayout);
		
		mToolTipButton = (Button)findViewById(R.id.tooltip_btn);
		mToolTipButton.setOnClickListener(this);
		
		
		nextBtn = (Button)findViewById(R.id.map_show_loc_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mGPSTracker.setOnLocationListener(null);
				MySharedPreferences.getInstance().setStage(MapShowLocationActivity.this, MySharedPreferences.STAGE_REG_INIT);
				startActivity(new Intent(MapShowLocationActivity.this, RegistrationActivity.class));
				finish();			
			}
		});
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		map = mapFragment.getMap();
		
		map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			
			@Override
			public void onMyLocationChange(Location location) {
				if (location != null){
					//setMapLocation(location);
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
			
			setMapInitLocation(mGPSTracker.getLocation());
		    
		 }
		
		MySharedPreferences.getInstance().setStage(this, MySharedPreferences.STAGE_MAP);
		
		
		
		 new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
	                addPurpleToolTipView();
	            }
	        }, 1000);
		
		
	}
	
    private void addPurpleToolTipView() {
/*    	
    	mToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip()
                        .withContentView(LayoutInflater.from(this).inflate(R.layout.custom_tooltip, null))
                        .withColor(getResources().getColor(R.color.holo_purple)), mToolTipButton);
    	mToolTipView.setOnToolTipViewClickedListener(this);
*/    	
    	mToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip()
                         .withText("Identifing your location. Please note.....")
                         .withColor(getResources().getColor(R.color.holo_green_light)), mToolTipButton);
    	mToolTipView.setOnToolTipViewClickedListener(this);
    }
	
    
   
    
	private void setMapInitLocation(Location location){
		if (location != null){
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			
		    map.getUiSettings().setZoomControlsEnabled(false);
			
	    	
	    	map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
				
				@Override
				public void onMapLoaded() {
					map.animateCamera(CameraUpdateFactory.zoomTo(18), 3000, null);
					
				}
			});

	    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 11));		
	    	
	    	

		    
		    
		    
            
            mMapAccuracyTV.setText(location.getAccuracy()+"");
		}
		
	}
    
	
	private void setMapLocation(Location location){
		if (location != null){
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			
			if (mLastMarker != null)
				mLastMarker.remove();
			//mLastMarker = map.addMarker(new MarkerOptions().position(ll).title("Your Location"));
			float currentZoom = map.getCameraPosition().zoom;

		    if (currentZoom < 15)
		    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));				
		    map.getUiSettings().setZoomControlsEnabled(false);
		    //map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
		    //CircleOptions co = new CircleOptions();
            //co.center(ll).radius(mGPSTracker.getAccuracy()).fillColor(Color.GRAY).strokeColor(Color.BLACK).strokeWidth(4.0f);
            //map.addCircle(co);
            
            mMapAccuracyTV.setText(location.getAccuracy()+"");
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



}
