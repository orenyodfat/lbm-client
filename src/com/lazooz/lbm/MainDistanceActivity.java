package com.lazooz.lbm;

import java.util.ArrayList;
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

import com.lazooz.lbm.businessClasses.ServerData;
import com.lazooz.lbm.businessClasses.StatsData;
import com.lazooz.lbm.businessClasses.StatsDataList;
import com.lazooz.lbm.businessClasses.StatsDataMinersDistDay;
import com.lazooz.lbm.businessClasses.StatsDataMinersDistDayList;
import com.lazooz.lbm.businessClasses.StatsDataMinersDistMonth;
import com.lazooz.lbm.businessClasses.StatsDataMinersDistMonthList;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.ChartUtil;
import com.lazooz.lbm.utils.Utils;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;

public class MainDistanceActivity extends ActionBarActivity {

	private LinearLayout mLayoutChart1;
	private LinearLayout mLayoutChart2;
	private LinearLayout mLayoutChart3;

	private GraphicalView mChartView1;
	private GraphicalView mChartView2;
	private GraphicalView mChartView3;
	
	private TextView mDistanceTV;
	public StatsDataMinersDistDayList mStatsDataWeekList;
	public StatsDataMinersDistDayList mStatsDataMonthList;
	public StatsDataMinersDistMonthList mStatsDataYearList;
	private ProgressBar mProgBar;
	public String mStatsDataWeekTotal;
	public String mStatsDataMonthTotal;
	public String mStatsDataYearTotal;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		setContentView(R.layout.activity_main_distance);

		mProgBar = (ProgressBar)findViewById(R.id.progbar);
		mProgBar.setVisibility(View.INVISIBLE);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Utils.setTitleColor(this, getResources().getColor(R.color.white));
		
		mLayoutChart1 = (LinearLayout)findViewById(R.id.report_chart_1);
		mLayoutChart2 = (LinearLayout)findViewById(R.id.report_chart_2);
		mLayoutChart3 = (LinearLayout)findViewById(R.id.report_chart_3);
		mDistanceTV = (TextView)findViewById(R.id.main_distance_tv);
		
		
		
		MySharedPreferences msp = MySharedPreferences.getInstance();
		ServerData sd = msp.getServerData(this);
		
		float distanceFromServer = sd.getDistanceFloat();
		float distanceLocal = msp.getLocalDistance(this);
		float distanceTotal = distanceFromServer + distanceLocal;
		float distanceKMf = distanceTotal / 1000;
		float distanceMf = distanceTotal % 1000;

		int distanceKMd = (int)distanceKMf;
		int distanceMd = (int)distanceMf;
		
		int localDist = (int)distanceLocal;
		
		//mDistanceTV.setText(String.format("%d", distanceKMd));
		mDistanceTV.setText(String.format("%.1f", distanceKMf));
		//mDistanceTV.setText(String.format("%dkm  %dm", distanceKMd, distanceMd));
		//mDistanceTV.setText(String.format("%dkm  %dm , l=%d", distanceKMd, distanceMd, localDist));
		
		
		
		
		
		//mDistanceTV.setText(sd.getDistance());

		
		
		
		getUserStatDataAsync();
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	/*
	private void buildChart1(){
		 
	    
		  String[] titles = new String[] { "this week"};
		    List<double[]> x = new ArrayList<double[]>();
		    x.add(new double[] { 1, 2, 3, 4, 5, 6, 7});
		    
		    List<double[]> values = new ArrayList<double[]>();
		    values.add(new double[] { 50, 50, 52, 55, 56, 58, 62});
		    int[] colors = new int[] { Color.BLUE};
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
	

	}*/
	
	private void buildChartDist1(){
		 
		String[] chartTitles;
		String mainTitle = "Mined Km this week - " + mStatsDataWeekTotal + "";
		String xTitle, yTitle;
 		chartTitles = new String[] { "This Week"};
 		xTitle = "";
 		yTitle = "";
	 		
	    
	    List<double[]> values = new ArrayList<double[]>();


	    values.add(mStatsDataWeekList.getDataDoubleArray());
	    
	    int[] colors = new int[] { Color.WHITE };
	    PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND};
	    XYMultipleSeriesRenderer renderer = ChartUtil.buildRenderer(colors, styles);
	  
	    

