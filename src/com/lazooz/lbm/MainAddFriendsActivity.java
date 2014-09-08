package com.lazooz.lbm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.BarChart.Type;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		setContentView(R.layout.activity_main_add_friends);
		
		mProgBar = (ProgressBar)findViewById(R.id.progbar);
		mProgBar.setVisibility(View.INVISIBLE);
		
		
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


}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
						
						if (jsonReturnObj.has("stats_data")){
							mTotalMiners = jsonReturnObj.getString("total_miners");
							JSONArray statsArray = jsonReturnObj.getJSONArray("stats_data");
							Log.e("TAG", statsArray.toString());
							mStatsDataMinerList = new StatsDataMinersList(statsArray);
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
			mProgBar.setVisibility(View.INVISIBLE);
			if (result.equals("success")){
				updateGUI();
			}
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(MainAddFriendsActivity.this);
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			mProgBar.setVisibility(View.VISIBLE);
		}
	}

	public void updateGUI() {
		//mAdapter = new FriendsAdapter(this, R.layout.friend_row, mContactFriendsList);//, mContactList);
		//mAddFriendsListView.setAdapter(mAdapter);
		
		buildChartZooz();
		
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
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}
	
	
}
