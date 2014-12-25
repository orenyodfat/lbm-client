package com.lazooz.lbm.businessClasses;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationClient;
import com.lazooz.lbm.businessClasses.TelephonyDataTracker.OnTelephonyDataListener;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class ActivityRecognitionService extends IntentService implements ConnectionCallbacks, OnConnectionFailedListener {

private String TAG = "o3nWatcherLog";

private Context context;

private static int activityEvaluation = 0;

//TODO MAKE THESE PREFERENCES
private static final int MIN_RECORD_DISTANCE = 750;
private static final int MIN_RECORD_INTERVAL = 10 * 1000 * 60;
private static final int MIN_POST_INTERVAL = 2 * 1000 * 60;
//END MAKE THESE PREFERENCES

private static int previousActivityCode = DetectedActivity.UNKNOWN;
private int activityCode = -1000;
private int activityConfidence = -1000;

public ActivityRecognitionService() {
    super("My Activity Recognition Service");
}




@Override
protected void onHandleIntent(Intent intent) {
    if(ActivityRecognitionResult.hasResult(intent)){
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Log.i(TAG, getType(result.getMostProbableActivity().getType()) +"t" + result.getMostProbableActivity().getConfidence());

        this.context = getApplicationContext();
        Log.d("o3nWatcherLog", "ActivityRecognitionService onHandleIntent called...");

        activityConfidence = result.getMostProbableActivity().getConfidence();
        activityCode = result.getMostProbableActivity().getType();

        Log.d("o3nWatcherLog", " ACTIVITY CODE : " + activityCode + " ACTIVITY CONFIDENCE : " + activityConfidence);

        // Evaluate the avtivity recognition result
        onActivityRecognitionChanged(activityCode);
        //evaluateActivityResult();

//        // Get current location
//        // check Google Play service APK is available and up to date.
//        final int googlePlayServiceAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
//        if (googlePlayServiceAvailable != ConnectionResult.SUCCESS) {
//            Log.d("o3nWatcherLog", "Google Play service is not available (status=" + result + ")");
//        }
//        else {
//            locationClient = new LocationClient(context, this, this);
//            locationClient.connect();
//        }
    }
}

    private void onActivityRecognitionChanged(int activityCode2) {
	// TODO Auto-generated method stub
	
}

// This method is only used in a log line to have readable status in logs
private String getType(int type){
    if(type == DetectedActivity.UNKNOWN)
        return "UNKNOWN";
    else if(type == DetectedActivity.IN_VEHICLE)
        return "IN_VEHICLE";
    else if(type == DetectedActivity.ON_BICYCLE)
        return "ON_BICYCLE";
    else if(type == DetectedActivity.ON_FOOT)
        return "ON_FOOT";
    else if(type == DetectedActivity.STILL)
        return "STILL";
    else if(type == DetectedActivity.TILTING)
        return "TILTING";
    else
        return "";
}


private void evaluateActivityResult() {
    // (Based on previousActivityCode and current activityCode 
            // assign a value to activityEvaluation)
    // compare activityCode to previousActivityCode
   // activityEvaluation = ...;

    previousActivityCode = activityCode;
}

private void actOnEvaluation(Location loc) {

    // Based on activityEvaluation decide to post or not
/*
    if ( activityEvaluation ....) 
        prepareTheLocationJsonAndRecord(loc);
        */
}

private void prepareTheLocationJsonAndRecord(Location loc) {
    // Record the location
}

@Override
public void onConnectionFailed(ConnectionResult arg0) {
    //Toast.makeText(context, "Google location services connection failed", Toast.LENGTH_LONG).show();
    Log.d("o3nWatcherLog","Google location services connection failed");
}

@Override
public void onDisconnected() {
    //Toast.makeText(context, "Google location services disconnected", Toast.LENGTH_LONG).show();
    Log.d("o3nWatcherLog", "Google location services disconnected");
}

@Override
public void onConnected(Bundle arg0) {
    
}

}