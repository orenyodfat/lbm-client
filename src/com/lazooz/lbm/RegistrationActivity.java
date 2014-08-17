package com.lazooz.lbm;




import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.haarman.supertooltips.ToolTip;
import com.haarman.supertooltips.ToolTipRelativeLayout;
import com.haarman.supertooltips.ToolTipView;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class RegistrationActivity extends ActionBarActivity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener {
	
	
	private Button mRegBtn;

	private Button mConfBtn;
	private ProgressBar mProgBar;
	private String mPhoneNoInternational;
	private String mPhoneNoE164;
	private TextView mToolTipButton;
	private ToolTipView mToolTipView;
	private ToolTipRelativeLayout mToolTipFrameLayout;
	protected TextView mCntryCodeTV;
	private Spinner mCountrySpinner;

	public boolean mIsNewUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration);

		mToolTipFrameLayout = (ToolTipRelativeLayout) findViewById(R.id.tooltipframelayout);
		
		mToolTipButton = (TextView)findViewById(R.id.reg_text_tooltip_tv);
		mToolTipButton.setOnClickListener(this);
		
		
		
		mRegBtn = (Button)findViewById(R.id.reg_reg_btn);
		mRegBtn.setOnClickListener(new View.OnClickListener() {
			
			

			@Override
			public void onClick(View v) {
				
				final View addView = getLayoutInflater().inflate(R.layout.activation_input_country, null);

				TextView inText = (TextView)addView.findViewById(R.id.activation_in_text);
				inText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				String phone = Utils.getMyPhoneNum(RegistrationActivity.this);
				inText.setText(phone);

				mCountrySpinner = (Spinner)addView.findViewById(R.id.country_spnr);
				mCountrySpinner.setOnItemSelectedListener(new CountryOnItemSelectedListener());
				
				
				mCntryCodeTV = (TextView)addView.findViewById(R.id.cntry_code_tv);
				
				PhoneNumberUtil pu = PhoneNumberUtil.getInstance();
				
				
				try {
					String countryCode = getResources().getConfiguration().locale.getCountry();
					if (countryCode.equals("")){
						TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
						countryCode = tm.getSimCountryIso();			
					}
					if (!countryCode.equals("")){
						countryCode = countryCode.toUpperCase();
						String[] countyList = getResources().getStringArray(R.array.country_list_entry_values);
						if (Arrays.asList(countyList).contains(countryCode)){
							mCountrySpinner.setSelection(Arrays.asList(countyList).indexOf(countryCode));   
							 String countryCodeNum = pu.getCountryCodeForRegion(countryCode) + "";
							 mCntryCodeTV.setText("(+" + countryCodeNum+")");
						}
					}
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
				
				
				
				
				
		
				
	        	Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
	        	builder.setTitle(getString(R.string.reg_input_num_title));
	        	builder.setMessage(getString(R.string.reg_input_num_body));
	        	builder.setView(addView);
	        	builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	TextView t = (TextView) addView.findViewById(R.id.activation_in_text);
	                	String regNum = t.getText().toString();
	                	handleInputNum(regNum);
	                	dialog.cancel();
	                }
	        	
	            });
	        	
	        	builder.setNegativeButton(getString(android.R.string.cancel), null);
	        	builder.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);			
				
				
			}
		});
		
		
		
		mProgBar = (ProgressBar)findViewById(R.id.reg_progbar);
		mProgBar.setVisibility(View.GONE);
		
		
		mConfBtn = (Button)findViewById(R.id.reg_confirm_btn);
		mConfBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				final View addView = getLayoutInflater().inflate(R.layout.activation_input, null);
				TextView inText = (TextView)addView.findViewById(R.id.activation_in_text);
				inText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
	        	Builder builder = new AlertDialog.Builder(RegistrationActivity.this);

	        	builder.setTitle(getString(R.string.reg_input_conf_title));
	        	builder.setMessage(getString(R.string.reg_input_conf_body));
	        	builder.setView(addView);
	        	builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	TextView t = (TextView) addView.findViewById(R.id.activation_in_text);
	                	String confCode = t.getText().toString();
	                	performActivation(confCode);

	                	dialog.cancel();
	                }
	        	
	            });
	        	
	        	builder.setNegativeButton(getString(android.R.string.cancel), null);
	        	builder.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);			
				
				
			}
		});
		
		
		
		
		String actcode = getIntent().getStringExtra("ACTIVATION_CODE");
		if ((actcode != null)&&(!actcode.equals(""))){
			performActivation(actcode);
			return;
		}
		
		
		
	}
	
	private class CountryOnItemSelectedListener implements OnItemSelectedListener {
		 
	    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
	         
			String[] countyList = getResources().getStringArray(R.array.country_list_entry_values);
			String countryCode = countyList[pos];
			PhoneNumberUtil pu = PhoneNumberUtil.getInstance();
			String countryCodeNum = pu.getCountryCodeForRegion(countryCode) + "";
			mCntryCodeTV.setText("(+" + countryCodeNum+")");
	    }
	 
	    @Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	}
	
	private void handleInputNum(final String inputNum){
		PhoneNumberUtil pu = PhoneNumberUtil.getInstance();
		
		try {
			String[] countyList = getResources().getStringArray(R.array.country_list_entry_values);
			String countryCode = countyList[mCountrySpinner.getSelectedItemPosition()];
			
			PhoneNumber num1 = pu.parse(inputNum, countryCode);
			mPhoneNoInternational = pu.format(num1, PhoneNumberFormat.INTERNATIONAL);
			mPhoneNoE164 = pu.format(num1, PhoneNumberFormat.E164);
		} catch (NumberParseException e) {
			Log.e("CONTACT", "fail to convert number: " + inputNum);
			e.printStackTrace();
		}

		new AlertDialog.Builder(RegistrationActivity.this)
			.setTitle("Phone number verification")
			.setMessage("Register with: " + mPhoneNoInternational +" ?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					registerToServerAsync(mPhoneNoE164);
				}	
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
			})
			.show();
		
		
		
    	//registerToServerAsync(regNum);
    	
    	
    	
		
	}
	
	protected void performActivation(String activationCode) {
		String requestId = MySharedPreferences.getInstance().getRegRequestId(RegistrationActivity.this);
		registerValidationToServerAsync(requestId, activationCode);			
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


	
	private void registerToServerAsync(String cellnum){
		RegisterToServer registerToServer = new RegisterToServer();
		registerToServer.execute(cellnum);
	}
	
	
	private void registerValidationToServerAsync(String requestId, String token){
		RegisterValidationToServer registerValidationToServer = new RegisterValidationToServer();
		registerValidationToServer.execute(requestId, token);
	}
	
	public static String checkActivationFromSMS(Context context, String smsBody){
		String actCodeTemplateEng = context.getString(R.string.activation_code_template_eng);
		String activationCode = "";
		if(smsBody.contains(actCodeTemplateEng)){
			int start = smsBody.indexOf(actCodeTemplateEng);
			activationCode = smsBody.substring(start+actCodeTemplateEng.length()+1, start+actCodeTemplateEng.length()+10);
		}
		return activationCode;		
	}

	

	
	
	
	
	private class RegisterToServer extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
			String cellnum = params[0];
          	
          	ServerCom bServerCom = new ServerCom(RegistrationActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				bServerCom.registerToServer(cellnum);
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
						String requestId = jsonReturnObj.getString("registration_request_id");
						MySharedPreferences.getInstance().saveRegRequestId(RegistrationActivity.this, requestId);
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
			mProgBar.setVisibility(View.GONE);
			if (result.equals("success")){
				MySharedPreferences.getInstance().setStage(RegistrationActivity.this, MySharedPreferences.STAGE_REG_CELL_SENT_OK);
				Toast.makeText(RegistrationActivity.this, "Thanks you. We are sending you now the confirmation code.", Toast.LENGTH_LONG).show();
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			MySharedPreferences.getInstance().setStage(RegistrationActivity.this, MySharedPreferences.STAGE_REG_CELL_SENT);
			mProgBar.setVisibility(View.VISIBLE);
		}
	}
		
	private class RegisterValidationToServer extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
			String requestId = params[0];
			String token = params[1];
          	ServerCom bServerCom = new ServerCom(RegistrationActivity.this);
        	
          	genKeyPair();
          	
            String publicKey = MySharedPreferences.getInstance().getPublicKey(RegistrationActivity.this);  
        	JSONObject jsonReturnObj=null;
			try {
				bServerCom.registerValidationToServer(requestId, token, publicKey);
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
						String userId = jsonReturnObj.getString("user_id");
						String userSecret = jsonReturnObj.getString("user_secret");
						mIsNewUser = Utils.yesNoToBoolean(jsonReturnObj.getString("is_new_user"));

						MySharedPreferences.getInstance().saveActivationData(RegistrationActivity.this, userId, userSecret);
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
			mProgBar.setVisibility(View.GONE);	
			if (result.equals("success")){
				MySharedPreferences.getInstance().setStage(RegistrationActivity.this, MySharedPreferences.STAGE_REG_CONF_SENT_OK);
				startNextScreen();
			}
			else{
				Toast.makeText(RegistrationActivity.this, "Confirmation failed, please check the code", Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected void onPreExecute() {
			MySharedPreferences.getInstance().setStage(RegistrationActivity.this, MySharedPreferences.STAGE_REG_CONF_SENT);
			mProgBar.setVisibility(View.VISIBLE);
		}
			
			
	}
	
	
	private void genKeyPair(){
		KeyPairGenerator kpg;
		try {
			/*
	        ECKey eck = new ECKey();
	        Address PubKey = eck.toAddress(NetworkParameters.prodNet());
	        String publicKey = PubKey.toString();
	        String privateKey = eck.getPrivateKeyEncoded(NetworkParameters.prodNet()).toString();
		      */  
			
			
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(4096);
			KeyPair keyPair = kpg.genKeyPair();
			
			String privateKey = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT);
			String publicKey = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT);
			
			MySharedPreferences.getInstance().saveKeyPair(this, privateKey, publicKey);
			
			//getExternalStorageDirectory  
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private void startNextScreen(){
		Utils.freezOrientation(this);
		if(mIsNewUser){
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		    alertDialog.setCanceledOnTouchOutside(false);
		    alertDialog.setTitle("Confirmation succeed");
		    alertDialog.setMessage("Congratulation, you've got yourself a new digital wallet!");
		    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		    	
		    	@Override
		        public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(RegistrationActivity.this, CongratulationsRegActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("NEW_USER", true);
					startActivity(intent);
					dialog.cancel();
					RegistrationActivity.this.finish();					        
		    	}
		    });
		    alertDialog.show();
			
		}
		else {
			
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		    alertDialog.setCanceledOnTouchOutside(false);
		    alertDialog.setTitle("Confirmation succeed");
		    alertDialog.setMessage("Welcome Back!");
		    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		    	
		    	@Override
		        public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("NEW_USER", false);
					startActivity(intent);
					dialog.cancel();
					RegistrationActivity.this.finish();					        
		    	}
		    });
		    alertDialog.show();
			
		}

		
//		startActivity(intent);
//		finish();		
	}
	
	
	  private void addPurpleToolTipView() {
		  /*    	
		      	mToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip()
		                          .withContentView(LayoutInflater.from(this).inflate(R.layout.custom_tooltip, null))
		                          .withColor(getResources().getColor(R.color.holo_purple)), mToolTipButton);
		      	mToolTipView.setOnToolTipViewClickedListener(this);
		  */    	
		      	mToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip()
		                           .withText("Right now your are mining potention Zooz which will be converted to real Zooz one the network is established and the authentication of")
		                           .withColor(getResources().getColor(R.color.holo_green_light)), mToolTipButton);
		      	mToolTipView.setOnToolTipViewClickedListener(this);
	  }
		  	
		      
		     
		      
		      
		  	
		

	  @Override
	  public void onToolTipViewClicked(ToolTipView toolTipView) {
		  mToolTipView = null;
	  }

	  @Override
	  public void onClick(View view) {
		  
		  if (mToolTipView == null) {
			  addPurpleToolTipView();
	      }else {
				mToolTipView.remove();
				mToolTipView = null;
	        }
	  }


	
	
	
}
