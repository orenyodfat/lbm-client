package com.lazooz.lbm;




import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;


import com.lazooz.lbm.businessClasses.DrawerItem;
import com.lazooz.lbm.cfg.StaticParms;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;

import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;

public class MyActionBarActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	//private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	//private String[] mDrawerTitles;
	protected ProgressBar mProgBar;
	private List<DrawerItem> mDrawerItems;
	 
	protected void onCreate(Bundle savedInstanceState, int layoutID) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		

		
		
		
		mDrawerItems = new ArrayList<DrawerItem>();
		mDrawerItems.add(new DrawerItem(getString(R.string.drawer_entry_me),R.drawable.tool_me));
		mDrawerItems.add(new DrawerItem(getString(R.string.drawer_entry_info),R.drawable.tool_info));
		mDrawerItems.add(new DrawerItem(getString(R.string.drawer_entry_share),R.drawable.tool_share));
		mDrawerItems.add(new DrawerItem(getString(R.string.drawer_entry_website),R.drawable.tool_web));
		mDrawerItems.add(new DrawerItem(getString(R.string.drawer_entry_wallet),R.drawable.tool_wallet));
		mDrawerItems.add(new DrawerItem(getString(R.string.drawer_entry_legal),R.drawable.tool_legal));
		mDrawerItems.add(new DrawerItem(getString(R.string.drawer_entry_report_bug),0));
		mDrawerItems.add(new DrawerItem(getString(R.string.drawer_entry_intro),0));

        
        
		setContentView(layoutID);
		
        
		createDrawerStuff();
		
		getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">" + getString(R.string.app_name) + "</font>"));		
		
	}

	private void createDrawerStuff(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (mDrawerLayout == null)
			return;
		
		//mTitle = mDrawerTitle = getTitle();
        //mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        
        mProgBar = (ProgressBar)findViewById(R.id.progbar);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
		//mDrawerList.setAdapter(new DrawerArrayAdapter(this, R.layout.drawer_list_item, mDrawerTitles));
		mDrawerList.setAdapter(new IconicAdapter(mDrawerItems));
		 
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  // host Activity 
                mDrawerLayout,         // DrawerLayout object 
                R.drawable.ic_navigation_drawer,  // nav drawer image to replace 'Up' caret 
                R.string.drawer_open1,  // "open drawer" description for accessibility 
                R.string.drawer_close1  // "close drawer" description for accessibility 
                ) {
            public void onDrawerClosed(View view) {
            	//getSupportActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	//getSupportActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
		
        
        
        
	}



    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	if (view.isEnabled())
        		selectItem(position);
        }
    }
    
    private void selectItem(int position) {

    	DrawerItem di = (DrawerItem)mDrawerList.getItemAtPosition(position);
    	
    	if (di.getText().equals(getString(R.string.drawer_entry_info))){
    		Intent intent = new Intent(this, InfoActivity.class);
    		startActivity(intent);
    	}
    	else if (di.getText().equals(getString(R.string.drawer_entry_me))){
    		Intent intent = new Intent(this, MeActivity.class);
    		startActivity(intent);
    	}
    	else if (di.getText().equals(getString(R.string.drawer_entry_legal))){
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra("URL", "http://lazooz.org/legal-and-privacy/");
			startActivity(intent);
    	}
    	else if (di.getText().equals(getString(R.string.drawer_entry_share))){
    		Intent sharingIntent = new Intent(); 
    		sharingIntent.setAction(Intent.ACTION_SEND);
    		sharingIntent.setType("text/plain");
	        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "La'Zooz");
	        
	        sharingIntent.putExtra(Intent.EXTRA_TEXT, "join La'Zooz google community to download the App \nhttps://plus.google.com/u/0/communities/116028422996838948960");
	        //sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\nCheck out this cool app La'Zooz.\nDownload from here:\nhttps://play.google.com/apps/testing/com.lazooz.lbm/");
    		startActivity(Intent.createChooser(sharingIntent, "Share via"));
    		
    		}
    	else if (di.getText().equals(getString(R.string.drawer_entry_wallet))){
    		Intent intent = new Intent(this, MainZoozActivity.class);
    		startActivity(intent);
    	}
    	else if (di.getText().equals(getString(R.string.drawer_entry_website))){
    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://lazooz.org"));
    		startActivity(browserIntent);
    	}
    	else if (di.getText().equals(getString(R.string.drawer_entry_intro))){
    		Intent intent = new Intent(this, IntroActivity.class);
    		intent.putExtra("FROM_MENU_MODE", true);
    		startActivity(intent);
    	}
    	else if (di.getText().equals(getString(R.string.drawer_entry_report_bug))){
			String userId = MySharedPreferences.getInstance().getUserId(this);
			String url = StaticParms.BASE_SERVER_URL + "client_report_issue/" + userId;
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra("URL", url);
			startActivity(intent);
    	}

    	
    	
    	/*
    	else if (s.equals(getString(R.string.drawer_entry_getfriends))){
    		mProgBar.setVisibility(View.VISIBLE);
    		final Handler handler = new Handler();
    		handler.postDelayed(new Runnable() {
    		  @Override
    		  public void run() {
   	    		Intent i = new Intent(MyActionBarActivity.this, ContactListActivity.class);
   	    		startActivityForResult(i, 1);
    		  }
    		}, 500);
    		
    		
    		
    		
    	}*/

        //setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
 
	
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null)
        	mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        if (mDrawerToggle != null)
        	mDrawerToggle.onConfigurationChanged(newConfig);
        
    }
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.main, menu);
	        return super.onCreateOptionsMenu(menu);
	        
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.settings, menu);
/*
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.menu_layout);
		Button b1 = (Button)findViewById(R.id.button1);
		b1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 if (mDrawerLayout == null)
					 return;
				 boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
				 if (drawerOpen)
					 mDrawerLayout.closeDrawer(mDrawerList);
				 else
					 mDrawerLayout.openDrawer(mDrawerList);
				
				
			}
		});

		//return true;
	}*/

	
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
		  
		  if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	        }  
/*		  switch (item.getItemId()) {
	        case R.id.menuitem_setting: 
	        	Intent intent2 = new Intent(this, SettingsActivity.class); 
	            startActivityForResult(intent2,REQUEST_SETTING);
	            return (true);
	 
	        }*/
	        return (super.onOptionsItemSelected(item));
	    } 
	
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	        // If the nav drawer is open, hide action items related to the content view
	        return super.onPrepareOptionsMenu(menu);
	    }

	    
	    
	    class IconicAdapter extends ArrayAdapter<DrawerItem> {
	    	private List<DrawerItem> items;
	    	
	    	public IconicAdapter(List<DrawerItem> objects) {
				super(MyActionBarActivity.this, android.R.layout.simple_list_item_activated_1,
						android.R.id.text1, objects);
				items = objects;
				
			}
			
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row=super.getView(position, convertView, parent);
				Drawable img = null;
				TextView tv =(TextView)row.findViewById(android.R.id.text1);
				try{
					int imageRes = items.get(position).getImageResource();
					if (imageRes > 0){
						img = MyActionBarActivity.this.getResources().getDrawable(imageRes);			
						tv.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null );
						tv.setCompoundDrawablePadding(35);						
					}
					tv.setText(items.get(position).getText());
					
				}
				catch(Exception e){}
				
				return(row);
			}
		}	    
	  
	    
}
