package com.lazooz.lbm;




import org.json.JSONException;
import org.json.JSONObject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends ActionBarActivity {
	
	
	private Button mRegBtn;
	//public String mRequestId;
	//public String mUserId;
	//public String mUserSecret;
	private Button mConfBtn;
	private ProgressBar mProgBar;
	private String mPhoneNoInternational;
	private String mPhoneNoE164;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration);

		mRegBtn = (Button)findViewById(R.id.reg_reg_btn);
		mRegBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//ApManager.configApState(RegistrationActivity.this);
				

				
				
				
				
				final View addView = getLayoutInflater().inflate(R.layout.activation_input, null);
				TextView inText = (TextView)addView.findViewById(R.id.activation_in_text);
				inText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				String phone = Utils.getMyPhoneNum(RegistrationActivity.this);
				inText.setText(phone);
				
	        	Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
	        	//Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
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
				inText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
	        	Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
	        	//Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
	        	builder.setTitle(getString(R.string.reg_input_conf_title));
	        	builder.setMessage(getString(R.string.reg_input_conf_body));
	        	builder.setView(addView);
	        	builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	TextView t = (TextView) addView.findViewById(R.id.activation_in_text);
	                	String confCode = t.getText().toString();
	                	performActivation(confCode);
	                	//startNextScreen();
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
	
	
	private void handleInputNum(final String inputNum){
		String currentLocale = Utils.getCurrentLocale(this);
		PhoneNumberUtil pu = PhoneNumberUtil.getInstance();
		//String phoneNoInternational  = num;
		try {
			PhoneNumber num1 = pu.parse(inputNum, currentLocale);
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
			activationCode = smsBody.substring(start+actCodeTemplateEng.length()+1, start+actCodeTemplateEng.length()+9);
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
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				bServerCom.registerValidationToServer(requestId, token);
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
	
	
	private void startNextScreen(){
		startActivity(new Intent(RegistrationActivity.this, CongratulationsRegActivity.class));
		finish();		
	}
	
}
