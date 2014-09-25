package com.lazooz.lbm.businessClasses;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SMSList {
	private ArrayList<SMS> smss = new ArrayList<SMS>();
	 
 	public ArrayList<SMS> getSMSs() {
 		return smss;
 	}
 
 	public void setSMSs(ArrayList<SMS> smss) {
 		this.smss = smss;
 	}
 	
 	public void addSMS(SMS sms) {
 		this.smss.add(sms);
 	}
  	
 	public SMSList() {
 		
 	}
 	
 	
 	public JSONObject toJSON(){
 		JSONObject obj; 
 		JSONObject retObj = new JSONObject();
 		JSONArray jsArray;
 		try {
 			jsArray = new JSONArray();
 			int size = getSMSs().size();
 			for(int i=0; i<size;i++){
 				obj = getSMSs().get(i).toJSON();
 				jsArray.put(obj);
 			}
 			
 			retObj.put("smss", jsArray);
 		}
 		catch (JSONException e) {}
		
		return retObj;
 	}
 	
 	public SMSList(JSONObject jsonObj) {
 		JSONArray ja;
 		try{
 			this.smss = new ArrayList<SMS>();
			ja = jsonObj.getJSONArray("smss");
			for (int i=0; i<ja.length(); i++){
				JSONObject jo = (JSONObject)ja.get(i);
				SMS c = new SMS(jo);
				this.smss.add(c);
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
 	}
}
