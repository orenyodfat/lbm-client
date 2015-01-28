package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.utils.Utils;

public class ContactFriend {

	private String mCellPhone;
	private String mName;
	private boolean mIsInstalled;
	public ContactFriend() {
		
		
	}
	public String getCellPhone() {
		return mCellPhone;
	}
	public void setCellPhone(String cellPhone) {
		this.mCellPhone = cellPhone;
	}
	public boolean isInstalled() {
		return mIsInstalled;
	}
	public void setInstalled(boolean isInstalled) {
		this.mIsInstalled = isInstalled;
	}

	
	public ContactFriend(JSONObject jsonObj){
		try {
			mCellPhone = jsonObj.getString("cellphone");
			mName = jsonObj.getString("name");
			mIsInstalled = Utils.yesNoToBoolean(jsonObj.getString("is_installed"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
			retObj.put("cellphone", mCellPhone);
			retObj.put("name", mName);
			retObj.put("is_installed", Utils.booleanToYesNo(mIsInstalled));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	
	
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	
	
}
