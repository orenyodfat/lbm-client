package com.lazooz.lbm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lazooz.lbm.businessClasses.Contact;
import com.lazooz.lbm.businessClasses.ContactFriendList;
import com.lazooz.lbm.businessClasses.StatsData;
import com.lazooz.lbm.businessClasses.StatsDataList;
import com.lazooz.lbm.businessClasses.StatsDataMiners;
import com.lazooz.lbm.businessClasses.StatsDataMinersDistDayList;
import com.lazooz.lbm.businessClasses.StatsDataMinersList;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.ChartUtil;
import com.lazooz.lbm.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;

public class MainAddFriendsActivity extends ActionBarActivity {


	private LinearLayout mLayoutChart2;
	private GraphicalView mChartView2;
	private Button mAddFriendsBtn;
	//private ListView mAddFriendsListView;
	public ContactFriendList mContactFriendsList;
	//private HashMap<String, Contact> mContactList;
	public StatsDataMinersList mStatsDataMinerList;
	//private FriendsAdapter mAdapter;
	private ProgressBar mProgBar;
	private TextView mNumAcceptedTV;
	private TextView mNumPendingTV;
	public String mTotalMiners;
	public String mNumPending;
	public String mNumAccepted;
	private LinkedHashMap<Integer,String> xyValues;
	public String mInitialDate;
	public static final int X_AXIS_MAX = 6; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		setContentView(R.layout.activity_main_add_friends);
		
		mProgBar = (ProgressBar)findViewById(R.id.progbar);
		mProgBar.setVisibility(View.INVISIBLE);
		
		Utils.setTitleColor(this, getResources().getColor(R.color.white));
		
