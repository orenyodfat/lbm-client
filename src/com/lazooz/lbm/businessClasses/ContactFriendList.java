package com.lazooz.lbm.businessClasses;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class ContactFriendList {
	private List<ContactFriend> mContacts;
	
	public ContactFriendList(JSONArray contactsArray) {
	
		mContacts = new ArrayList<ContactFriend>();
		
		for (int i=0; i<contactsArray.length(); i++) {
		    try {
		    	JSONObject obj = (JSONObject)contactsArray.get(i);
		    	ContactFriend cf = new ContactFriend(obj);
		    	mContacts.add(cf);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
	}

	public List<ContactFriend> getContacts() {
		return mContacts;
	}

	


}
