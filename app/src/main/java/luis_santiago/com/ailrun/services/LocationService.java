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

import static luis_santiago.com.ailrun.Constants.EXTRA_LATITUDE;
import static luis_santiago.com.ailrun.Constants.EXTRA_LONGITUDE;

/**
 * Created by Luis Santiago on 9/12/18.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener , com.google.android.gms.location.LocationListener{

    private String TAG = LocationService.class.getSimpleName();
    private LocationManager mLocationManager = null;
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    public static final String ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }
    }

    private void sendMessageToUI(String lat, String lng) {
        Log.d(TAG, "Sending info...");
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
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
