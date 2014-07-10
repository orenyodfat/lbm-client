package com.lazooz.lbm;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapShowLocationActivity extends ActionBarActivity {

	private Button nextBtn;
	private GoogleMap map;
	private boolean mWasInMission;
	
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_show_location);

		
		mWasInMission = getIntent().getBooleanExtra("MISSION_GPS_ON", false);
		
		nextBtn = (Button)findViewById(R.id.map_show_loc_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MapShowLocationActivity.this, RegistrationActivity.class));
				finish();			
			}
		});
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		
		
		if (map!=null){
			MarkerOptions mo = new MarkerOptions();
			GPSTracker gps = GPSTracker.getInstance(getApplicationContext());
			
			LatLng ll = gps.getLocationLL();
			
		    Marker yourLoc = map.addMarker(new MarkerOptions().position(ll).title("Your Location"));
		    map.getUiSettings().setZoomControlsEnabled(false);
		    map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 11));
		    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
		    
		    
		    CircleOptions co = new CircleOptions();
            co.center(ll);
            co.radius(gps.getAccuracy());
            co.fillColor(Color.GRAY);
            co.strokeColor(Color.BLACK);
            co.strokeWidth(4.0f);
            
            map.addCircle(co);
		    
		    
		 }
	}
	




}
