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

	public static final String KEY_CHARGER_CONNECT = "key_charger_connectivity";
	public static final String KEY_MINING_ENA_DIS = "key_mining_enable_disable";
	public static final String KEY_SOUND_ENA_DIS = "key_sound_enable_disable";
	
	
	public static final boolean PREF_MINING_CHARGER_CONNECTIVITY_DEFAULT = true;
	public static final boolean PREF_MINING_MINING_ENAB_DIS_DEFAULT = true;
	public static final boolean PREF_SOUND_GESTERS_ENAB_DIS_DEFAULT = false;
	
	
	
	private PreferenceScreen mPrefSet;
	private CheckBoxPreference mPowerSaverPref;
	private CheckBoxPreference mMiningEnaDisPref;
	private CheckBoxPreference mSoundEnaDisPref;
	
	




	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		 
		addPreferencesFromResource(R.xml.preferences);		
		mPrefSet = getPreferenceScreen();
		
		//getSActionBar().setDisplayHomeAsUpEnabled(true);
		
		mPowerSaverPref = (CheckBoxPreference)mPrefSet.findPreference(KEY_CHARGER_CONNECT);  
		mPowerSaverPref.setChecked(MySharedPreferences.getInstance().getChargerConnectivityMode(this));
		if(mPowerSaverPref.isChecked()){
			mPowerSaverPref.setSummary(R.string.settings_mining_charger_connectivity_summary_on);
			mPowerSaverPref.setTitle(R.string.settings_mining_charger_connectivity_title_on);
		}
		else {
			mPowerSaverPref.setSummary(R.string.settings_mining_charger_connectivity_summary_off);
			mPowerSaverPref.setTitle(R.string.settings_mining_charger_connectivity_title_off);
		}

		mMiningEnaDisPref = (CheckBoxPreference)mPrefSet.findPreference(KEY_MINING_ENA_DIS);  
		mMiningEnaDisPref.setChecked(MySharedPreferences.getInstance().getMiningEnabledMode(this));
		mMiningEnaDisPref.setTitle(R.string.settings_mining_enable_disable_mining_title);
		if(mMiningEnaDisPref.isChecked())
			mMiningEnaDisPref.setSummary(R.string.settings_mining_enable_disable_mining_summary_enable);
		else 
			mMiningEnaDisPref.setSummary(R.string.settings_mining_enable_disable_mining_summary_disable);
		
		mSoundEnaDisPref = (CheckBoxPreference)mPrefSet.findPreference(KEY_SOUND_ENA_DIS);  
		mSoundEnaDisPref.setChecked(MySharedPreferences.getInstance().getSoundEnabledMode(this));
		mSoundEnaDisPref.setTitle(R.string.settings_sound_enable_disable_title);
		if(mSoundEnaDisPref.isChecked())
			mSoundEnaDisPref.setSummary(R.string.settings_sound_enable_disable_summary_enable);
		else 
			mSoundEnaDisPref.setSummary(R.string.settings_sound_enable_disable_summary_disable);

		
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
		if (key.equals(KEY_CHARGER_CONNECT)){
			
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
		else if (key.equals(KEY_MINING_ENA_DIS)){
			
			MySharedPreferences.getInstance().setMiningEnabledMode(this, mMiningEnaDisPref.isChecked());
			
			if(mMiningEnaDisPref.isChecked())
				mMiningEnaDisPref.setSummary(R.string.settings_mining_enable_disable_mining_summary_enable);
			else 
				mMiningEnaDisPref.setSummary(R.string.settings_mining_enable_disable_mining_summary_disable);
			
			
		}
        else if (key.equals(KEY_SOUND_ENA_DIS)){
			
			MySharedPreferences.getInstance().setSoundEnabledMode(this, mSoundEnaDisPref.isChecked());
			
			if(mSoundEnaDisPref.isChecked())
				mSoundEnaDisPref.setSummary(R.string.settings_sound_enable_disable_summary_enable);
			else 
				mSoundEnaDisPref.setSummary(R.string.settings_sound_enable_disable_summary_disable);
			
			
		}
		
	}

}
