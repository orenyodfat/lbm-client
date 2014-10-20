package com.lazooz.lbm.preference;


import com.lazooz.lbm.R;
import com.lazooz.lbm.utils.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	public static final boolean PREF_MINING_CHARGER_CONNECTIVITY_DEFAULT = false;
	
	private PreferenceScreen mPrefSet;
	private CheckBoxPreference mPowerSaverPref;




	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		 
		addPreferencesFromResource(R.xml.preferences);		
		mPrefSet = getPreferenceScreen();
		
		//getSActionBar().setDisplayHomeAsUpEnabled(true);
		
		mPowerSaverPref = (CheckBoxPreference)mPrefSet.findPreference("key_charger_connectivity");  
		mPowerSaverPref.setChecked(MySharedPreferences.getInstance().getChargerConnectivityMode(this));
		if(mPowerSaverPref.isChecked()){
			mPowerSaverPref.setSummary(R.string.settings_mining_charger_connectivity_summary_on);
			mPowerSaverPref.setTitle(R.string.settings_mining_charger_connectivity_title_on);
		}
		else {
			mPowerSaverPref.setSummary(R.string.settings_mining_charger_connectivity_summary_off);
			mPowerSaverPref.setTitle(R.string.settings_mining_charger_connectivity_title_off);
		}
		
	}

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
	    super.onResume();
	    // Set up a listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	
	
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		if (key.equals("key_charger_connectivity")){
			
			MySharedPreferences.getInstance().setChargerConnectivityMode(this, mPowerSaverPref.isChecked());
			if(mPowerSaverPref.isChecked()){
				mPowerSaverPref.setSummary(R.string.settings_mining_charger_connectivity_summary_on);
				mPowerSaverPref.setTitle(R.string.settings_mining_charger_connectivity_title_on);
			}
			else{
				mPowerSaverPref.setSummary(R.string.settings_mining_charger_connectivity_summary_off);
				mPowerSaverPref.setTitle(R.string.settings_mining_charger_connectivity_title_off);
			}
		}
		
	}

}
