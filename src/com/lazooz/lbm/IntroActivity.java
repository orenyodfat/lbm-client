package com.lazooz.lbm;

import com.lazooz.lbm.preference.MySharedPreferences;
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
import android.os.Build;
import android.provider.Settings;

public class IntroActivity extends ActionBarActivity {

	private Button nextBtn;
	private Button gpsActivateBtn;
	private GPSTracker gps;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		gps = GPSTracker.getInstance(IntroActivity.this);
		
		nextBtn = (Button)findViewById(R.id.intro_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(gps.isGPSEnabled()){
					Intent intent = new Intent(IntroActivity.this, MapShowLocationActivity.class);
					intent.putExtra("MISSION_GPS_ON", true);
					startActivity(intent);
				}

				else
					gps.showSettingsAlert();
					//Utils.messageToUser(IntroActivity.this, "GPS", "Please turn on the GPS");
			}
		});
		
			
		gpsActivateBtn = (Button)findViewById(R.id.intro_gpson_btn);
		gpsActivateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				IntroActivity.this.startActivity(intent);				
			}
		});

		gpsActivateBtn.setVisibility(View.GONE);
		
		
		MySharedPreferences.getInstance().setStage(this, MySharedPreferences.STAGE_INTRO);
	}
	




}
