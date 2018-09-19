package luis_santiago.com.ailrun.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import de.hdodenhof.circleimageview.CircleImageView;
import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.POJOS.User;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;
import luis_santiago.com.ailrun.helpers.GlideApp;
import luis_santiago.com.ailrun.interfaces.IUser;
import luis_santiago.com.ailrun.services.LocationService;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, View.OnClickListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        setUpDrawer();
        setUpWindow();
        setUpUserImage();
        init();
        setUpGoogleClient();
        if (checkLocationAvailable()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            handleReceiveBroadCast(context, intent);
                        }
                    }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
            );
        }

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
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
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(this);
        stop_button = findViewById(R.id.stop_button);
        stop_button.setOnClickListener(this);
        time_lapse = findViewById(R.id.time_lapse);
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
        if(mPolyline != null){
            mPolyline.remove();
        }
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.MAX_ZOOM_MAP));
    }

    private void setUpGoogleClient() {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL_LONG);
        mLocationRequest.setFastestInterval(Constants.LOCATION_INTERVAL_LONG);
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();
    }

    private void handleReceiveBroadCast(Context context, Intent intent) {
        Double latitude = Double.parseDouble(intent.getStringExtra(Constants.EXTRA_LATITUDE));
        Double longitude = Double.parseDouble(intent.getStringExtra(Constants.EXTRA_LONGITUDE));
        Log.e(TAG, "LONG" + longitude);
        Log.e(TAG, "LAT" + latitude);
        Log.e(TAG, "TIME" + intent.getExtras().toString());
        lastLocation = new LatLng(latitude, longitude);
//        time_lapse.setText(String.valueOf(timeLapse));
        animateToPlace(lastLocation);
        mPolyline = mMap.addPolyline(new PolylineOptions()
                .add(initialLocation, lastLocation)
                .width(5)
                .color(Color.RED));
    }

    private void animateToPlace(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
                GlideApp
                        .with(HomeActivity.this)
                        .load(user.getUrlImage())
                        .placeholder(R.drawable.logo_ailrun)
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
        LatLng myLocation = new LatLng(18.141822, -94.482907);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, Constants.MAX_ZOOM_MAP));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
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
        initialLocation = new LatLng(location.getLatitude(), location.getLongitude());
        animateToPlace(initialLocation);
        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
    }


    private void showWarningDialogue() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Estas seguro de cancelar esta carrera?")
                .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelRun();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setTitle("Cancelar carrera")
                .create();
        dialog.show();

    }

    @Override
    public void onClick(View view) {
        int currentItem = view.getId();
        switch (currentItem) {
            case R.id.start_button: {
                if (!isServiceStarted) {
                    startActivityForResult(new Intent(HomeActivity.this , PrepareRunActivity.class), Constants.CODE_START_RACE);
                }
                break;
            }

            case R.id.stop_button: {
                if (isServiceStarted) {
                    showWarningDialogue();
                }
                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.CODE_START_RACE){
            startRun();
        }
    }

    private void startRun() {
        changeStatusBarColor(HomeActivity.this.getResources().getColor(R.color.red));
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        startButton.setVisibility(View.INVISIBLE);
        startService(new Intent(HomeActivity.this, LocationService.class));
        isServiceStarted = true;
        mMap.animateCamera(CameraUpdateFactory.zoomBy(22f));
    }
}
