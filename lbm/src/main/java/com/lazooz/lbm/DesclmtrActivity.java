package com.lazooz.lbm;

import com.lazooz.lbm.components.MyScrollView;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.Settings;

public class DesclmtrActivity extends ActionBarActivity {

	private Button nextBtn;
	private TextView mInfoTV;
	private LocationManager mLocationManager;
	private boolean mIsFromMenuMode;
	private MyScrollView mScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_desclimer);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mIsFromMenuMode = getIntent().getBooleanExtra("FROM_MENU_MODE", false);
		
		mInfoTV = (TextView)findViewById(R.id.intro_info_tv);
		String theTitle = getString(R.string.disclaimer_title);
		//String theTitle = MySharedPreferences.getInstance().getDisclaimerScreenTitle(this);
		//String theText = MySharedPreferences.getInstance().getDisclaimerScreenText(this);
		String theText = getString(R.string.disclaimer_text);
		
		mInfoTV.setText(theTitle + "\n"+ theText);
		

		mScrollView = (MyScrollView)findViewById(R.id.dsclmr_scrill_view);
		mScrollView.setOnScrollChangeListener(new MyScrollView.OnScrollChangeListener() {
			
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				View view = (View) mScrollView.getChildAt(mScrollView.getChildCount()-1);
		        int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));// Calculate the scrolldiff
		        if( diff < 80 ){  // if diff is zero, then the bottom has been reached
		        }
				
			}
		});
		
		
		nextBtn = (Button)findViewById(R.id.intro_accept_btn);
		nextBtn.setEnabled(true);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
					
					
				if (!isGPSEnabled && !isNetworkEnabled)
					Utils.showSettingsAlertNoRem(DesclmtrActivity.this, getString(R.string.gps_message_no_gps_no_net));
				else{
					Intent intent = new Intent(DesclmtrActivity.this, MapShowLocationActivity.class);
					intent.putExtra("MISSION_GPS_ON", true);
					startActivity(intent);
					finish();
				}
				
			}
		});
		
		
		
		
		
	}
	

	
}
