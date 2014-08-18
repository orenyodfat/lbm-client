package com.lazooz.lbm;


import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;

public class MainZoozActivity extends ActionBarActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		setContentView(R.layout.activity_main_zooz);
		
		
		Button scanBtn = (Button)findViewById(R.id.zooz_scan_btn);
		scanBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainZoozActivity.this, CameraQRActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		
		
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 1) {
			String s = intent.getStringExtra("DATA");
			Toast.makeText(this, s,  Toast.LENGTH_LONG).show();
		}
		

          
	}


}
