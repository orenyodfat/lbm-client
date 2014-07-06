package com.lazooz.lbm;

import com.lazooz.lbm.preference.MySharedPreferences;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
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

	protected void StartTheActivity() {
		if (MySharedPreferences.getInstance().wasInitFirstTime(this)){
			startActivity(new Intent(SplashActivity.this, MainActivity.class));
			finish();
		}
		else{
			startActivity(new Intent(SplashActivity.this, IntroActivity.class));
			finish();			
		}
		
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