		mLayoutChart2 = (LinearLayout)findViewById(R.id.report_chart_2);
		mAddFriendsBtn = (Button)findViewById(R.id.add_friends_btn);
		mAddFriendsBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainAddFriendsActivity.this, ContactListActivity.class);
   	    		startActivityForResult(i, 1);				
			}
		});
		
		//mAddFriendsListView = (ListView)findViewById(R.id.add_friends_listview);
		
		mNumAcceptedTV = (TextView)findViewById(R.id.num_accepted_tv);
		mNumPendingTV = (TextView)findViewById(R.id.num_pending_tv);
		
		
		getUserContactDataAsync();
			
		//CreateList(null);
		
		
		
		//buildChart2();
		
	}
	

	private void openChart(){
		 
		String[] chartTitles;
 		chartTitles = new String[] { "Total"};
		List<double[]> values = new ArrayList<double[]>();
	    values.add(mStatsDataMinerList.getDataDoubleArray());
	    int totalXlength = 0;
	    
    	
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    
        List<double[]> xAxisValues = new ArrayList<double[]>();
	    xAxisValues.add(mStatsDataMinerList.getXDoubleArray());
	    
	    for (int i = 0; i < chartTitles.length; i++) {
	    	XYSeries series = new XYSeries(chartTitles[i]);
	    	double[] xV = xAxisValues.get(i);
	    	double[] yV = values.get(i);
	    	int seriesLength = xV.length;
	      for (int k = 0; k < seriesLength; k++) {
	    	  System.out.println("LOG X is "+xV[k]+ " y is "+yV[k]);
	    	  series.add(xV[k]-1, yV[k]);
	      }
	      dataset.addSeries(series);
	    }
        
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.WHITE);
        incomeRenderer.setPointStyle(PointStyle.CIRCLE);
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);
        incomeRenderer.setDisplayChartValuesDistance(15);
        incomeRenderer.setChartValuesTextSize(20);
        incomeRenderer.setLineWidth(4);
        incomeRenderer.setDisplayBoundingPoints(false); // for hiding the series when we scroll
    	
        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setChartTitle("Miners Paveing the Web");
        multiRenderer.setXTitle("Days");
        multiRenderer.setYTitle("Miners");
        multiRenderer.setZoomButtonsVisible(true);
        multiRenderer.setBackgroundColor(0xf7f7f7);
        multiRenderer.setMarginsColor(0xf7f7f7);
        multiRenderer.setMargins(new int[] { 50, 60, 60, 30 });
        multiRenderer.setAxisTitleTextSize(20);
        multiRenderer.setChartTitleTextSize(25);
        multiRenderer.setLabelsTextSize(20);
        
        multiRenderer.setXLabelsAlign(Align.CENTER);
        multiRenderer.setYLabelsAlign(Align.RIGHT);
        multiRenderer.setPanEnabled(true, false); // scroll only x axis so true
        multiRenderer.setZoomEnabled(false,false);
        multiRenderer.setPointSize(8);  // increase the width of point size
        multiRenderer.setXLabelsPadding(10);
        
        
        xyValues = new LinkedHashMap<Integer,String>();
        
        for (int i = 0; i < chartTitles.length; i++) {
	    	double[] xV = xAxisValues.get(i);
	    	totalXlength =  xV.length;
	    	System.out.println("LOG len is "+totalXlength);
	    	for(int j=0;j<totalXlength;j++){
	    		multiRenderer.addXTextLabel(j+1, Utils.addDays(xV[j], mInitialDate));  
	    		xyValues.put(j+1, Utils.addDays(xV[j], mInitialDate));
	    	}    	
        }
        
    	multiRenderer.setXLabels(0);
	    multiRenderer.setShowAxes(false);
	    multiRenderer.setXAxisMin(0);
	    multiRenderer.setXAxisMax(X_AXIS_MAX);
	    if(totalXlength < X_AXIS_MAX){
	    	multiRenderer.setXAxisMax(totalXlength);
	    }
    	multiRenderer.setPanEnabled(true);
    	multiRenderer.setPanLimits(new double [] {0,totalXlength+1,0,0});
    	
    	multiRenderer.setYAxisMin(Utils.getMinValueFromList(values));
    	multiRenderer.setYAxisMax(Utils.getMaxValueFromList(values));
	    multiRenderer.setAxesColor(Color.GRAY);
	    multiRenderer.setLabelsColor(Color.WHITE);
        multiRenderer.addSeriesRenderer(incomeRenderer);
 
        // Creating a Time Chart
        mChartView2 = (GraphicalView) ChartFactory.getTimeChartView(getBaseContext(), dataset, multiRenderer,"dd-MMM-yyyy");
 
        multiRenderer.setClickEnabled(true);
        multiRenderer.setSelectableBuffer(10);
 
        // Setting a click event listener for the graph
        mChartView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	
                SeriesSelection seriesSelection = mChartView2.getCurrentSeriesAndPoint();
                double[] xy = mChartView2.toRealPoint(0);

                if (seriesSelection != null) {

                    //                  debug
                    Log.d("Punto", seriesSelection.getXValue() + ", " + seriesSelection.getValue());
                    //                  debug
                    Log.d("Chart point", "Chart element in series index " + seriesSelection.getSeriesIndex() + " data point index "
                            + seriesSelection.getPointIndex() + " was clicked" + " closest point value X=" + seriesSelection.getXValue()
                            + ", Y=" + seriesSelection.getValue() + " clicked point value X=" + (float) xy[0] + ", Y=" + (float) xy[1]);
                    
                    Toast.makeText(getBaseContext(),"" +xyValues.get((int)seriesSelection.getXValue()) + " , " + seriesSelection.getValue() +" miners" ,Toast.LENGTH_SHORT).show();               }
            }
        });
 
            // Adding the Line Chart to the LinearLayout
        mLayoutChart2.addView(mChartView2);
    }	


	
