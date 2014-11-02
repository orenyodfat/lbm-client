package com.lazooz.lbm;

import org.json.JSONException;
import org.json.JSONObject;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {
	
	protected int _splashTime = 3000;
	private boolean mFinishTimer;
	private boolean mFinishRetrieveData;
	private ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		/*
		Intent intent = new Intent(SplashActivity.this, ShakeSecondActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		*/
		
		mProgressBar = (ProgressBar)findViewById(R.id.splash_progress);
		
		
		Utils.getScreenDendity(this);
		
		getScreenTextAsync();
		
	
		
		
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		mProgressBar.setVisibility(View.VISIBLE);
		
		
	    new Handler().postDelayed(new Runnable() {
	        public void run() {
	        	mFinishTimer = true;
	        	if (mFinishRetrieveData){
	        		mFinishTimer = false;
	        		StartTheActivity();
	        	}
	        	
	        }
	    }, _splashTime);
	
	    
	}
	

	private void getScreenTextAsync() {
		GetScreenInfoText getScreenInfoText = new GetScreenInfoText();
		getScreenInfoText.execute();
		
	}

	public Class<?> getNextActivity(){
		int stage = MySharedPreferences.getInstance().getStage(this);
       // stage = MySharedPreferences.STAGE_REG_CONGRATS;
		switch (stage) {
		case MySharedPreferences.STAGE_NEVER_RUN:
			return IntroActivity.class;
		
		case MySharedPreferences.STAGE_INTRO:
			return IntroActivity.class;
				
			
		case MySharedPreferences.STAGE_MAP:
			//return RegistrationActivity.class;
			return MapShowLocationActivity.class;
		
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

	


	
	
	private class GetScreenInfoText extends AsyncTask<String, Void, String> {


		private int mSrvrMinBuildNum;
		private int mSrvrCurrentBuildNum;


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(SplashActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {				
	
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
						
						
						mSrvrMinBuildNum = jsonReturnObj.getInt("min_build_num");
						mSrvrCurrentBuildNum = jsonReturnObj.getInt("current_build_num");
						MySharedPreferences.getInstance().saveBuildNum(SplashActivity.this, mSrvrCurrentBuildNum, mSrvrMinBuildNum);
						
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
			if (result.equals("success")){
				if (mFinishTimer){
 					mFinishRetrieveData = false;
 					StartTheActivity();
 				}
 				else
 					mFinishRetrieveData = true;
				/*
				mLocalBuildNum = Utils.getVersionCode(SplashActivity.this);
				       
				if (mSrvrMinBuildNum > mLocalBuildNum){
					AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
			     	alertDialog.setTitle(getString(R.string.splash_version_check_title));
			     	alertDialog.setMessage(getString(R.string.splash_version_check_les_min));
			        alertDialog.setCanceledOnTouchOutside(false);
				    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
				    	
				    	@Override
				        public void onClick(DialogInterface dialog, int which) {
			 				 dialog.cancel();
			 				 SplashActivity.this.finish();
			 				System.exit(0);
				    	}
				    });
				    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Open Google Play", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog,int which) {
							final Uri marketUri = Uri.parse("market://details?id=com.lazooz.lbm"); 
							startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
			            	dialog.cancel();
			 				SplashActivity.this.finish();
			 				System.exit(0);
			            }
			        });
				    alertDialog.show();

				}              
				else if (mSrvrCurrentBuildNum > mLocalBuildNum){
					AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
			     	alertDialog.setTitle(getString(R.string.splash_version_check_title));
			     	alertDialog.setMessage(getString(R.string.splash_version_check_les_current));
				    alertDialog.setCanceledOnTouchOutside(false);
				    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
				    	
				    	@Override
				        public void onClick(DialogInterface dialog, int which) {
			 				 dialog.cancel();
			 				 mFinishRetrieveData = false;
			 				 StartTheActivity();
				    	}
				    });
				    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Open Google Play", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog,int which) {
							final Uri marketUri = Uri.parse("market://details?id=com.lazooz.lbm"); 
							startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
			            	dialog.cancel();
			 				SplashActivity.this.finish();
			 				System.exit(0);
			            }
			        });
				    alertDialog.show();
				}
				else{
	 				if (mFinishTimer){
	 					mFinishRetrieveData = false;
	 					StartTheActivity();
	 				}
	 				else
	 					mFinishRetrieveData = true;
				}*/
			}
			
			else if (result.equals("ConnectionError")){
				Utils.displayConnectionError(SplashActivity.this, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SplashActivity.this.finish();
						
					}
				});
			}
			
				
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}

/*	 private void StartAnimations() { 
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
	*/
	

}
