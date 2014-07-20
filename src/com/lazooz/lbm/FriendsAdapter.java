package com.lazooz.lbm;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.internal.ac;
import com.lazooz.lbm.businessClasses.Contact;
import com.lazooz.lbm.businessClasses.ContactFriend;
import com.lazooz.lbm.businessClasses.ContactFriendList;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class FriendsAdapter extends ArrayAdapter<ContactFriend>  {

	private Activity activity;
	//private List<Contact> items;
	
	private List<ContactFriend> mContactFriendsList;
	  
	private HashMap<String, Contact> mContactList;
	
	private int row;
	private ContactFriend objBean;

	
	private Object mContext;

	
	
	
	
	
	public FriendsAdapter(Activity act, int row, ContactFriendList items, HashMap<String, Contact> contactList) {
		super(act, row, items.getContacts());

		mContactFriendsList = items.getContacts();
		mContactList = contactList;
		
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

		if ((mContactFriendsList == null) || ((position + 1) > mContactFriendsList.size()))
			return view;

		objBean = mContactFriendsList.get(position);
		
		
		Contact contact = mContactList.get(objBean.getCellPhone());
		

		holder.nameTV = (TextView) view.findViewById(R.id.friend_name_tv);
		
		if (objBean.isInstalled())
			holder.nameTV.setBackgroundResource(activity.getResources().getColor(R.color.green));

		if (contact != null){
			holder.nameTV.setText(Html.fromHtml(contact.getName()));
		}
		else
			holder.nameTV.setText(Html.fromHtml(objBean.getCellPhone()));
		
		return view;
	}

	public class ViewHolder {
		public TextView nameTV;
	}

}
