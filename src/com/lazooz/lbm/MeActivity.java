package com.lazooz.lbm;

import com.lazooz.lbm.businessClasses.ServerData;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class MeActivity extends ActionBarActivity {



	private TextView mIDTV;
	private TextView mWalletNumTV;
	private TextView mNumFriendsTV;
	private TextView mNumKmTV;
	private TextView mNumShakesTV;
	private TextView mZoozsTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		setContentView(R.layout.activity_me);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Utils.setTitleColor(this, getResources().getColor(R.color.white));
		
		mIDTV= (TextView)findViewById(R.id.me_id_tv);
		mWalletNumTV= (TextView)findViewById(R.id.me_wallet_num_tv);
		mNumFriendsTV= (TextView)findViewById(R.id.me_num_friends_invited_tv);
		mNumKmTV= (TextView)findViewById(R.id.me_num_km_mined_tv);
		mNumShakesTV= (TextView)findViewById(R.id.me_num_shakes_tv);
		mZoozsTV= (TextView)findViewById(R.id.me_zoozs_tv);
		
		
		
		
		MySharedPreferences msp = MySharedPreferences.getInstance();
		ServerData sd = msp.getServerData(this);

		float distanceFromServer = sd.getDistanceFloat();
		float distanceLocal = msp.getLocalDistance(this);
		float distanceTotal = distanceFromServer + distanceLocal;
		float distanceKMf = distanceTotal / 1000;
		float distanceMf = distanceTotal % 1000;

		int distanceKMd = (int)distanceKMf;
		int distanceMd = (int)distanceMf;
		
		int localDist = (int)distanceLocal;

		
		
		mIDTV.setText(msp.getUserIdSD(this));
		mWalletNumTV.setText(msp.getWalletNum(this));
		mNumFriendsTV.setText(msp.getNumInvitedContacts(this)+"");
		mNumKmTV.setText(String.format("%.1f", distanceKMf));
		mNumShakesTV.setText(msp.getNumShakedUsers(this)+"");
		mZoozsTV.setText(sd.getZoozBalance());

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
