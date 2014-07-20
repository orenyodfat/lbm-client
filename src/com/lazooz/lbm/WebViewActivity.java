package com.lazooz.lbm;


import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.ZoomDensity;

public class WebViewActivity extends Activity {

	private WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		webview = (WebView)findViewById(R.id.webView1);
		
		webview.getSettings().setJavaScriptEnabled(true);
		
		webview.setWebViewClient(new MyWebViewClient());
		
		webview.setVerticalScrollBarEnabled(false);
		webview.setHorizontalScrollBarEnabled(false);
		
		
		
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.setScrollbarFadingEnabled(false);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); 
		
		
		
		String uri = "http://www.walla.com";
				
		webview.loadUrl(uri);
		
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
	 
	 
}
