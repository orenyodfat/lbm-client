package com.lazooz.lbm;

import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;
import android.provider.Settings;

public class MissionGPSOnActivity extends ActionBarActivity {

	private Button nextBtn;
	private Button gpsActivateBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		setContentView(R.layout.activity_mission_gps_on);

		
		nextBtn = (Button)findViewById(R.id.msn_gpson_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GPSTracker gps = GPSTracker.getInstance(getApplicationContext());
				if(gps.isGPSEnabled()){
					Intent intent = new Intent(MissionGPSOnActivity.this, MapShowLocationActivity.class);
					intent.putExtra("MISSION_GPS_ON", true);
					startActivity(intent);
				}

				else
					Utils.messageToUser(MissionGPSOnActivity.this, "GPS", "Please turn on the GPS");
			}
		});

		gpsActivateBtn = (Button)findViewById(R.id.msn_gpson_activate_gps_btn);
		gpsActivateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				MissionGPSOnActivity.this.startActivity(intent);				
			}
		});
		
	}
	




}
