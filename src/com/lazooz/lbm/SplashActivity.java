package com.lazooz.lbm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Build;

public class SplashActivity extends ActionBarActivity implements AnimationListener{
	
	protected int _splashTime = 3000;
	private boolean mFinishAnimation;
	private boolean mFinishTimer;
	private boolean mFinishRetrieveData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		/*
		Intent intent = new Intent(SplashActivity.this, ShakeSecondActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		*/
		
		
		
		getScreenTextAsync();
		
	
		
		
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		
		StartAnimations();
		
	    new Handler().postDelayed(new Runnable() {
	        public void run() {
	        	mFinishTimer = true;
	        	if (mFinishRetrieveData)
	        		StartTheActivity();
	        	
	        }
	    }, _splashTime);
	
	    
	}
	

	private void getScreenTextAsync() {
		GetScreenInfoText getScreenInfoText = new GetScreenInfoText();
		getScreenInfoText.execute();
		
	}

	public Class<?> getNextActivity(){
		int stage = MySharedPreferences.getInstance().getStage(this);

		switch (stage) {
		case MySharedPreferences.STAGE_NEVER_RUN:
			return IntroActivity.class;
		
		case MySharedPreferences.STAGE_INTRO:
			if (GPSTracker.getInstance(this).isGPSEnabled())
				return MapShowLocationActivity.class;
			else
				return IntroActivity.class;
			
		case MySharedPreferences.STAGE_MAP:
			return RegistrationActivity.class;
		
		case MySharedPreferences.STAGE_REG_INIT:
			return RegistrationActivity.class;
		
		case MySharedPreferences.STAGE_REG_CELL_SENT:
			return RegistrationActivity.class;
		
		case MySharedPreferences.STAGE_REG_CELL_SENT_OK:
			return RegistrationActivity.class;
		
		case MySharedPreferences.STAGE_REG_CONF_SENT:
			return RegistrationActivity.class;

		case MySharedPreferences.STAGE_REG_CONF_SENT_OK:
			return CongratulationsRegActivity.class;
		
		case MySharedPreferences.STAGE_REG_CONGRATS:
			return MainActivity.class;
			
		case MySharedPreferences.STAGE_MAIN:
			return MainActivity.class;
			

		default:
			return IntroActivity.class;
		}

	}
	
	protected void StartTheActivity() {
		if(Utils.haveNetworkConnection(this)){
			Intent intent = new Intent(SplashActivity.this, getNextActivity());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		else {
			
	     	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	     	alertDialog.setTitle("No internet Connection");
	     	alertDialog.setMessage("Please connect your device to the internet and restart the application");
		    alertDialog.setCanceledOnTouchOutside(false);
		    
		    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
		    	
		    	@Override
		        public void onClick(DialogInterface dialog, int which) {
		    		SplashActivity.this.finish();
		    	}
		    });
		    
		    alertDialog.show();
		}
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	private class GetScreenInfoText extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(SplashActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				
				bServerCom.getScreenInfoText();
				jsonReturnObj = bServerCom.getReturnObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        	
        	String serverMessage = "";
	
			try {
				if (jsonReturnObj == null)
					serverMessage = "ConnectionError";
				else {
					serverMessage = jsonReturnObj.getString("message");
					if (serverMessage.equals("success")){

						
						MySharedPreferences.getInstance().saveScreenInfoText(SplashActivity.this, jsonReturnObj);

					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				serverMessage = "GeneralError";
			}
			
			
			return serverMessage;
		}
		
		@Override
		protected void onPostExecute(String result) {
			mFinishRetrieveData = true;
			if (result.equals("success")){
				
			}
			
			if (mFinishTimer)
				StartTheActivity();
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}

	 private void StartAnimations() { 
	        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
	        anim.reset();
	        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
	        l.clearAnimation();
	        
	        l.startAnimation(anim);
	        
	        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
	        //anim.reset();
	        anim.setInterpolator(new AccelerateInterpolator(2.0f));
	        anim.setAnimationListener(this);
	        
	        //ImageView iv = (ImageView) findViewById(R.id.logo);
	        LinearLayout llLogo = (LinearLayout) findViewById(R.id.logoll);
	        
	        //iv.clearAnimation();
	        llLogo.startAnimation(anim);
	        //mProgBar.setVisibility(View.INVISIBLE);
	    }

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
