package com.lazooz.lbm;

import com.lazooz.lbm.preference.MySharedPreferences;
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
import android.widget.Toast;
import android.os.Build;

public class CongratulationsGetFriendsActivity extends ActionBarActivity {

	private Button nextBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		setContentView(R.layout.activity_congratulations_getfriends);
		
		
		nextBtn = (Button)findViewById(R.id.congratulation_getfriends_next_btn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CongratulationsGetFriendsActivity.this, "the button was pressed", Toast.LENGTH_LONG).show();
				MySharedPreferences.getInstance().setStage(CongratulationsGetFriendsActivity.this, MySharedPreferences.STAGE_MAIN);
			}
		});
		
	}
	




}
