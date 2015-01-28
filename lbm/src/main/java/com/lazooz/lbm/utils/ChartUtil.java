package com.lazooz.lbm.utils;

import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

public class ChartUtil {

	public ChartUtil() {
		
	}
	
	
	

	
	  public static void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
		      String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
		      int labelsColor, int axesLabelsColor) {
		    renderer.setChartTitle(title);
		    renderer.setXTitle(xTitle);
		    renderer.setYTitle(yTitle);
		    renderer.setXAxisMin(xMin);
		    renderer.setXAxisMax(xMax);
		    renderer.setYAxisMin(yMin);
		    renderer.setYAxisMax(yMax);
		    renderer.setAxesColor(axesColor);
		    renderer.setLabelsColor(labelsColor);
//		    renderer.setXLabelsColor(axesLabelsColor); 
//		    renderer.setYLabelsColor(0, axesLabelsColor);
		  }
	  
	  
	  
	  public static XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    //renderer.setAxisTitleTextSize(16);
		    //renderer.setChartTitleTextSize(20);
		    //renderer.setLabelsTextSize(15);
		    //renderer.setLegendTextSize(15);
		    
		    renderer.setAxisTitleTextSize(20);
		    renderer.setChartTitleTextSize(25);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(20);
		    
		    renderer.setMargins(new int[] { 50, 60, 60, 30 });
		    renderer.setFitLegend(true);
		    
		    renderer.setBackgroundColor(0xf7f7f7);
		    renderer.setMarginsColor(0xf7f7f7);		    
		    
		    int length = colors.length;
		    for (int i = 0; i < length; i++) {
		      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		      r.setColor(colors[i]);
		      renderer.addSeriesRenderer(r);
		    }
		    return renderer;
		  }
	  public static XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    int length = titles.length;
		    for (int i = 0; i < length; i++) {
		      CategorySeries series = new CategorySeries(titles[i]);
		      double[] v = values.get(i);
		      int seriesLength = v.length;
		      for (int k = 0; k < seriesLength; k++) {
		        series.add(v[k]);
		      }
		      dataset.addSeries(series.toXYSeries());
		    }
		    return dataset;
		  }
	  
	  public static XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    setRenderer(renderer, colors, styles);
		    return renderer;
		  }
	  
	  public static void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
		    renderer.setAxisTitleTextSize(20);
		    renderer.setChartTitleTextSize(25);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(20);
		    renderer.setPointSize(5f);
		    renderer.setMargins(new int[] { 50, 60, 60, 30 });
		    renderer.setFitLegend(true);
		    renderer.setShowAxes(false);
		    renderer.setExternalZoomEnabled(false);
		    renderer.setBackgroundColor(0xf7f7f7);
		    renderer.setMarginsColor(0xf7f7f7);
		    
		    
		    int length = colors.length;
		    for (int i = 0; i < length; i++) {
		      XYSeriesRenderer r = new XYSeriesRenderer();
		      r.setColor(colors[i]);
		      r.setPointStyle(styles[i]);
		      renderer.addSeriesRenderer(r);
		    }
		  }
	  
	  public static XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
		      List<double[]> yValues) {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    addXYSeries(dataset, titles, xValues, yValues, 0);
		    return dataset;
		  }

	  public static void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
		      List<double[]> yValues, int scale) {
		    int length = titles.length;
		    for (int i = 0; i < length; i++) {
		      XYSeries series = new XYSeries(titles[i], scale);
		      double[] xV = xValues.get(i);
		      double[] yV = yValues.get(i);
		      int seriesLength = xV.length;
		      for (int k = 0; k < seriesLength; k++) {
		        series.add(xV[k], yV[k]);
		      }
		      dataset.addSeries(series);
		    }
		  }
		  

}
