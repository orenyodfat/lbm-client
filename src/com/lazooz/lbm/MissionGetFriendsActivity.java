package com.lazooz.lbm;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MissionGetFriendsActivity extends ActionBarActivity {

	private Button okBtn;
	private Button gpsActivateBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_friends);

		
		okBtn = (Button)findViewById(R.id.msn_drive100_ok_btn);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					Intent intent = new Intent(MissionGetFriendsActivity.this, MainActivity.class);
					startActivity(intent);
				}
		});

		
	}
	




}
