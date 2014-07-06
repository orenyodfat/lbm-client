package com.lazooz.lbm;



import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LbmService extends Service {

	private Timer twoSecondsTimer;
	private Timer oneMinTimer;

	public LbmService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    //Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
		
		//Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		twoSecondsTimer = new Timer();
		TimerTask twoSecondsTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryTwoSeconds();				
				}
			};
		twoSecondsTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 2*1000);
		

		oneMinTimer = new Timer();
		TimerTask oneMinTimerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryOneMin();				
				}
			};
		oneMinTimer.scheduleAtFixedRate(oneMinTimerTask, 0, 1*60*1000);
		
		
		
		return Service.START_STICKY;
	}

	protected void checkEveryOneMin() {
		// TODO Auto-generated method stub
		
	}

	protected void checkEveryTwoSeconds() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
