package com.lazooz.lbm;




import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.content.Intent;
import android.content.res.Configuration;

public class MyActionBarActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	//private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerTitles;

	 
	protected void onCreate(Bundle savedInstanceState, int layoutID) {
		super.onCreate(savedInstanceState);
		
		//Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		
		
        
        
		setContentView(layoutID);
		
        
		createDrawerStuff();
		
		
	}

	private void createDrawerStuff(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (mDrawerLayout == null)
			return;
		
		//mTitle = mDrawerTitle = getTitle();
        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
		mDrawerList.setAdapter(new DrawerArrayAdapter(this, R.layout.drawer_list_item, mDrawerTitles));
		 
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  // host Activity 
                mDrawerLayout,         // DrawerLayout object 
                R.drawable.ic_drawer,  // nav drawer image to replace 'Up' caret 
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

    	String s = (String)mDrawerList.getItemAtPosition(position);
    	if (s.equals(getString(R.string.drawer_entry_info))){
    	}
    	else if (s.equals(getString(R.string.drawer_entry_getfriends))){
    		Intent i = new Intent(this, ContactListActivity.class);
    		startActivityForResult(i, 1);
    	}

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
		});*/

		//return true;
	}

	
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

	    
	    
	    
	  
	    
}
