package com.lazooz.lbm;

import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.preference.MySharedPreferences;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("MySMSReceiver", "recieve sms");
		
		//Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(context));
		
		mContext = context;
		
		int stage = MySharedPreferences.getInstance().getStage(context);
		if(stage >= MySharedPreferences.STAGE_REG_CONF_SENT_OK)
			return;

		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}
		
		
		 

		String smsBody = smsMessage[0].getMessageBody();
		String smsSender = smsMessage[0].getOriginatingAddress();
		
		
		String actdode = RegistrationActivity.checkActivationFromSMS(context, smsBody);
		if (!actdode.equals("")){
			//Toast.makeText(context, context.getString(R.string.registration_recieve_registration_sms), Toast.LENGTH_LONG).show();
			Intent myIntent = new Intent(context.getApplicationContext(), RegistrationActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			myIntent.putExtra("ACTIVATION_CODE", actdode);
			context.startActivity(myIntent);
		}

		
//		Toast toast = Toast.makeText(context,"Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
//		toast.show();
	}
	
	
  
	
	
	
	
}
		