/*
	private void buildChartZooz(){
		 
		String[] chartTitles;
		String mainTitle = "Miners Paveing the Web - " + mTotalMiners + "";
		String xTitle, yTitle;
 		chartTitles = new String[] { "This Year"};
 		xTitle = "";
 		yTitle = "";
	 		
		
	    List<double[]> values = new ArrayList<double[]>();


	    values.add(mStatsDataMinerList.getDataDoubleArray());
	   
	    
	    int[] colors = new int[] { Color.WHITE };
	    PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND};
	    XYMultipleSeriesRenderer renderer = ChartUtil.buildRenderer(colors, styles);

	    
	    
	    
	    ChartUtil.setChartSettings(renderer, mainTitle, xTitle, yTitle, 0.5,
	    		mStatsDataMinerList.getList().size() + 0.5, 0, mStatsDataMinerList.getMaxVal() + 5, 
	    		this.getResources().getColor(R.color.graph_labels), Color.WHITE, this.getResources().getColor(R.color.graph_labels));
	    
	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
	    renderer.getSeriesRendererAt(0).setDisplayChartValuesDistance(15);
	    renderer.getSeriesRendererAt(0).setChartValuesTextSize(20);
	    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).setFillPoints(true);
	    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).setLineWidth(4);
	    
	    
	    
	    renderer.setXLabels(0);

	    
	    

	    
	    List<double[]> x = new ArrayList<double[]>();
	    x.add(mStatsDataMinerList.getXDoubleArray());
	    
	    
	    
	    int i = 1;
	     
	    for(StatsDataMiners point : mStatsDataMinerList.getList()){
	    		renderer.addXTextLabel(i++, point.getMonth(this));
	    }
	    
	    
	    
	    renderer.setYLabels(10);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setPanEnabled(true, false);
	    
	    renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(0.5f);
	    
	    XYMultipleSeriesDataset dataset = ChartUtil.buildDataset(chartTitles, x, values);
	    mChartView2 = ChartFactory.getLineChartView(this, dataset, renderer);    

	    
	    mLayoutChart2.removeAllViews();
	    mLayoutChart2.addView(mChartView2, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));


}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void getUserContactDataAsync(){
		GetUserContactData getUserContactData = new GetUserContactData();
		getUserContactData.execute();
	}
	
	private class GetUserContactData extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(MainAddFriendsActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				bServerCom.getUserContactData(msp.getUserId(MainAddFriendsActivity.this), msp.getUserSecret(MainAddFriendsActivity.this));
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
						mNumPending = jsonReturnObj.getString("num_pending");
						mNumAccepted = jsonReturnObj.getString("num_accepted");
						
						if (jsonReturnObj.has("contacts")){
							JSONArray contactsArray = jsonReturnObj.getJSONArray("contacts");
							Log.e("TAG", contactsArray.toString());
							mContactFriendsList = new ContactFriendList(contactsArray);
						}
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
				mNumAcceptedTV.setText(mNumAccepted);
				mNumPendingTV.setText(mNumPending);
				getUserStatDataAsync();
			}
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(MainAddFriendsActivity.this);
			}
			else if (result.equals("ConnectionError")){
				try {
					Utils.displayConnectionError(MainAddFriendsActivity.this, null);
				} catch (Exception e) {}
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			mProgBar.setVisibility(View.VISIBLE);
		}
	}

	public void getUserStatDataAsync() {
		GetUserStatData getUserStatData = new GetUserStatData();
		getUserStatData.execute();
		
	}

	private class GetUserStatData extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(MainAddFriendsActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				bServerCom.getUserStatDataMiners(msp.getUserId(MainAddFriendsActivity.this), msp.getUserSecret(MainAddFriendsActivity.this));
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
						
						//jsonReturnObj = readJasonFile();
						JSONArray statsArray = jsonReturnObj.getJSONArray("stats_data");
						Log.e("TAG", statsArray.toString());
						mStatsDataMinerList = new StatsDataMinersList(statsArray);
						
						mInitialDate = jsonReturnObj.getString("initial_date");
						
						
					/*	if (jsonReturnObj.has("stats_data")){
							mTotalMiners = jsonReturnObj.getString("total_miners");
							JSONArray statsArray = jsonReturnObj.getJSONArray("stats_data");
							Log.e("TAG", statsArray.toString());
							mStatsDataMinerList = new StatsDataMinersList(statsArray);
						}*/
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
			mProgBar.setVisibility(View.INVISIBLE);
			if (result.equals("success")){
				updateGUI();
			}
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(MainAddFriendsActivity.this);
			}
			else if (result.equals("ConnectionError")){
				try {
					Utils.displayConnectionError(MainAddFriendsActivity.this, null);
				} catch (Exception e) {}
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			mProgBar.setVisibility(View.VISIBLE);
		}
	}

	private JSONObject readJasonFile(){
        InputStream is;
        String s = "";
		try {
			is = getAssets().open("server_data.json");
	        int size = is.available();
	        byte[] buffer = new byte[size];
	        is.read(buffer);
	        is.close();
	        s = new String(buffer, "UTF-8");
	        return new JSONObject(s);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
			
	}
	
	public void updateGUI() {
		//mAdapter = new FriendsAdapter(this, R.layout.friend_row, mContactFriendsList);//, mContactList);
		//mAddFriendsListView.setAdapter(mAdapter);
		openChart();
		//buildChartZooz();
		
	}

	
	private void CreateList(String constraint){
		HashMap<String, Contact> contactList = new HashMap<String, Contact>();
		String currentLocale = Utils.getCurrentLocale(this);
		List<String> contactsWithApp = MySharedPreferences.getInstance().getContactsWithInstalledApp(this);
		Cursor phones;
		
		if (constraint == null){
			phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
		}
		else{
			Uri contentUri =Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(constraint));
			phones = this.getContentResolver().query(contentUri, null, null,null, null);
		}
		
		while (phones.moveToNext()) {

			String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			Contact objContact = new Contact(currentLocale);
			objContact.setPhoneNo(phoneNumber);
			if(objContact.isValidPhoneNum()){
				objContact.setName(name);
				objContact.setHasApp(contactsWithApp.contains(objContact.getPhoneNoInternational()));
				contactList.put(objContact.getPhoneNoInternational(), objContact);
			}

		}
		phones.close();
		//mContactList = contactList;
		MySharedPreferences.getInstance().clearRecommendUsers(this);
		
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (mProgBar != null)
				mProgBar.setVisibility(View.GONE);
		    if (requestCode == 1) {
		        if(resultCode == RESULT_OK){
		        	String fromActivity = data.getStringExtra("ACTIVITY");
		        	String theMessage = data.getStringExtra("MESSAGE");
		        	String s = MySharedPreferences.getInstance().getRecommendUserList(this).toString();
		            Log.e("aaa", s);
		            sendFriendRecommendToServerAsync(theMessage);
		        }
		        if (resultCode == RESULT_CANCELED) {
		            //Write your code if there's no result
		        }
		    }
	}

	
	
	
	
	private void sendFriendRecommendToServerAsync(String theMessage){
		FriendRecommendToServer friendRecommendToServer = new FriendRecommendToServer();
		friendRecommendToServer.execute(theMessage);
	}
	
	private class FriendRecommendToServer extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
			String theMessage = params[0];
			
          	ServerCom bServerCom = new ServerCom(MainAddFriendsActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				JSONArray dataList = msp.getRecommendUserList(MainAddFriendsActivity.this);
				
				bServerCom.setFriendRecommend(msp.getUserId(MainAddFriendsActivity.this), msp.getUserSecret(MainAddFriendsActivity.this), dataList.toString(), theMessage);
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
				Toast.makeText(MainAddFriendsActivity.this, "Recommendation Sent", Toast.LENGTH_LONG).show();
				getUserContactDataAsync();
				//startActivity(new Intent(MainAddFriendsActivity.this, CongratulationsGetFriendsActivity.class));
			}
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(MainAddFriendsActivity.this);
			}
			else if (result.equals("ConnectionError")){
				Utils.displayConnectionError(MainAddFriendsActivity.this, null);
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}
	
	
}