	    ChartUtil.setChartSettings(renderer, mainTitle, xTitle, yTitle, 0.5,
	    		mStatsDataWeekList.getList().size() + 0.5, 0, mStatsDataWeekList.getMaxVal() + 5, Color.GRAY, Color.WHITE, Color.LTGRAY);

	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
	    renderer.getSeriesRendererAt(0).setDisplayChartValuesDistance(15);
	    renderer.getSeriesRendererAt(0).setChartValuesTextSize(20);
	    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).setFillPoints(true);
	    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).setLineWidth(4);
	    
	    renderer.setXLabels(0);
	    
	    List<double[]> x = new ArrayList<double[]>();
	    x.add(mStatsDataWeekList.getXDoubleArray());
	    
	    int i = 1;
	     
	    for(StatsDataMinersDistDay point : mStatsDataWeekList.getList()){
	    		renderer.addXTextLabel(i++, point.getDayInWeek(this));
	    }
	    
	    renderer.setYLabels(10);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setPanEnabled(true, false);
	     renderer.setZoomEnabled(false);
	    renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(0.5f);
	    
	    XYMultipleSeriesDataset dataset = ChartUtil.buildDataset(chartTitles, x, values);
	    mChartView1 = ChartFactory.getLineChartView(this, dataset, renderer);    
	 
	    mLayoutChart1.removeAllViews();
	    mLayoutChart1.addView(mChartView1, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

}

	private void buildChartDist2(){
		 
		String[] chartTitles;
		String mainTitle = "Mined Km this month - " + mStatsDataMonthTotal + "";
		String xTitle, yTitle;
 		chartTitles = new String[] { "This Month"};
 		xTitle = "";
 		yTitle = "";
	 		
	    
	    List<double[]> values = new ArrayList<double[]>();


	    values.add(mStatsDataMonthList.getDataDoubleArray());
	    
	    int[] colors = new int[] { Color.WHITE };
	    PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND};
	    XYMultipleSeriesRenderer renderer = ChartUtil.buildRenderer(colors, styles);
	  
	    

	    ChartUtil.setChartSettings(renderer, mainTitle, xTitle, yTitle, 0.5,
	    		mStatsDataMonthList.getList().size() + 0.5, 0, mStatsDataMonthList.getMaxVal() + 5, Color.GRAY, Color.WHITE, Color.LTGRAY);

	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
	    renderer.getSeriesRendererAt(0).setDisplayChartValuesDistance(15);
	    renderer.getSeriesRendererAt(0).setChartValuesTextSize(20);
	    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).setFillPoints(true);
	    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).setLineWidth(4);
	    
	    renderer.setXLabels(0);
	    
	    List<double[]> x = new ArrayList<double[]>();
	    x.add(mStatsDataMonthList.getXDoubleArray());
	    
	    int i = 1;
	     
	    for(StatsDataMinersDistDay point : mStatsDataMonthList.getList()){
	    		renderer.addXTextLabel(i++, point.getDayInMonth(this));
	    }
	    
	    renderer.setYLabels(10);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setPanEnabled(true, false);
	     renderer.setZoomEnabled(false);
	    renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(0.5f);
	    
	    XYMultipleSeriesDataset dataset = ChartUtil.buildDataset(chartTitles, x, values);
	    mChartView2 = ChartFactory.getLineChartView(this, dataset, renderer);    
	 
	    mLayoutChart2.removeAllViews();
	    mLayoutChart2.addView(mChartView2, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

	}	

	private void buildChartDist3(){
		 
		String[] chartTitles;
		String mainTitle = "Total of Km mined - " + mStatsDataYearTotal + "";
		String xTitle, yTitle;
 		chartTitles = new String[] { "Total"};
 		xTitle = "";
 		yTitle = "";
	 		
	    
	    List<double[]> values = new ArrayList<double[]>();


	    values.add(mStatsDataYearList.getDataDoubleArray());
	    
	    int[] colors = new int[] { Color.WHITE };
	    PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND};
	    XYMultipleSeriesRenderer renderer = ChartUtil.buildRenderer(colors, styles);
	  
	    

	    ChartUtil.setChartSettings(renderer, mainTitle, xTitle, yTitle, 0.5,
	    		mStatsDataYearList.getList().size() + 0.5, 0, mStatsDataYearList.getMaxVal() + 5, Color.GRAY, Color.WHITE, Color.LTGRAY);

	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
	    renderer.getSeriesRendererAt(0).setDisplayChartValuesDistance(15);
	    renderer.getSeriesRendererAt(0).setChartValuesTextSize(20);
	    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).setFillPoints(true);
	    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).setLineWidth(4);
	    
	    renderer.setXLabels(0);
	    
	    List<double[]> x = new ArrayList<double[]>();
	    x.add(mStatsDataYearList.getXDoubleArray());
	    
	    int i = 1;
	     
	    for(StatsDataMinersDistMonth point : mStatsDataYearList.getList()){
	    		renderer.addXTextLabel(i++, point.getMonth(this));
	    }
	    
	    renderer.setYLabels(10);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setPanEnabled(true, false);
	     renderer.setZoomEnabled(false);
	    renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(0.5f);
	    
	    XYMultipleSeriesDataset dataset = ChartUtil.buildDataset(chartTitles, x, values);
	    mChartView3 = ChartFactory.getLineChartView(this, dataset, renderer);    
	 
	    mLayoutChart3.removeAllViews();
	    mLayoutChart3.addView(mChartView3, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

	}
	
	public void getUserStatDataAsync() {
		GetUserStatData getUserStatData = new GetUserStatData();
		getUserStatData.execute();
		
	}

	private class GetUserStatData extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(MainDistanceActivity.this);
        	
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				bServerCom.getUserStatDataMinersDist(msp.getUserId(MainDistanceActivity.this), msp.getUserSecret(MainDistanceActivity.this));
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

						JSONArray statsArray = jsonReturnObj.getJSONArray("stats_data_week");
						Log.e("TAG", statsArray.toString());
						mStatsDataWeekList = new StatsDataMinersDistDayList(statsArray);

						statsArray = jsonReturnObj.getJSONArray("stats_data_month");
						Log.e("TAG", statsArray.toString());
						mStatsDataMonthList = new StatsDataMinersDistDayList(statsArray);

						statsArray = jsonReturnObj.getJSONArray("stats_data_year");
						Log.e("TAG", statsArray.toString());
						mStatsDataYearList = new StatsDataMinersDistMonthList(statsArray);
						
						mStatsDataWeekTotal = jsonReturnObj.getString("stats_data_week_total");
						mStatsDataMonthTotal = jsonReturnObj.getString("stats_data_month_total");
						mStatsDataYearTotal = jsonReturnObj.getString("stats_data_year_total");
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
				buildChartDist1();
				buildChartDist2();
				buildChartDist3();
				//buildChart1();
			}
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(MainDistanceActivity.this);
			}
			else if (result.equals("ConnectionError")){
				Utils.displayConnectionError(MainDistanceActivity.this, null);
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			mProgBar.setVisibility(View.VISIBLE);
			
		}
	}
	
	
	
}
