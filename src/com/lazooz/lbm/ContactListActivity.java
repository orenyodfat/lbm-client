package com.lazooz.lbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.lazooz.lbm.ContanctAdapter.OnCheckedListener;
import com.lazooz.lbm.businessClasses.Contact;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.Utils;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class ContactListActivity extends Activity implements
		OnItemClickListener, OnCheckedListener {

	private ListView listView;
	private List<Contact> mContactList = new ArrayList<Contact>();
	private boolean mIsSearchResultView = false;
	private String mSearchTerm; // Stores the current search query term
    private boolean mSearchQueryChanged;
	private ContanctAdapter mAdapter;
	private List<String> mContactsWithApp;
	private LinearLayout mOperationButtonLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(this);

		CreateList(null);
		sortList();
			
		mAdapter = new ContanctAdapter(this, R.layout.contact_row, mContactList);
		mAdapter.setOnCheckedListener(this);
		
		listView.setAdapter(mAdapter);
		listView.setTextFilterEnabled(true);
		
	    mOperationButtonLayout = (LinearLayout)findViewById(R.id.operation_layout);
	    mOperationButtonLayout.setVisibility(View.INVISIBLE);


	}
	
	
	 private void showOperationButtonLayout(){
			if (mOperationButtonLayout.getVisibility() == View.VISIBLE)
				return;
			TranslateAnimation animate = new TranslateAnimation(0,0,mOperationButtonLayout.getHeight(),0);
			animate.setDuration(500);
			animate.setFillAfter(true);
			animate.setInterpolator(new AccelerateInterpolator(1.0f));
			mOperationButtonLayout.startAnimation(animate);
			mOperationButtonLayout.setVisibility(View.VISIBLE);
		}
		
		
		private void hideOperationButtonLayout(){
			if (mOperationButtonLayout.getVisibility() == View.GONE)
				return;
				TranslateAnimation animate = new TranslateAnimation(0,0,0,mOperationButtonLayout.getHeight());
				animate.setDuration(500);
				animate.setInterpolator(new AccelerateInterpolator(1.0f));
				animate.setFillAfter(true);
				mOperationButtonLayout.startAnimation(animate);
				mOperationButtonLayout.setVisibility(View.GONE);
		}
		
	
	

	private void sortList(){
		if ((mContactList != null) && (mContactList.size()!=0)) {
			Collections.sort(mContactList, new Comparator<Contact>() {
				@Override
				public int compare(Contact lhs, Contact rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});

		} else {
			showToast("No Contact Found!!!");
		}
	}
	
	private void CreateList(String constraint){
		ArrayList<Contact> contactList = new ArrayList<Contact>();
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
				contactList.add(objContact);

			}

		}
		phones.close();
		mContactList = contactList;
		MySharedPreferences.getInstance().clearRecommendUsers(this);
		
		
	}
	
	
	 public void setSearchQuery(String query) {
	        if (TextUtils.isEmpty(query)) {
	            mIsSearchResultView = false;
	        } else {
	            mSearchTerm = query;
	            mIsSearchResultView = true;
	        }
	  }
	 
	 
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_list_menu, menu);
		
		 MenuItem searchItem = menu.findItem(R.id.menu_search);
		 
		// In versions prior to Android 3.0, hides the search item to prevent additional
	        // searches. In Android 3.0 and later, searching is done via a SearchView in the ActionBar.
	        // Since the search doesn't create a new Activity to do the searching, the menu item
	        // doesn't need to be turned off.
	        if (mIsSearchResultView) {
	            searchItem.setVisible(false);
	        }
	        
	        if (Utils.hasHoneycomb()) {

	            // Retrieves the system search manager service
	            final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

	            // Retrieves the SearchView from the search menu item
	            final SearchView searchView = (SearchView) searchItem.getActionView();

	            // Assign searchable info to SearchView
	            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

	            // Set listeners for SearchView
	            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	                @Override
	                public boolean onQueryTextSubmit(String queryText) {
	                    // Nothing needs to happen when the user submits the search string
	                    return true;
	                }

	                @Override
	                public boolean onQueryTextChange(String newText) {
	                    // Called when the action bar search text has changed.  Updates
	                    // the search filter, and restarts the loader to do a new query
	                    // using the new search string.
	                    String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

	                    // Don't do anything if the filter is empty
	                    if (mSearchTerm == null && newFilter == null) {
	                        return true;
	                    }

	                    // Don't do anything if the new filter is the same as the current filter
	                    if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
	                        return true;
	                    }

	                    // Updates current filter to new filter
	                    mSearchTerm = newFilter;

	                    // Restarts the loader. This triggers onCreateLoader(), which builds the
	                    // necessary content Uri from mSearchTerm.
	                    mSearchQueryChanged = true;
	                    //getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, ContactsListFragment.this);
	                    
	                    //CreateList(mSearchTerm);
	                    //sortList();
	                    //mAdapter.notifyDataSetChanged();
	                    
	                    
	                    mAdapter.getFilter().filter(mSearchTerm);	
	                    
	                    return true;
	                }
	            });

	            if (Utils.hasICS()) {
	                // This listener added in ICS
	                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
	                    @Override
	                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
	                        // Nothing to do when the action item is expanded
	                        return true;
	                    }

	                    @Override
	                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
	                        // When the user collapses the SearchView the current search string is
	                        // cleared and the loader restarted.
	                        if (!TextUtils.isEmpty(mSearchTerm)) {
	                            //onSelectionCleared();
	                        }
	                        mSearchTerm = null;
	                        //getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, ContactsListFragment.this);
	                        return true;
	                    }
	                });
	            }

	            if (mSearchTerm != null) {
	                // If search term is already set here then this fragment is
	                // being restored from a saved state and the search menu item
	                // needs to be expanded and populated again.

	                // Stores the search term (as it will be wiped out by
	                // onQueryTextChange() when the menu item is expanded).
	                final String savedSearchTerm = mSearchTerm;

	                // Expands the search menu item
	                if (Utils.hasICS()) {
	                    searchItem.expandActionView();
	                }

	                // Sets the SearchView to the previous search string
	                searchView.setQuery(savedSearchTerm, false);
	            }
	        }
		
		
		
		return true;
	}
	
	
	
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		Contact bean = (Contact) listview.getItemAtPosition(position);
		
		
		CheckBox cb = (CheckBox) v.findViewById(R.id.contact_selected_cb);
		if (cb != null){
			cb.setChecked(!cb.isChecked());
		}
		
		
		
		
	}

	@Override
	public void onChecked(boolean isChecked, Contact contactBean) {
		MySharedPreferences msp = MySharedPreferences.getInstance();
		if (isChecked){
			msp.addRecommendUser(this, contactBean);
			if(msp.areThere3RecommendUser(this)){
				Intent returnIntent = new Intent();
				returnIntent.putExtra("ACTIVITY", "ContactListActivity");
				setResult(RESULT_OK,returnIntent);
				finish();
			}
		}
		else
			MySharedPreferences.getInstance().removeRecommendUser(this, contactBean);
		
	}

	
}
