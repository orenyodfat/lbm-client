package com.lazooz.lbm;




import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.businessClasses.ServerData;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.Utils;
import com.lazooz.lbm.utils.ChartUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends MyActionBarActivity {
	
	private Timer ShortPeriodTimer;
	private TextView mDistanceTV;
	private TextView mZoozBalTV;
	private LinearLayout mLayoutChart1;
	private LinearLayout mLayoutChart2;
	private GraphicalView mChartView1;
	private GraphicalView mChartView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState, R.layout.activity_main);
		//setContentView(R.layout.activity_main);

		
		startService(new Intent(this, LbmService.class));
		
		mDistanceTV = (TextView)findViewById(R.id.main_distance_tv);
		mDistanceTV.setText("0.0");
		mZoozBalTV = (TextView)findViewById(R.id.main_zoz_balance_tv);
		mZoozBalTV.setText("0.0");
		
		
		
		 final Handler guiHandler = new Handler();
		 final Runnable guiRunnable = new Runnable() {
		      public void run() {
		         UpdateGUI();
		      }
		   };
		
		
		ShortPeriodTimer = new Timer();
		TimerTask twoSecondsTimerTask = new TimerTask() {
				@Override
				public void run() {
					guiHandler.post(guiRunnable);				
				}
			};
		ShortPeriodTimer.scheduleAtFixedRate(twoSecondsTimerTask, 0, 10*1000);

		
		boolean selectContacts = getIntent().getBooleanExtra("SELECT_CONTACTS", false);
		if(selectContacts){
			MySharedPreferences.getInstance().setStage(this, MySharedPreferences.STAGE_MAIN_NO_GET_FRIENDS);
    		Intent i = new Intent(this, ContactListActivity.class);
    		startActivityForResult(i, 1);			
		}
		
		startOnDayScheduler();
		
		
		mLayoutChart1 = (LinearLayout)findViewById(R.id.report_chart_1);
		mLayoutChart2 = (LinearLayout)findViewById(R.id.report_chart_2);
		
		buildChart1();
		buildChart2();
	}

	
	private void startOnDayScheduler() {
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(this, AlarmOneDaySchedReciever.class);
		PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		Random r = new Random();
		int delay = r.nextInt(1000);
		
		//alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + delay, 24*60*60*1000, pintent);
		//alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + delay, 3*60*1000, pintent);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , 60*1000, pintent);

		
	}
	
	
	protected void UpdateGUI() {
		ServerData sd = MySharedPreferences.getInstance().getServerData(this);
		mDistanceTV.setText(sd.getDistance());
		mZoozBalTV.setText(sd.getZoozBalance());
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (mProgBar != null)
				mProgBar.setVisibility(View.GONE);
		    if (requestCode == 1) {
		        if(resultCode == RESULT_OK){
		        	String fromActivity = data.getStringExtra("ACTIVITY");
		        	String s = MySharedPreferences.getInstance().getRecommendUserList(this).toString();
		            Log.e("aaa", s);
		            sendFriendRecommendToServerAsync(s);
		        }
		        if (resultCode == RESULT_CANCELED) {
		            //Write your code if there's no result
		        }
		    }
	}

	
	private void buildChart1(){
		 
	    
		  String[] titles = new String[] { "this week", "previus week"};
		    List<double[]> x = new ArrayList<double[]>();
		    for (int i = 0; i < titles.length; i++) {
		      x.add(new double[] { 1, 2, 3, 4, 5, 6, 7});
		    }
		    List<double[]> values = new ArrayList<double[]>();
		    values.add(new double[] { 50, 50, 52, 55, 56, 58, 62});
		    values.add(new double[] { 36, 37, 38, 40, 46, 48, 48 });
		    int[] colors = new int[] { Color.BLUE, Color.RED };
		    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND};
		    XYMultipleSeriesRenderer renderer = ChartUtil.buildRenderer(colors, styles);
		    int length = renderer.getSeriesRendererCount();
		    for (int i = 0; i < length; i++) {
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setDisplayChartValues(true);
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setDisplayChartValuesDistance(15);
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setLineWidth(3);
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setChartValuesTextSize(20);
		    }
		    ChartUtil.setChartSettings(renderer, "Total of 54,000 km mined", "Time", "Distance", 0.5, 7.5, 20, 90,
		        Color.LTGRAY, Color.BLUE, Color.LTGRAY);
		    //renderer.setXLabels(12);	    
		    renderer.setXLabels(0);
		    renderer.addXTextLabel(1, "Sun");
		    renderer.addXTextLabel(2, "Mon");
		    renderer.addXTextLabel(3, "Tue");
		    renderer.addXTextLabel(4, "Wed");
		    renderer.addXTextLabel(5, "Thu");
		    renderer.addXTextLabel(6, "Fri");
		    renderer.addXTextLabel(7, "Sat");
		    
		    
		    
		    renderer.setYLabels(10);
		    renderer.setShowGrid(true);
		    renderer.setXLabelsAlign(Align.RIGHT);
		    renderer.setYLabelsAlign(Align.RIGHT);
		    renderer.setZoomButtonsVisible(true);
		   
		    
		    //renderer.setLegendHeight(100);

		    //renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		    //renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

		    XYMultipleSeriesDataset dataset = ChartUtil.buildDataset(titles, x, values);
		    XYSeries series = dataset.getSeriesAt(0);
		    //series.addAnnotation("Vacation", 6, 30);
		    mChartView1 = ChartFactory.getLineChartView(this, dataset, renderer);    
		
		    
		    
		    
		    //mChartView =  ChartFactory.getRangeBarChartView(this, buildBarDataset(titles, values), renderer, Type.DEFAULT);
		    

		mLayoutChart1.addView(mChartView1, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	

	}
	
	private void buildChart2(){
		 
	    
		  String[] titles = new String[] { "this week", "previus week"};
		    List<double[]> x = new ArrayList<double[]>();
		    for (int i = 0; i < titles.length; i++) {
		      x.add(new double[] { 1, 2, 3, 4, 5, 6, 7});
		    }
		    List<double[]> values = new ArrayList<double[]>();
		    values.add(new double[] { 50, 50, 52, 55, 56, 58, 62});
		    values.add(new double[] { 36, 37, 38, 40, 46, 48, 48 });
		    int[] colors = new int[] { Color.BLUE, Color.RED };
		    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND};
		    XYMultipleSeriesRenderer renderer = ChartUtil.buildRenderer(colors, styles);
		    int length = renderer.getSeriesRendererCount();
		    for (int i = 0; i < length; i++) {
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setDisplayChartValues(true);
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setDisplayChartValuesDistance(15);
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setLineWidth(3);
		      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setChartValuesTextSize(20);
		    }
		    ChartUtil.setChartSettings(renderer, "24,000 miners paving the web", "Time", "Miners", 0.5, 7.5, 20, 90,
		        Color.LTGRAY, Color.BLUE, Color.LTGRAY);
		    //renderer.setXLabels(12);	    
		    renderer.setXLabels(0);
		    renderer.addXTextLabel(1, "Sun");
		    renderer.addXTextLabel(2, "Mon");
		    renderer.addXTextLabel(3, "Tue");
		    renderer.addXTextLabel(4, "Wed");
		    renderer.addXTextLabel(5, "Thu");
		    renderer.addXTextLabel(6, "Fri");
		    renderer.addXTextLabel(7, "Sat");
		    
		    
		    
		    renderer.setYLabels(10);
		    renderer.setShowGrid(true);
		    renderer.setXLabelsAlign(Align.RIGHT);
		    renderer.setYLabelsAlign(Align.RIGHT);
		    renderer.setZoomButtonsVisible(true);
		   
		    
		    //renderer.setLegendHeight(100);

		    //renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		    //renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

		    XYMultipleSeriesDataset dataset = ChartUtil.buildDataset(titles, x, values);
		    XYSeries series = dataset.getSeriesAt(0);
		    //series.addAnnotation("Vacation", 6, 30);
		    mChartView2 = ChartFactory.getLineChartView(this, dataset, renderer);    
		
		    
		    
		    
		    //mChartView =  ChartFactory.getRangeBarChartView(this, buildBarDataset(titles, values), renderer, Type.DEFAULT);
		    

		mLayoutChart2.addView(mChartView2, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	

	}
	
	
	private void sendFriendRecommendToServerAsync(String data){
		FriendRecommendToServer friendRecommendToServer = new FriendRecommendToServer();
		friendRecommendToServer.execute(data);
	}
	
	private class FriendRecommendToServer extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(MainActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				JSONArray dataList = msp.getRecommendUserList(MainActivity.this);
				
				bServerCom.setFriendRecommend(msp.getUserId(MainActivity.this), msp.getUserSecret(MainActivity.this), dataList.toString());
				jsonReturnObj = bServerCom.getReturnObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        	
        	String serverMessage = "";
	
			try {
				if (jsonReturnObj == null)
					serverMessage = "ConnectionError";
				else {
					serverMessage = jsonReturnObj.getString("message");
					if (serverMessage.equals("success")){
						
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				serverMessage = "GeneralError";
			}
			
			
			return serverMessage;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result.equals("success")){
				Toast.makeText(MainActivity.this, "Friend List Sent", Toast.LENGTH_LONG).show();
				startActivity(new Intent(MainActivity.this, CongratulationsGetFriendsActivity.class));
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}
	
}
