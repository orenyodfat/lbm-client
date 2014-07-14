package com.lazooz.lbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.lazooz.lbm.ContanctAdapter.OnCheckedListener;
import com.lazooz.lbm.businessClasses.Contact;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.Utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

public class ContactListActivity extends Activity implements
		OnItemClickListener, OnCheckedListener {

	private ListView listView;
	private List<Contact> list = new ArrayList<Contact>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(this);

		String currentLocale = Utils.getCurrentLocale(this);
		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
		while (phones.moveToNext()) {

			String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			Contact objContact = new Contact(currentLocale);
			objContact.setName(name);
			objContact.setPhoneNo(phoneNumber);
			list.add(objContact);

		}
		phones.close();

		MySharedPreferences.getInstance().clearRecommendUsers(this);

		
		ContanctAdapter objAdapter = new ContanctAdapter(this, R.layout.contact_row, list);
		objAdapter.setOnCheckedListener(this);
		
		listView.setAdapter(objAdapter);

		if (null != list && list.size() != 0) {
			Collections.sort(list, new Comparator<Contact>() {

				@Override
				public int compare(Contact lhs, Contact rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});

		} else {
			showToast("No Contact Found!!!");
		}
	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		Contact bean = (Contact) listview.getItemAtPosition(position);
		
		
		CheckBox cb = (CheckBox) v.findViewById(R.id.contact_selected_cb);
		if (cb != null){
			cb.setChecked(!cb.isChecked());
		}
		
		
		
		
	}

	@Override
	public void onChecked(boolean isChecked, Contact contactBean) {
		MySharedPreferences msp = MySharedPreferences.getInstance();
		if (isChecked){
			msp.addRecommendUser(this, contactBean);
			if(msp.areThere3RecommendUser(this)){
				Intent returnIntent = new Intent();
				returnIntent.putExtra("ACTIVITY", "ContactListActivity");
				setResult(RESULT_OK,returnIntent);
				finish();
			}
		}
		else
			MySharedPreferences.getInstance().removeRecommendUser(this, contactBean);
		
	}

	
}
