package com.lazooz.lbm;

import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

public class MainShakeActivity extends ActionBarActivity {


	private TextView mMainTextTV;
	private Button mNextBtn;
	protected LocationManager mLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		setContentView(R.layout.activity_main_shake);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Utils.setTitleColor(this, getResources().getColor(R.color.white));
		
		mMainTextTV = (TextView)findViewById(R.id.main_shake_maintext_tv);
		
		//mMainTextTV.setText(MySharedPreferences.getInstance().getBeforShakeText(this));
		
		
		mNextBtn = (Button)findViewById(R.id.main_shake_next_btn);
		mNextBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				
				mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
					
					
				if (!isGPSEnabled && !isNetworkEnabled)
					Utils.showSettingsAlertNoRem(MainShakeActivity.this, getString(R.string.gps_message_no_gps_no_net_feature));
				else{
					Intent intent = new Intent(MainShakeActivity.this, ShakeSecondActivity.class);
					startActivity(intent);
					finish();
				}
				
			}
		});

		
		
		
				
				
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



}
