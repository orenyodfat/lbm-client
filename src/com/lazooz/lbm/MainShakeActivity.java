package com.lazooz.lbm;

import com.lazooz.lbm.preference.MySharedPreferences;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;

public class MainShakeActivity extends ActionBarActivity {


	private TextView mMainTextTV;
	private Button mNextBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_shake);
		
		
		mMainTextTV = (TextView)findViewById(R.id.main_shake_maintext_tv);
		mMainTextTV.setText(MySharedPreferences.getInstance().getBeforShakeText(this));
		
		
		mNextBtn = (Button)findViewById(R.id.main_shake_next_btn);
		mNextBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainShakeActivity.this, ShakeSecondActivity.class);
				startActivity(intent);
				
			}
		});

		
		
	}
	




}
