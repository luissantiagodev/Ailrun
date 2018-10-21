package luis_santiago.com.ailrun.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;
import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.POJOS.CustomLocation;
import luis_santiago.com.ailrun.POJOS.User;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.Tools;
import luis_santiago.com.ailrun.fragments.HistoryFragment;
import luis_santiago.com.ailrun.fragments.ProfileFragment;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;
import luis_santiago.com.ailrun.helpers.GlideApp;
import luis_santiago.com.ailrun.interfaces.IUser;
import luis_santiago.com.ailrun.interfaces.OnAcceptListener;
import luis_santiago.com.ailrun.services.LocationService;
import luis_santiago.com.ailrun.business_logic.HealthCalculations;

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
    private ArrayList<CustomLocation> points;
    private User mUser;
    private NavigationView navigationView;
    private SupportMapFragment mapFragment;
    private CircleImageView profileDrawer;
    private double caloriesBurned;
    private double totalDistancePassed = 0;
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
        FirebaseHelper.getInstance().getUserInfo(new IUser() {
            @Override
            public void onUserLoaded(User user) {
                mUser = user;
                View header = navigationView.getHeaderView(0);
                initProfileFromDrawer(header);
            }
        });
        serviceIntent = new Intent(HomeActivity.this, LocationService.class);
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
        mapFragment = new SupportMapFragment();
        mapFragment.getMapAsync(this);
        setUpFragment(mapFragment);
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
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpFragment(new ProfileFragment());
            }
        });
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
        Intent intent = new Intent(this, PublishRunActivity.class);
        intent.putParcelableArrayListExtra(Constants.EXTRAS_POINTS, points);
        intent.putExtra(Constants.EXTRAS_TIME_PASSED, msPassed);
        intent.putExtra(Constants.EXTRAS_CALORIES_BURNED, caloriesBurned);
        intent.putExtra(Constants.EXTRAS_URL_PROFILE_IMAGE , mUser.getUrlImage());
        intent.putExtra(Constants.EXTRAS_DISTANCE_PASSED, totalDistancePassed);
        startActivity(intent);
        if (mPolyline != null) {
            mPolyline.remove();
        }

        mPolyline = null;
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.MAX_ZOOM_MAP));
        stopService(serviceIntent);
        points.clear();
    }

    private void setUpGoogleClient() {
        mLocationClient = Tools.generateClient(this);
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL_LONG);
        mLocationRequest.setFastestInterval(Constants.LOCATION_INTERVAL_LONG);
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();
    }


    private void handleReceiveBroadCast(Intent intent) {
        Double latitude = Double.parseDouble(intent.getStringExtra(Constants.EXTRA_LATITUDE));
        Double longitude = Double.parseDouble(intent.getStringExtra(Constants.EXTRA_LONGITUDE));
        Log.e(TAG, "LONG" + longitude);
        Log.e(TAG, "LAT" + latitude);
        lastLocation = new LatLng(latitude, longitude);
        CustomLocation customLocation = new CustomLocation(latitude, longitude);
        points.add(customLocation);
        animateToPlace(lastLocation);
        PolylineOptions options = new PolylineOptions().width(5).color(R.color.colorPrimary).geodesic(true);
        final double[] totalOfMasRecovered = {0};
        for (int z = 0; z < points.size(); z++) {
            LatLng point = new LatLng(points.get(z).getLatng(), points.get(z).getLongt());
            options.add(point);
        }

        Collections.sort(points, new Comparator<CustomLocation>() {
            @Override
            public int compare(CustomLocation customLocation, CustomLocation ct1) {
                float[] results = new float[1];
                LatLng latLng = new LatLng(customLocation.getLatng(), customLocation.getLongt());
                LatLng t1 = new LatLng(ct1.getLatng(), ct1.getLongt());
                Location.distanceBetween(latLng.latitude, latLng.longitude, t1.latitude, t1.longitude, results);
                totalOfMasRecovered[0] += results[0];
                return 0;
            }
        });

        double finalTotal = totalOfMasRecovered[0];
        mPolyline = mMap.addPolyline(options);
        DecimalFormat df = new DecimalFormat("####0.00");
        Log.e(TAG, "RAW SEG PASSED: " + msPassed + " seg");
        Log.e(TAG, "RAW:" + finalTotal + "  MTS: " + df.format(finalTotal) + " mts KM: " + HealthCalculations.metersToKilometers(Double.parseDouble(df.format(finalTotal))) + "KM Speed: " + String.valueOf(HealthCalculations.velocity(finalTotal, msPassed)));

        totalDistancePassed = finalTotal;
        speed.setText(df.format(HealthCalculations.velocity(finalTotal, msPassed)) + "mts/seg");
        if (mUser != null) {
            caloriesBurned = HealthCalculations.calculateEnergyExpenditure(mUser.getHeight(), mUser.getAge(), mUser.getWeight(), mUser.getSexOption(), msPassed, finalTotal);
            Log.e(TAG, "RAW CALORIES BURNED: " + caloriesBurned + " Kca");
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
                        .with(getApplicationContext())
                        .load(user.getUrlImage())
                        .placeholder(R.drawable.oficial_logo)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                circleImageView.setImageDrawable(resource);
                                profileDrawer.setImageDrawable(resource);
                                return false;
                            }
                        })
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
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initProfileFromDrawer(View header) {
        TextView name1 = header.findViewById(R.id.user_name);
        TextView email = header.findViewById(R.id.email);
        profileDrawer = header.findViewById(R.id.imageView);
        email.setText(mUser.getEmail());
        name1.setText(mUser.getName());
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        circleImageView = findViewById(R.id.circle_image_view);
        setSupportActionBar(toolbar);
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
        }
        //showButtons();
        setUpFragment(mapFragment);
        showButtons();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.main_map: {
                showButtons();
                setUpFragment(mapFragment);
                break;
            }
            case R.id.history_runs: {
                hideButtons();
                setUpFragment(new HistoryFragment());
                break;
            }

            case R.id.settings: {
                setUpFragment(new ProfileFragment());
                hideButtons();
                break;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpFragment(Fragment profileFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map, profileFragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.DEFAULT_LOCATION, Constants.MAX_ZOOM_MAP));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
                    if (initialLocation != null) {
                        startActivityForResult(new Intent(HomeActivity.this, PrepareRunActivity.class), Constants.CODE_START_RACE);
                    } else {
                        Toast.makeText(this, "Necesitas una localizacion", Toast.LENGTH_SHORT).show();
                    }
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


    private void hideButtons() {
        startButton.setVisibility(View.INVISIBLE);
        location_button.setVisibility(View.INVISIBLE);
        circleImageView.setVisibility(View.INVISIBLE);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

    }

    private void showButtons() {
        startButton.setVisibility(View.VISIBLE);
        location_button.setVisibility(View.VISIBLE);
        circleImageView.setVisibility(View.VISIBLE);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));

    }


}
