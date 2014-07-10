package com.lazooz.lbm;

import com.lazooz.lbm.preference.MySharedPreferences;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.os.Build;

public class SplashActivity extends ActionBarActivity {
	
	protected int _splashTime = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

	
		    new Handler().postDelayed(new Runnable() {
		        public void run() {
		            StartTheActivity();
		        	
		        }
		    }, _splashTime);
		
		
	}

	public Class<?> getNextActivity(){
		int stage = MySharedPreferences.getInstance().getStage(this);

		switch (stage) {
		case MySharedPreferences.STAGE_NEVER_RUN:
			return IntroActivity.class;
		
		case MySharedPreferences.STAGE_INTRO:
			if (GPSTracker.getInstance(this).isGPSEnabled())
				return MapShowLocationActivity.class;
			else
				return IntroActivity.class;
			
		case MySharedPreferences.STAGE_MAP:
			return RegistrationActivity.class;
		
		case MySharedPreferences.STAGE_REG_INIT:
			return RegistrationActivity.class;
		
		case MySharedPreferences.STAGE_REG_CELL_SENT:
			return RegistrationActivity.class;
		
		case MySharedPreferences.STAGE_REG_CELL_SENT_OK:
			return RegistrationActivity.class;
		
		case MySharedPreferences.STAGE_REG_CONF_SENT:
			return RegistrationActivity.class;

		case MySharedPreferences.STAGE_REG_CONF_SENT_OK:
			return CongratulationsRegActivity.class;
		
		case MySharedPreferences.STAGE_REG_CONGRATS:
			return MissionDrive100Activity.class;
		case MySharedPreferences.STAGE_DRIVE100:
			return MainActivity.class;
		case MySharedPreferences.STAGE_MAIN_NO_DRIVE100:
			return MainActivity.class;
		case MySharedPreferences.STAGE_DRIVE100_CONGRATS:
			return MainActivity.class;
		case MySharedPreferences.STAGE_MAIN_NO_GET_FRIENDS:
			return MainActivity.class;
		case MySharedPreferences.STAGE_GET_FRIENDS_CONGRATS:
			return MainActivity.class;
		case MySharedPreferences.STAGE_MAIN:
			return MainActivity.class;
			

		default:
			return IntroActivity.class;
		}

	}
	
	protected void StartTheActivity() {
		startActivity(new Intent(SplashActivity.this, getNextActivity()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
