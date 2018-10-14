package luis_santiago.com.ailrun.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.SensorsApi;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.POJOS.User;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.Tools;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;
import luis_santiago.com.ailrun.helpers.GlideApp;
import luis_santiago.com.ailrun.interfaces.IUser;
import luis_santiago.com.ailrun.interfaces.OnAcceptListener;
import luis_santiago.com.ailrun.services.LocationService;
import luis_santiago.com.ailrun.tools.HealthCalculations;

import static luis_santiago.com.ailrun.Constants.EXTRA_MS_LAPSE;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CircleImageView circleImageView;
    private Toolbar toolbar;
    private LocationRequest mLocationRequest = new LocationRequest();
    private GoogleApiClient mLocationClient;
    private Button startButton;
    private boolean isServiceStarted = false;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet;
    private Button stop_button;
    private Button pause;
    private LatLng initialLocation;
    private LatLng lastLocation;
    private Polyline mPolyline;
    private TextView time_lapse;
    private Intent serviceIntent;
    private TextView speed;
    private ImageButton location_button;
    private boolean isPause;
    private TextView distanceDifferenceTextView;
    private ArrayList<LatLng> points;
    private User mUser;
    private long msPassed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        setUpDrawer();
        setUpWindow();
        setUpUserImage();
        init();
        setUpButtons();
        setBottomSheet();
        if (checkLocationAvailable()) {
            listenForBroadcast();
            setUpGoogleClient();
        }

        if (checkSensorsBody()) {
            setUpGoogleFit();
        }

        FirebaseHelper.getInstance().getUserInfo(new IUser() {
            @Override
            public void onUserLoaded(User user) {
                mUser = user;
            }
        });


        serviceIntent = new Intent(HomeActivity.this, LocationService.class);
    }

    private void setUpGoogleFit() {

    }

    private boolean checkSensorsBody() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    private void listenForBroadcast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (isServiceStarted) {
                            handleReceiveBroadCast(intent);
                        }
                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (isServiceStarted) {
                            handleReceiveTimeBroadcast(intent);
                        }
                    }
                }, new IntentFilter(LocationService.ACTION_TIME_BROADCAST)
        );
    }

    private void setBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        Log.e(TAG, "Bottom hidden");
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        Log.e(TAG, "Bottom expanded");
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        Log.e(TAG, "Bottom collapsed");
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    }

                    case BottomSheetBehavior.STATE_DRAGGING: {
                        Log.e(TAG, "Bottom sheek dragging");
                        break;
                    }

                    case BottomSheetBehavior.STATE_SETTLING: {
                        Log.e(TAG, "Bottom settling");
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
    }

    private void setUpButtons() {
        startButton.setOnClickListener(this);
        stop_button.setOnClickListener(this);
        pause.setOnClickListener(this);
        location_button.setOnClickListener(this);
    }

    private void handleReceiveTimeBroadcast(Intent intent) {
        Log.e(TAG, intent.getExtras().toString() + "Time to User Interface");
        msPassed = intent.getExtras().getLong(EXTRA_MS_LAPSE) / 1000;
        long minutes = msPassed / 60;
        long seconds = msPassed % 60;
        String template = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        time_lapse.setText(template);
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        points = new ArrayList<>();
        points.clear();
        startButton = findViewById(R.id.start_button);
        time_lapse = findViewById(R.id.time_lapse);
        stop_button = findViewById(R.id.stop_button);
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        location_button = findViewById(R.id.location_button);
        distanceDifferenceTextView = findViewById(R.id.km);
        pause = findViewById(R.id.pause_button);
        speed = findViewById(R.id.speed);
    }

    private void changeStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = HomeActivity.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }


    private void cancelRun() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        startButton.setVisibility(View.VISIBLE);
        isServiceStarted = false;
        changeStatusBarColor(HomeActivity.this.getResources().getColor(R.color.colorPrimaryDark));
        mPolyline.remove();
        mPolyline = null;
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.MAX_ZOOM_MAP));
        stopService(serviceIntent);
        points.clear();
    }

    private void setUpGoogleClient() {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL_LONG);
        mLocationRequest.setFastestInterval(Constants.LOCATION_INTERVAL_LONG);
        int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();
    }


    private void handleReceiveBroadCast(Intent intent) {
        Double latitude = Double.parseDouble(intent.getStringExtra(Constants.EXTRA_LATITUDE));
        Double longitude = Double.parseDouble(intent.getStringExtra(Constants.EXTRA_LONGITUDE));
        Log.e(TAG, "LONG" + longitude);
        Log.e(TAG, "LAT" + latitude);
        lastLocation = new LatLng(latitude, longitude);
        points.add(lastLocation);
        animateToPlace(lastLocation);
        PolylineOptions options = new PolylineOptions().width(5).color(R.color.colorPrimary).geodesic(true);
        final double[] totalOfMasRecovered = {0};
        for (int z = 0; z < points.size(); z++) {
            LatLng point = points.get(z);
            options.add(point);
        }

        Collections.sort(points, new Comparator<LatLng>() {
            @Override
            public int compare(LatLng latLng, LatLng t1) {
                float[] results = new float[1];
                Location.distanceBetween(latLng.latitude, latLng.longitude, t1.latitude, t1.longitude, results);
                totalOfMasRecovered[0] += results[0];
                return 0;
            }
        });

        double finalTotal = totalOfMasRecovered[0];
        mPolyline = mMap.addPolyline(options);
        DecimalFormat df = new DecimalFormat("####0.00");
        Log.e(TAG , "RAW SEG PASSED: " + msPassed +" seg");
        Log.e(TAG, "RAW:" + finalTotal + "  MTS: " + df.format(finalTotal) + " mts KM: " + HealthCalculations.metersToKilometers(Double.parseDouble(df.format(finalTotal))) + "KM Speed: " + String.valueOf(HealthCalculations.velocity(finalTotal , msPassed)));
        speed.setText(df.format(HealthCalculations.velocity(finalTotal , msPassed)) + "mts/seg");

        if (mUser != null){
            double calories = HealthCalculations.calculateEnergyExpenditure(mUser.getHeight() , mUser.getAge() , mUser.getWeight() , mUser.getSexOption() , msPassed , finalTotal);
            Log.e(TAG , "RAW CALORIES BURNED: " + calories+" Kca");
        }
        distanceDifferenceTextView.setText(df.format(finalTotal) + " mts");
    }

    private void animateToPlace(LatLng latLng) {
        if (latLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    private void setUpWindow() {
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setUpUserImage() {
        FirebaseHelper.getInstance().getUserInfo(new IUser() {
            @Override
            public void onUserLoaded(User user) {
                if (user.getHeight() == null) {
                    Intent intent = new Intent(HomeActivity.this, RequestInfoActivity.class);
                    intent.putExtra(Constants.EXTRAS_URL_PROFILE_IMAGE, user.getUrlImage());
                    intent.putExtra(Constants.EXTRAS_PROFILE_NAME, user.getName());
                    intent.putExtra(Constants.EXTRAS_PROFILE_UID, user.getUid());
                    startActivity(intent);
                }

                GlideApp
                        .with(HomeActivity.this)
                        .load(user.getUrlImage())
                        .placeholder(R.drawable.oficial_logo)
                        .into(circleImageView);
            }
        });
    }

    private void setUpDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        circleImageView = findViewById(R.id.circle_image_view);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setTitle("AilRun");
        setSupportActionBar(toolbar);
    }


    private boolean checkLocationAvailable() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.DEFAULT_LOCATION, Constants.MAX_ZOOM_MAP));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("HOME ACTIVITY", "I GOT LOCATION FOR FIRST TIME");
        initialLocation = new LatLng(location.getLatitude(), location.getLongitude());
        animateToPlace(initialLocation);
        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
    }


    private void showWarningDialogue() {
        Tools.showDialogue(this, new OnAcceptListener() {
            @Override
            public void onAccept() {
                cancelRun();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int currentItem = view.getId();
        switch (currentItem) {
            case R.id.start_button: {
                if (!isServiceStarted) {
                    startActivityForResult(new Intent(HomeActivity.this, PrepareRunActivity.class), Constants.CODE_START_RACE);
                }
                break;
            }

            case R.id.stop_button: {
                if (isServiceStarted) {
                    showWarningDialogue();
                }
                break;
            }

            case R.id.pause_button: {
                pauseRace();
            }

            case R.id.location_button: {
                if (lastLocation != null) {
                    animateToPlace(lastLocation);
                } else {
                    animateToPlace(initialLocation);
                }
            }
        }
    }

    private void pauseRace() {
        Intent intent = new Intent(Constants.STOP_SERVICE_BROADCAST);
        if (!isPause) {
            pause.setText("Continuar");
            intent.putExtra(Constants.EXTRAS_STATE_TIME, true);
        } else {
            pause.setText("Pausar");
            intent.putExtra(Constants.EXTRAS_STATE_TIME, false);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        isPause = !isPause;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CODE_START_RACE) {
            startRun();
        }


    }

    private void startRun() {
        changeStatusBarColor(HomeActivity.this.getResources().getColor(R.color.red));
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        startButton.setVisibility(View.INVISIBLE);
        startService(serviceIntent);
        isServiceStarted = true;
    }


}
