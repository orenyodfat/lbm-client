package com.lazooz.lbm;

import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

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

public class CongratulationsGPSActivity extends ActionBarActivity {

	private Button nextBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		setContentView(R.layout.activity_congratulations_gps);

		
		nextBtn = (Button)findViewById(R.id.congratulation_gps_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CongratulationsGPSActivity.this, RegistrationActivity.class));
				finish();			
			}
		});
		
	}
	




}
