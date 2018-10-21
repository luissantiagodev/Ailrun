package luis_santiago.com.ailrun.Activities;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;
import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.POJOS.CustomLocation;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.GlideApp;

public class PublishRunActivity extends AppCompatActivity implements OnMapReadyCallback {


    private ArrayList<CustomLocation> points = new ArrayList<>();
    private android.support.v7.widget.Toolbar toolbar;
    private double kilometersRan = 0;
    private double secondsPassed = 0;
    private CircleImageView circleImageView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_run);
        init();
        if (getIntent().getExtras() != null) {
            points = getIntent().getExtras().getParcelableArrayList(Constants.EXTRAS_POINTS);
            secondsPassed = getIntent().getExtras().getDouble(Constants.EXTRAS_TIME_PASSED);
            kilometersRan = getIntent().getExtras().getDouble(Constants.EXTRAS_DISTANCE_PASSED);
            String urlImage = getIntent().getExtras().getString(Constants.EXTRAS_URL_PROFILE_IMAGE);
            loadProfileImage(urlImage);
        }

        Log.e("PUBLISH LOCATION", "RESULTS:" + points);
    }

    private void loadProfileImage(String urlImage) {
        GlideApp
                .with(getApplicationContext())
                .load(urlImage)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        circleImageView.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(circleImageView);
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Publicar carrera");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        circleImageView = findViewById(R.id.profilePicture);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        PolylineOptions options = new PolylineOptions().width(5).color(R.color.colorPrimary).geodesic(true);
        LatLng initialLocation = new LatLng(points.get(0).getLatng(), points.get(0).getLongt());
        LatLng lastLocation = new LatLng(points.get(points.size() - 1).getLatng(), points.get(points.size() - 1).getLongt());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(initialLocation);
        builder.include(lastLocation);
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        googleMap.animateCamera(cameraUpdate);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_exercise_filled_100);
        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.icons8_finish_flag_96);


//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 17));
        googleMap.addMarker(new MarkerOptions().position(initialLocation).title("Posición Inicial").icon(icon));
        googleMap.addMarker(new MarkerOptions().position(lastLocation).title("Posición Final").icon(icon2));
        for (int z = 0; z < points.size(); z++) {
            LatLng point = new LatLng(points.get(z).getLatng(), points.get(z).getLongt());
            options.add(point);
        }
        googleMap.addPolyline(options);
    }
}
