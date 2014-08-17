package com.lazooz.lbm;

import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class MainZoozActivity extends ActionBarActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		setContentView(R.layout.activity_main_zooz);

		
		
	}
	




}
