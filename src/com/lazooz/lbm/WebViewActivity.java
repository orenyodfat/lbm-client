package com.lazooz.lbm;


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
		setContentView(R.layout.activity_webview);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		
		return true;
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
	 
}
