package luis_santiago.com.ailrun.Activities;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.POJOS.CustomLocation;
import luis_santiago.com.ailrun.POJOS.Run;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;
import luis_santiago.com.ailrun.helpers.GlideApp;

public class PublishRunActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String TAG = PublishRunActivity.class.getSimpleName();
    private ArrayList<CustomLocation> points = new ArrayList<>();
    private android.support.v7.widget.Toolbar toolbar;
    private double kilometersRan = 0;
    private double miliSecondsPassed = 0;
    private double caloriesBurned = 0;
    private double velocity = 0.0;
    private CircleImageView circleImageView;
    private GoogleMap googleMap;
    private TextView km_textView;
    private TextView kca_textView;
    private TextView time_textView;
    private TextView nameTextview;
    private ImageButton button_save;
    private String template = "0:00:00";
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_run);
        init();
        if (getIntent().getExtras() != null) {
            points = getIntent().getExtras().getParcelableArrayList(Constants.EXTRAS_POINTS);
            miliSecondsPassed = getIntent().getExtras().getDouble(Constants.EXTRAS_TIME_PASSED);
            kilometersRan = getIntent().getExtras().getDouble(Constants.EXTRAS_DISTANCE_PASSED);
            caloriesBurned = getIntent().getExtras().getDouble(Constants.EXTRAS_CALORIES_BURNED);
            velocity = getIntent().getExtras().getDouble(Constants.EXTRAS_VELOCITY);
            String urlImage = getIntent().getExtras().getString(Constants.EXTRAS_URL_PROFILE_IMAGE);
            String nameProfile = getIntent().getExtras().getString(Constants.EXTRAS_PROFILE_NAME);
            loadProfileImage(urlImage);
            Log.e(TAG , "MTS RAN:" + kilometersRan);
            Log.e(TAG , "TIME ELAPSE RAN:" + miliSecondsPassed);

            long minutes = (long) (miliSecondsPassed / 60);
            long seconds = (long) (miliSecondsPassed % 60);
            template = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

            time_textView.setText(template);
            km_textView.setText(kilometersRan +" mts");
            nameTextview.setText("¡Felicidades " + nameProfile +"!");
            kca_textView.setText(caloriesBurned + "kca");
        }


        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = ProgressDialog.show(PublishRunActivity.this, "Subiendo datos",
                        "Cargando. Por favor espere...", true);
                dialog.show();
                Run run = new Run();
                run.setPoints(points);
                run.setTimeElapsedMs(template);
                run.setKcaBurned(caloriesBurned);
                run.setKmRan(kilometersRan);
                run.setVelocity(velocity);
                run.setPublishedToGlobal(checkbox.isChecked());
                FirebaseHelper.getInstance().registerRunForUser(run, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Toast.makeText(getApplicationContext() , "Datos subidos" , Toast.LENGTH_SHORT).show();
                        finish();
                        dialog.dismiss();
                    }
                });
            }
        });
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
        km_textView = findViewById(R.id.km_textView);
        time_textView = findViewById(R.id.time_textView);
        kca_textView = findViewById(R.id.kca_textView);
        button_save = findViewById(R.id.button_save);
        nameTextview = findViewById(R.id.name_text);
        checkbox = findViewById(R.id.checkbox);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (points.size() > 2) {
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
}
