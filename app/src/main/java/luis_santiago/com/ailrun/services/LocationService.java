package luis_santiago.com.ailrun.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.helpers.ChronometerRunning;

import static luis_santiago.com.ailrun.Constants.EXTRA_LATITUDE;
import static luis_santiago.com.ailrun.Constants.EXTRA_LONGITUDE;
import static luis_santiago.com.ailrun.Constants.EXTRA_MS_LAPSE;


/**
 * Created by Luis Santiago on 9/12/18.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener , com.google.android.gms.location.LocationListener{

    private String TAG = LocationService.class.getSimpleName();

    private GoogleApiClient mLocationClient;

    private LocationRequest mLocationRequest = new LocationRequest();

    public static final String ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast";

    private ChronometerRunning mChronometer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mChronometer = new ChronometerRunning(LocationService.this);
        mChronometer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.LOCATION_FASTEST_INTERVAL);
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();
        return Service.START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG , "LATITUD" + location.getLatitude());
            Log.e(TAG , "Longitud" + location.getLongitude());
            Log.e(TAG , "TIME LAPSE" + mChronometer.getMsElapsed());
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()) , mChronometer.getMsElapsed());
        }
    }

    private void sendMessageToUI(String lat, String lng , int tmsLapse) {
        Log.d(TAG, "Sending info...");
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        intent.putExtra(EXTRA_MS_LAPSE , tmsLapse);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG , "Google client connected");
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient , mLocationRequest , this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG , "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG , "Connection failed");
    }
}