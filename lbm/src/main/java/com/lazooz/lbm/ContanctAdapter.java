package com.lazooz.lbm;

import java.util.ArrayList;
import java.util.List;

import com.lazooz.lbm.businessClasses.Contact;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.Utils;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


public class ContanctAdapter extends ArrayAdapter<Contact> implements Filterable {

	private Activity activity;
	//private List<Contact> items;
	
	private ArrayList<Contact> mOriginalList;
	private ArrayList<Contact> mContactList;
	  
	
	private int row;
	private Contact objBean;

	private OnCheckedListener mOnCheckedListener;
	private Object mContext;

	public interface OnCheckedListener {
        public void onChecked(boolean isChecked, Contact objBean);
    }
	
	
	public OnCheckedListener getOKListener() {
		return mOnCheckedListener;
	}

	public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
		this.mOnCheckedListener = onCheckedListener;
	}
	
	
	
	
	public ContanctAdapter(Activity act, int row, List<Contact> items) {
		super(act, row, items);

		mContactList = new ArrayList<Contact>();
		mContactList.addAll(items);
		mOriginalList = new ArrayList<Contact>();
		mOriginalList.addAll(items);
		
		
		this.activity = act;
		this.row = row;
		//this.items = items;
		//CreateList(null);

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

		if ((mContactList == null) || ((position + 1) > mContactList.size()))
			return view;

		objBean = mContactList.get(position);

		holder.nameTV = (TextView) view.findViewById(R.id.contact_name_tv);
		holder.phoneNoTV = (TextView) view.findViewById(R.id.contact_phone_tv);
		holder.selectedCB = (CheckBox) view.findViewById(R.id.contact_selected_cb);
		holder.hasAppIV = (ImageView) view.findViewById(R.id.contact_hasapp_iv);
		holder.hasSentInvIV = (ImageView) view.findViewById(R.id.contact_hassentinv_iv);
		
		
		

		holder.selectedCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	  CheckBox cb = (CheckBox)buttonView;
        	  Contact contact = mContactList.get(position);
        	  contact.setSelected(buttonView.isChecked());
         	 if (mOnCheckedListener != null)
         		 mOnCheckedListener.onChecked(buttonView.isChecked(), contact);
          }
        });
		
		
		

		if (holder.nameTV != null && null != objBean.getName()
				&& objBean.getName().trim().length() > 0) {
			holder.nameTV.setText(Html.fromHtml(objBean.getName()));
		}
		if (holder.phoneNoTV != null && null != objBean.getPhoneNo()
				&& objBean.getPhoneNo().trim().length() > 0) {
			holder.phoneNoTV.setText(Html.fromHtml(objBean.getPhoneNo()));
		}
		
		if (holder.selectedCB != null){
			holder.selectedCB.setChecked(objBean.isSelected());
			holder.selectedCB.setTag(position);
			holder.selectedCB.setEnabled(!objBean.hasApp());
		}
		
		
		
		if(holder.hasAppIV != null){
			if (objBean.hasApp())
				holder.hasAppIV.setVisibility(View.VISIBLE);
			else
				holder.hasAppIV.setVisibility(View.INVISIBLE);
		}
		
		if(holder.hasSentInvIV != null){
			holder.hasSentInvIV.setVisibility(View.INVISIBLE);
			if (!objBean.hasApp()){
				if (objBean.hasSentInv())
					holder.hasSentInvIV.setVisibility(View.VISIBLE);
			}
		}

		return view;
	}


	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return new Filter() {
			
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				//ArrayList<Contact> contats = (ArrayList<Contact>)results.values;
				mContactList = (ArrayList<Contact>)results.values;

				notifyDataSetChanged();
				clear();
				if (mContactList!=null)
				{
				 for(Contact contact : mContactList)
				  	add(contact);
				}
				
				notifyDataSetInvalidated();
				
			}
			
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				//constraint = constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				if(constraint != null && constraint.toString().length() > 0)
				{
					constraint = constraint.toString().toLowerCase();
					List<Contact> filteredItems = new ArrayList<Contact>();
				 
					for(Contact contact : mOriginalList){
				    	if(contact.getName().toLowerCase().contains(constraint))
				    		filteredItems.add(contact);						
					}
					
				    result.count = filteredItems.size();
				    result.values = filteredItems;
				}
				else{
				     synchronized(this)
				     {
				    	 result.values = mOriginalList;
				    	 result.count = mOriginalList.size();
				     }
				}
			    return result;
			}
		};
	}
	
	
	
	public class ViewHolder {
		public TextView nameTV, phoneNoTV;
		public CheckBox selectedCB;
		public ImageView hasAppIV;
		public ImageView hasSentInvIV;
	}

}
