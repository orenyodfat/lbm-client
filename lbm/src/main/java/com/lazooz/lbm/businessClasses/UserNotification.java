package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.lazooz.lbm.MainActivity;
import com.lazooz.lbm.R;
import com.lazooz.lbm.utils.Utils;

public class UserNotification {

	private String mBody;
	private String mTitle;
	private int mNum;
	private boolean mIsNotif;
	private boolean mIsPopup;
	private String mType;
	

	public UserNotification() {
	}

	
	public UserNotification(JSONObject jsonObj){
		try {
			mBody = jsonObj.getString("body");
			mTitle = jsonObj.getString("title");
			mNum = jsonObj.getInt("num");
			mIsNotif = Utils.yesNoToBoolean(jsonObj.getString("is_notification"));
			mIsPopup = Utils.yesNoToBoolean(jsonObj.getString("is_popup"));
			mType = jsonObj.getString("type");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
			retObj.put("body", mBody);
			retObj.put("title", mTitle);
			retObj.put("type", mType);
			retObj.put("num", mNum);
			retObj.put("is_notification", Utils.booleanToYesNo(mIsNotif));
			retObj.put("is_popup", Utils.booleanToYesNo(mIsPopup));			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}


	public void displayNotifBar(Context context){
		Intent notifIntent = new Intent(context, MainActivity.class);
		Utils.sendNotifications(context, R.drawable.ic_stat_notif_logo, "Message from La'zooz", mTitle, mBody, notifIntent, true);
	}

	public void displayPopup(Context context) {
		Utils.messageToUser(context, mTitle, mBody);
	}

	
	public String getBody() {
		return mBody;
	}


	public void setBody(String body) {
		mBody = body;
	}


	public String getTitle() {
		return mTitle;
	}


	public void setTitle(String title) {
		mTitle = title;
	}


	public int getNum() {
		return mNum;
	}


	public void setNum(int num) {
		mNum = num;
	}


	public boolean isNotif() {
		return mIsNotif;
	}


	public void setIsNotif(boolean isNotif) {
		mIsNotif = isNotif;
	}


	public boolean isPopup() {
		return mIsPopup;
	}


	public void setIsPopup(boolean isPopup) {
		mIsPopup = isPopup;
	}


	public String getType() {
		return mType;
	}


	public void setType(String type) {
		mType = type;
	}




	
	
	
	
	
}
