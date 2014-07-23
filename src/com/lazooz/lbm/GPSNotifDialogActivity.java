package com.lazooz.lbm;

import com.lazooz.lbm.preference.MySharedPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class GPSNotifDialogActivity extends Activity {

	private CheckBox mDontShowCB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		final View view  = getLayoutInflater().inflate(R.layout.activation_gps_notif_dlg, null);
		
     	
     	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
     	alertDialog.setTitle(getString(R.string.gps_notif_open_gps_title));
     	alertDialog.setMessage(getString(R.string.gps_notif_open_gps_body));
     	alertDialog.setView(view);
	    alertDialog.setCanceledOnTouchOutside(false);
	    
	    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
	    	
	    	@Override
	        public void onClick(DialogInterface dialog, int which) {
	    		 CheckBox cb = (CheckBox)view.findViewById(R.id.dont_show_cb);
	    		 MySharedPreferences.getInstance().setDefaultGPSNotif(GPSNotifDialogActivity.this, cb.isChecked(), System.currentTimeMillis());
 				 dialog.cancel();
 				 GPSNotifDialogActivity.this.finish();				        
	    	}
	    });
	    
	    
	    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.gps_activate_gps_setting), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
	    		 CheckBox cb = (CheckBox)view.findViewById(R.id.dont_show_cb);
    			 MySharedPreferences.getInstance().setDefaultGPSNotif(GPSNotifDialogActivity.this, cb.isChecked(), System.currentTimeMillis());
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	startActivity(intent);
            	dialog.cancel();
            	GPSNotifDialogActivity.this.finish();
            }
        });
	    
	    
	    
	    alertDialog.show();

	    
	    
     	
     	
     	
		
	}
		
		




}
