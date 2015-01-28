package com.lazooz.lbm;


import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {

		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(context));
		
		if (MySharedPreferences.getInstance().wasInitFirstTime(context)){
			Intent startServiceIntent = new Intent(context, LbmService.class);
			context.startService(startServiceIntent);
		}
        

    }

}
