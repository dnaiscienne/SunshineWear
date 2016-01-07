package com.example.android.sunshine.app.sync;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by DS on 1/7/2016.
 */
public class SunshineWearUpdater implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = SunshineWearUpdater.class.getSimpleName();
    private static final String MAX_KEY = "high";
    private static final String MIN_KEY = "low";

    private  GoogleApiClient mGoogleApiClient;

    public SunshineWearUpdater(Context context){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    public void updateWatchWeather(double high, double low){
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/forecast");
        DataMap dataMap = putDataMapRequest.getDataMap();
        dataMap.putDouble(MAX_KEY, high);
        dataMap.putDouble(MIN_KEY, low);
        PutDataRequest request = putDataMapRequest.asPutDataRequest();

        Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected: " + bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended: " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
