package com.lazooz.lbm.businessClasses;

import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.utils.Utils;

public class ContactFriend {

	private String mCellPhone;
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
			mIsInstalled = Utils.yesNoToBoolean(jsonObj.getString("is_installed"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		JSONObject retObj = new JSONObject();
		
		try {
			retObj.put("cellphone", mCellPhone);
			retObj.put("is_installed", Utils.booleanToYesNo(mIsInstalled));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retObj;
	
	
	}
	
	
}
