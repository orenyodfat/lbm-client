package com.lazooz.lbm;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.lazooz.lbm.businessClasses.ContactFriend;
import com.lazooz.lbm.businessClasses.ContactFriendList;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

import java.util.List;


public class FriendsAdapter extends ArrayAdapter<ContactFriend>  {

	private Activity activity;
	//private List<Contact> items;
	
	private List<ContactFriend> mContactFriendsList;
	  
	//private HashMap<String, Contact> mContactList;
	
	private int row;
	private ContactFriend objBean;

	
	private Object mContext;

	
	
	
	
	
	//public FriendsAdapter(Activity act, int row, ContactFriendList items, HashMap<String, Contact> contactList) {
	public FriendsAdapter(Activity act, int row, ContactFriendList items) {
		super(act, row, items.getContacts());
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(act));
		
		mContactFriendsList = items.getContacts();
		//mContactList = contactList;
		
		this.activity = act;
		this.row = row;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(row, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		//if ((mContactFriendsList == null) || ((position + 1) > mContactFriendsList.size()))
			//return view;

		objBean = mContactFriendsList.get(position);
		
		
		//Contact contact = mContactList.get(objBean.getCellPhone());
		

		holder.nameTV = (TextView) view.findViewById(R.id.friend_name_tv);
		
		if (objBean.isInstalled())
			holder.nameTV.setBackgroundResource(R.color.green);

		holder.nameTV.setText(Html.fromHtml(objBean.getName()));
		
		return view;
	}

	public class ViewHolder {
		public TextView nameTV;
	}

}
