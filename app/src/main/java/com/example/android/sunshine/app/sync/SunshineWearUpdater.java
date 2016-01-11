package com.example.android.sunshine.app.sync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.example.android.sunshine.app.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by DS on 1/7/2016.
 */
public class SunshineWearUpdater implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = SunshineWearUpdater.class.getSimpleName();
    public static final String MAX_KEY = "high";
    public static final String MIN_KEY = "low";
    public static final String ICON_KEY = "icon";
    public static final String FORECAST_PATH = "/forecast";
    private boolean mResolvingError = false;
    private Context mContext;

    private  GoogleApiClient mGoogleApiClient;

    public SunshineWearUpdater(Context context){
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        connectGoogleApiClient();
    }


    public void updateWatchWeather(double high, double low, int weatherId){
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(FORECAST_PATH);
        DataMap dataMap = putDataMapRequest.getDataMap();

        dataMap.putString(MAX_KEY, Utility.formatTemperature(mContext, high));
        dataMap.putString(MIN_KEY, Utility.formatTemperature(mContext, low));
        int artId = Utility.getArtResourceForWeatherCondition(weatherId);
        Asset weatherIcon = createAsset(artId);
        dataMap.putAsset(ICON_KEY, weatherIcon);
        dataMap.putLong("time", System.currentTimeMillis());
        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        request.setUrgent();
        PendingResult pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(final DataApi.DataItemResult result) {
                Log.v(LOG_TAG, "onResult");
                if (result.getStatus().isSuccess()) {
                    Log.d(LOG_TAG, "Data item set: " + result.getDataItem().getUri());
                }
            }
        });

    }

    public void disconnectGoogleApiClient(){
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.v(LOG_TAG, "disconnecting GoogleApiClient");
            mGoogleApiClient.disconnect();
            }
    }
    public void  connectGoogleApiClient(){
        if(!mResolvingError){
            Log.v(LOG_TAG, "connecting GoogleApiClient");
            mGoogleApiClient.connect();
        }
    }

    private Asset createAsset(int artId) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), artId);
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                }
            }
        }
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
        Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult);
    }
}
