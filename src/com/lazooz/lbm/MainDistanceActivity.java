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
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.ChartUtil;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;

public class MainDistanceActivity extends ActionBarActivity {

	private LinearLayout mLayoutChart1;
	private GraphicalView mChartView1;
	private TextView mDistanceTV;
	public StatsDataList mStatsDataList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_distance);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mLayoutChart1 = (LinearLayout)findViewById(R.id.report_chart_1);
		mDistanceTV = (TextView)findViewById(R.id.main_distance_tv);
		
		
		ServerData sd = MySharedPreferences.getInstance().getServerData(this);
		mDistanceTV.setText(sd.getDistance());

		
		
		
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
	
	private void buildChartDist(){
		 
		String[] chartTitles;
		String mainTitle = "Total Distance";
		String xTitle, yTitle;
 		chartTitles = new String[] { "This Year"};
 		xTitle = "Months";
 		yTitle = "Distance";
	 		
		
	    List<double[]> values = new ArrayList<double[]>();


	    values.add(mStatsDataList.getDistDoubleArray());
	    values.add(mStatsDataList.getDistDoubleArray());
	    
	    int[] colors = new int[] { Color.BLUE };
	    XYMultipleSeriesRenderer renderer = ChartUtil.buildBarRenderer(colors);
	    ChartUtil.setChartSettings(renderer, mainTitle, xTitle, yTitle, 0.5,
	    		mStatsDataList.getList().size() + 0.5, 0, mStatsDataList.getMaxValDist() + 5, Color.GRAY, Color.BLUE, Color.LTGRAY);

	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
	    //renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
	    
	    renderer.getSeriesRendererAt(0).setDisplayChartValuesDistance(15);
	    //renderer.getSeriesRendererAt(1).setDisplayChartValuesDistance(15);

	    renderer.getSeriesRendererAt(0).setChartValuesTextSize(20);
	    //renderer.getSeriesRendererAt(1).setChartValuesTextSize(20);

	    
	    renderer.setXLabels(0);
	    
	    int i = 1;
	    for(StatsData point : mStatsDataList.getList()){
	    		renderer.addXTextLabel(i++, point.getTime());
	    }
	    
	   /* renderer.addXTextLabel(1, "Sun");
	    renderer.addXTextLabel(2, "Mon");
	    renderer.addXTextLabel(3, "Tue");
	    renderer.addXTextLabel(4, "Wed");
	    renderer.addXTextLabel(5, "Thu");
	    renderer.addXTextLabel(6, "Fri");
	    renderer.addXTextLabel(7, "Sat");
	    */
	    
	    //renderer.setXLabels(12);
	    renderer.setYLabels(10);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setPanEnabled(true, false);
	    // renderer.setZoomEnabled(false);
	    renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(0.5f);
	    mChartView1 =  ChartFactory.getBarChartView(this, ChartUtil.buildBarDataset(chartTitles, values), renderer,
	        Type.STACKED);

	    mLayoutChart1.removeAllViews();
	    mLayoutChart1.addView(mChartView1, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

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
				
				bServerCom.getUserStatData(msp.getUserId(MainDistanceActivity.this), msp.getUserSecret(MainDistanceActivity.this));
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
							JSONArray statsArray = jsonReturnObj.getJSONArray("stats_data");
							Log.e("TAG", statsArray.toString());
							mStatsDataList = new StatsDataList(statsArray);
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
				buildChartDist();
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}
	
	
	
}
