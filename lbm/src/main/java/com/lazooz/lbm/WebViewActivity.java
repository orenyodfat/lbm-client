package com.lazooz.lbm;


import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.support.v7.app.ActionBarActivity;

public class WebViewActivity extends ActionBarActivity {

	private WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		setContentView(R.layout.activity_webview);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Utils.setTitleColor(this, getResources().getColor(R.color.white));
		
		webview = (WebView)findViewById(R.id.webView1);
		
		webview.getSettings().setJavaScriptEnabled(true);
		
		webview.setWebViewClient(new MyWebViewClient());
		
		webview.setVerticalScrollBarEnabled(false);
		webview.setHorizontalScrollBarEnabled(false);
		
		
		
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.setScrollbarFadingEnabled(false);
		webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); 
		
		String url = getIntent().getStringExtra("URL");
		
		webview.loadUrl(url);
		
	}

	
	@Override
	public void onBackPressed()
	{
	    if(webview.canGoBack())
	    	webview.goBack();
	    else
	        super.onBackPressed();
	}
	
	 private class MyWebViewClient extends WebViewClient {

	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            view.loadUrl(url);
	            return false;
	        }
	    }
	 
}
