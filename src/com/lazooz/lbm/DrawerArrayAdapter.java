package com.lazooz.lbm;



import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrawerArrayAdapter extends ArrayAdapter<String> {

	private Context mContext;
	private int id;
	private String[] mObjects;

	public DrawerArrayAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(context));
		
		mContext = context;
		id = resource;
		mObjects = objects;
	}

	public String getItem(int i)
	 {
		 return mObjects[i];
		 
	 }

	 @Override
     public View getView(int position, View convertView, ViewGroup parent){
		 boolean hasPrev;
         View v = convertView;
         if (v == null) {
             LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             v = vi.inflate(id, null);
         }
         
         final String s = getItem(position);
         if (s != null) {
        	TextView t1 = (TextView) v.findViewById(R.id.item_textview);
          	t1.setText(s);
        	t1.setBackgroundResource(R.drawable.selector_drawer_item);
          	t1.setEnabled(true);              		
           	t1.setTextColor(mContext.getResources().getColor(R.color.the_setting_entry_text));
          	 
         }

		 
		 return v;
	 }
	
}
