package com.lazooz.lbm;



import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DrawerUtil {

	private String[] mDrawerTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ActionBarActivity mActivity;

	public DrawerUtil(ActionBarActivity aba){
		mActivity = aba;
        mDrawerTitles = aba.getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) aba.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) aba.findViewById(R.id.left_drawer);
        
		mDrawerList.setAdapter(new DrawerArrayAdapter(aba, R.layout.drawer_list_item, mDrawerTitles));
		 
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        aba.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        aba.getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
        		aba,                  // host Activity 
                mDrawerLayout,         // DrawerLayout object 
                R.drawable.settings_menu_icon,  // nav drawer image to replace 'Up' caret 
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
        // update selected item and title, then close the drawer
//        mDrawerList.setItemChecked(position, true);
        mActivity.setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		/*
		mActivity.getMenuInflater().inflate(R.menu.settings, menu);

		mActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mActivity.getSupportActionBar().setCustomView(R.layout.menu_layout);
		Button b1 = (Button)mActivity.findViewById(R.id.button1);
		b1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
				 if (drawerOpen)
					 mDrawerLayout.closeDrawer(mDrawerList);
				 else
					 mDrawerLayout.openDrawer(mDrawerList);

			}
		});*/

		return true;
	}
    
    
    
}
