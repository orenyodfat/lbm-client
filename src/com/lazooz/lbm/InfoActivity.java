package com.lazooz.lbm;

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
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class InfoActivity extends ActionBarActivity {

	private Button nextBtn;
	private TextView mBuildNameTV;
	private TextView mBuildNumTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		mBuildNameTV = (TextView)findViewById(R.id.build_name_tv);
		mBuildNumTV = (TextView)findViewById(R.id.build_num_tv);
		
		mBuildNameTV.setText(Utils.getVersionName(this));
		mBuildNumTV.setText(Utils.getVersionCode(this)+"");
		
	}
	




}
