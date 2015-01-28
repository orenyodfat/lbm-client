package com.lazooz.lbm;

import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

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
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class CongratulationsRegActivity extends ActionBarActivity {

	private Button nextBtn;
	private TextView mMainTextTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_congratulations_reg);
		MySharedPreferences.getInstance().setStage(CongratulationsRegActivity.this, MySharedPreferences.STAGE_REG_CONGRATS);
		
		
		
		
		mMainTextTV = (TextView)findViewById(R.id.congrats_reg_text_tv);
		//mMainTextTV.setText(MySharedPreferences.getInstance().getSecondScreenText(this));
		
		
		nextBtn = (Button)findViewById(R.id.congratulation_reg_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CongratulationsRegActivity.this, MainActivity.class));
				finish();			
			}
		});
		
		//boolean isNewUser = getIntent().getBooleanExtra("NEW_USER", false);
		
		
		//Utils.messageToUser(this, "Confirmation succeed", "Congratulation, you've got yourself a new digital wallet!");
		
	}
	




}
