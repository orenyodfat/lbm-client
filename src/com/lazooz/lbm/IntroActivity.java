package com.lazooz.lbm;

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

public class IntroActivity extends ActionBarActivity {

	private Button nextBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);

		
		nextBtn = (Button)findViewById(R.id.intro_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GPSTracker gps = GPSTracker.getInstance(getApplicationContext());
				if(gps.isGPSEnabled()){
					Intent intent = new Intent(IntroActivity.this, MapShowLocationActivity.class);
					startActivity(intent);
					finish();								
				}
				else{
					Intent intent = new Intent(IntroActivity.this, MissionGPSOnActivity.class);
					startActivity(intent);
					finish();			
				}
			}
		});
		
	}
	




}
