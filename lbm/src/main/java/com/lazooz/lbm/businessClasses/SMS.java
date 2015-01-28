package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class SMS {
	private String mAddress;
 	private String mBody;
 	private String mDate;
	public String getAddress() {
		return mAddress;
	}
	public void setAddress(String address) {
		this.mAddress = address;
	}
	public String getBody() {
		return mBody;
	}
	public void setBody(String body) {
		this.mBody = body;
	}
	public String getDate() {
		return mDate;
	}
	public void setDate(String date) {
		this.mDate = date;
	}
 	
 	public SMS(String body, String date, String address) {
 		this.mDate = date;
 		this.mAddress = address;
 		this.mBody = body;
 	}
	
 	public SMS(JSONObject jsonObj) throws JSONException {
 		this.mDate = jsonObj.getString("date");
 		this.mAddress = jsonObj.getString("address");
 		this.mBody = jsonObj.getString("body");
 	}
	
 	public JSONObject toJSON(){
 		JSONObject obj = new JSONObject();
 		try {
			obj.put("date", this.mDate);
			obj.put("address", this.mAddress);
			obj.put("body", this.mBody);
		}
 		catch (JSONException e) {}		
 		return obj;
 	}
	
	
}
