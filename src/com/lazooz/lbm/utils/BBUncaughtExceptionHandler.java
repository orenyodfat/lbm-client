package com.lazooz.lbm.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BBUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private Context mContext;



	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
        
		
		
		 StringWriter stackTrace = new StringWriter();
		 ex.printStackTrace(new PrintWriter(stackTrace));
	     System.err.println(stackTrace);// You can use LogCat too
	     String s = stackTrace.toString();
	     
	     Log.e("uncaughtException", s);
	     
	     OfflineActivities.getInstance(mContext.getApplicationContext()).setExceptionData(s);   
	        
	        
		Intent i = mContext.getPackageManager()
	             .getLaunchIntentForPackage( mContext.getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mContext.startActivity(i);
	
		
		android.os.Process.killProcess(android.os.Process.myPid());
	    System.exit(0);
	        
	}

	
	
	public BBUncaughtExceptionHandler(Context context){
		super();
		mContext = context;
	}
	
}
