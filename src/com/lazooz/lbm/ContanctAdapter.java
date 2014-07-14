package com.lazooz.lbm;

import java.util.List;

import com.lazooz.lbm.businessClasses.Contact;


import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ContanctAdapter extends ArrayAdapter<Contact> {

	private Activity activity;
	private List<Contact> items;
	private int row;
	private Contact objBean;

	private OnCheckedListener mOnCheckedListener;

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

		this.activity = act;
		this.row = row;
		this.items = items;

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

		if ((items == null) || ((position + 1) > items.size()))
			return view;

		objBean = items.get(position);

		holder.nameTV = (TextView) view.findViewById(R.id.contact_name_tv);
		holder.phoneNoTV = (TextView) view.findViewById(R.id.contact_phone_tv);
		holder.selectedCB = (CheckBox) view.findViewById(R.id.contact_selected_cb);
		holder.hasAppIV = (ImageView) view.findViewById(R.id.contact_hasapp_iv);

		
		

		holder.selectedCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	  CheckBox cb = (CheckBox)buttonView;
        	  int pos = (Integer)cb.getTag();
        	  Contact contact = items.get(position);
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
		
		return view;
	}


	public class ViewHolder {
		public TextView nameTV, phoneNoTV;
		public CheckBox selectedCB;
		public ImageView hasAppIV;
	}

}
