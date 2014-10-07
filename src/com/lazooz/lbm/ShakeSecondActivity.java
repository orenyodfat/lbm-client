package com.lazooz.lbm;



import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterInfoWindowClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.lazooz.lbm.businessClasses.WifiData;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

public class ShakeSecondActivity extends ActionBarActivity {

	private GoogleMap map;
	private TextView mMainTextTV;
	private GPSTracker mGPSTracker;
	private Marker mLastMarker;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	private AccelerometerTracker mAccelerometerTracker;
	private ClusterManager<POSClusterItem> mClusterManager;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		setContentView(R.layout.activity_shake_second);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_shake);
		map = mapFragment.getMap();
		
		map.setMyLocationEnabled(true);
		
		
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        setMapLocation(location);
		
		
		initShakeDetector();
		
		mClusterManager = new ClusterManager<POSClusterItem>(this, map);
		mClusterManager.setRenderer(new MyClusterRenderer(this, map, mClusterManager));
		
		
				
		
		map.setOnCameraChangeListener(mClusterManager);
		map.setOnMarkerClickListener(mClusterManager);

		
		getUsersLocationNearMeAsync();
		
				
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		initShakeDetector();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mAccelerometerTracker != null){
			mAccelerometerTracker.release();
			mAccelerometerTracker.setListener(null);
			mAccelerometerTracker = null;
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
	
	private void initShakeDetector() {
		  // ShakeDetector initialization
		
		/*
      mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      mShakeDetector = new ShakeDetector();
      mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

          @Override
          public void onShake(int count) {
              
              
              	Utils.beep();
          }
      });
	*/
      
		mAccelerometerTracker = new AccelerometerTracker(this);
		mAccelerometerTracker.setListener(new AccelerometerTracker.AccelerometerListener() {
			
			@Override
			public void onShake(float force) {
				if (mAccelerometerTracker != null){
					Utils.playSound1(ShakeSecondActivity.this, R.raw.shake);
					//playSound();
				}
			}
			
			@Override
			public void onAccelerationChanged(float x, float y, float z) {
				// TODO Auto-generated method stub
				
			}
		});

      
      
	}
	
	private MediaPlayer mpTada = null;
	public JSONArray mLocationArray;
	
	private void playSound(){
		if (mpTada == null){
			 mpTada = MediaPlayer.create(this, R.raw.shake);
			 mpTada.setVolume(1.0f, 1.0f);
			 mpTada.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mpTada.release();
						mpTada = null;
					}
				});
			 mpTada.start();
		}
		 
	}
	
	private void setMapLocation(Location location){
		if (location != null){
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			
			float currentZoom = map.getCameraPosition().zoom;

		    if (currentZoom < 10)
		    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 10));				
		    map.getUiSettings().setZoomControlsEnabled(true);
            
		}
		
	}
	
	private void setMapLocation2(Location location){
		if (location != null){
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
	    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 11));				
            
		}
		
	}
	
	private void addMarkerToMap(double latitude, double longitude){
		try {
			map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addMarkerToMap(LatLng point){
		map.addMarker(new MarkerOptions().position(point));
	}

	
	private void getUsersLocationNearMeAsync(){
		GetUsersLocationNearMe getUsersLocationNearMe = new GetUsersLocationNearMe();
		getUsersLocationNearMe.execute();
	}
	
	
	private class GetUsersLocationNearMe extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(ShakeSecondActivity.this);
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				
				bServerCom.getUsersLocationNearMe(msp.getUserId(ShakeSecondActivity.this), msp.getUserSecret(ShakeSecondActivity.this));
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
						mLocationArray = jsonReturnObj.getJSONArray("obj_list");
						
						
						
						Log.e("dddd", mLocationArray.toString());
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
				for (int i=0; i<mLocationArray.length(); i++){
					JSONObject jo;
					try {
						jo = (JSONObject)mLocationArray.get(i);
						double lat = jo.getDouble("la");
						double lon = jo.getDouble("lo");
						boolean isMe = Utils.yesNoToBoolean(jo.getString("is_me"));
						POSClusterItem pci = new POSClusterItem(lat, lon, isMe);
						mClusterManager.addItem(pci);
						//addMarkerToMap(lat, lon);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				setMapLocation2(map.getMyLocation());
				

			}
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(ShakeSecondActivity.this);
			}
			else if (result.equals("ConnectionError")){
				Utils.displayConnectionError(ShakeSecondActivity.this, null);
			}
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}
	
	
	
	public class POSClusterItem implements ClusterItem {
	    private final LatLng mPosition;
	    private final boolean mIsMe;
	    public POSClusterItem(double lat, double lng, boolean isMe) {
	        mPosition = new LatLng(lat, lng);
	        mIsMe = isMe;
	    }

	    
	    
	    @Override
	    public LatLng getPosition() {
	        return mPosition;
	    }
	    
	    public boolean isMe(){
	    	return mIsMe;
	    }
	}
	
	
	class MyClusterRenderer extends DefaultClusterRenderer<POSClusterItem> {

	    public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<POSClusterItem> clusterManager) {
	        super(context, map, clusterManager);
	    }

	    @Override
	    protected void onBeforeClusterItemRendered(POSClusterItem item, MarkerOptions markerOptions) {
	        super.onBeforeClusterItemRendered(item, markerOptions);

	        //markerOptions.title("");
	        if (item.isMe()){
	        	markerOptions.title("Me :-)");
	        	markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
	        }
	    }

	    @Override
	    protected void onClusterItemRendered(POSClusterItem clusterItem, Marker marker) {
	        super.onClusterItemRendered(clusterItem, marker);

	        //here you have access to the marker itself
	    }
	}
	
	
	
	
	
	
	/*
	 * googlemap.addMarker(new MarkerOptions()
    .position(new LatLng( 65.07213,-2.109375))
    .title("This is my title")
    .snippet("and snippet")
    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    
    
	 * */
	
	
	
}
