package com.lazooz.lbm;



import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.businessClasses.SMS;
import com.lazooz.lbm.businessClasses.SMSList;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("MySMSReceiver", "recieve sms");
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(context));
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
			String friendsCode = checkRecommendationFromSMSList(context);
			
			Intent myIntent = new Intent(context.getApplicationContext(), RegistrationActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			myIntent.putExtra("ACTIVATION_CODE", actdode);
			myIntent.putExtra("RECOMMENDATION_CODE", friendsCode);
			context.startActivity(myIntent);
		}

		
//		Toast toast = Toast.makeText(context,"Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
//		toast.show();
	}
	
	
  
	private String checkRecommendationFromSMSList(Context context){
		SMSList slInbox1 = newSMSListFromLastWeek(context, "content://sms/inbox");
		String friendsCode = "";
	
		for (SMS sms : slInbox1.getSMSs()){
			String theBody = sms.getBody();
			if (RegistrationActivity.hasFriendRecommendationFromSMS(context, theBody)){
				friendsCode = RegistrationActivity.checkFriendRecommendationFromSMS(context, theBody);
				break;
			}

		}
		return friendsCode;
	}
	
	
	public SMSList newSMSListFromLastWeek(Context context, String uriString){
 		SMSList smsList = new SMSList();
 		ContentResolver cr = context.getContentResolver();
 		try {
			Uri uriSMSURI = Uri.parse(uriString);
//			Cursor cur = cr.query(uriSMSURI, new String[]{"_id", "address", "date", "body"}, "date BETWEEN datetime('now', ' -6 days') AND datetime('now', 'localtime')", null, null);
			Cursor cur = cr.query(uriSMSURI, new String[]{"_id", "address", "date", "body"}, null, null, null);
			String id="";
			String addr="";
			String date="";
			String body="";
			int max = cur.getCount();
			int i = 0;
			boolean cancle = false;
			while (cur.moveToNext()) {
				//if (this.mOnProgressListener != null)
					//cancle = mOnProgressListener.onProgress(max, i++, mContext.getString(R.string.backup_backup_sms));
				//if (cancle)
					//break;
				id = cur.getString(0);
				addr = cur.getString(1);
				date = cur.getString(2);
				body = cur.getString(3);
				SMS s = new SMS(body, date, addr);
				smsList.addSMS(s);
			  }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		return smsList;
 	}
}
		

