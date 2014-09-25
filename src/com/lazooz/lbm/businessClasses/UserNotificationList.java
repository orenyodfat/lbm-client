package com.lazooz.lbm.businessClasses;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class UserNotificationList {
	private List<UserNotification> mList;
	
	public UserNotificationList() {
		mList = new ArrayList<UserNotification>();		
	}

	public UserNotificationList(JSONArray array) {
	
		mList = new ArrayList<UserNotification>();
		
		for (int i=0; i<array.length(); i++) {
		    try {
		    	JSONObject obj = (JSONObject)array.get(i);
		    	UserNotification un = new UserNotification(obj);
		    	mList.add(un);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
	}
	
 	public JSONArray toJSON(){
 		JSONObject obj; 
 		JSONArray jsArray = new JSONArray();
		int size = mList.size();
		for(int i=0; i<size;i++){
			obj = mList.get(i).toJSON();
			jsArray.put(obj);
		}
		
		return jsArray;
 	}

	public List<UserNotification> getNotifications() {
		return mList;
	}

	


}
