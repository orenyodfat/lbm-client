package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class Contact {
	private String mName;
	private String mPhoneNo;
	private String mPhoneNoInternational;
	private boolean mSelected;
	private String mCountryCode;
	
	public String getKey(){
		return mName + mPhoneNo;
	}
	
	public Contact(String countryCode){
		mCountryCode = countryCode;
	}
	
	public Contact(JSONObject jsonObj, String countryCode){
		try {
			mCountryCode = countryCode;
			mName = jsonObj.getString("name");
			mPhoneNo = jsonObj.getString("cellphone");
			mSelected = jsonObj.getBoolean("selected");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
		
			
			PhoneNumberUtil pu = PhoneNumberUtil.getInstance();
			try {
				PhoneNumber num = pu.parse(mPhoneNo, mCountryCode);
				mPhoneNoInternational = pu.format(num, PhoneNumberFormat.INTERNATIONAL);
			} catch (NumberParseException e) {
				e.printStackTrace();
			}
			
			retObj.put("name", mName);
			retObj.put("cellphone", mPhoneNo);
			retObj.put("cellphone_int", mPhoneNoInternational);
			retObj.put("selected", mSelected);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	}

	                              
	                              
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public String getPhoneNo() {
		return mPhoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.mPhoneNo = phoneNo;
	}
	public boolean isSelected() {
		return mSelected;
	}
	public void setSelected(boolean selected) {
		this.mSelected = selected;
	}
	
	
}
