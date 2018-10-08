package luis_santiago.com.ailrun;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Luis Santiago on 9/9/18.
 */
public class Constants {

    public static final long LOCATION_INTERVAL = 1000;
    public static final long LOCATION_INTERVAL_LONG = 100000;
    public static final long LOCATION_FASTEST_INTERVAL = 1000;
    public static final int CODE_START_RACE = 5;
    public static final LatLng DEFAULT_LOCATION = new LatLng(18.141822, -94.482907);
    public static final String EXTRAS_PROFILE_NAME = "extras_profile_name";
    public static final String EXTRAS_PROFILE_UID = "extras_profile_uid";
    public static Long MAX_LIMIT_TIME_RUNNING = 9000000000000000000L;
    public static final String STOP_SERVICE_BROADCAST = "stop_service_broadcast";
    public static String EXTRA_LATITUDE = "extra_latitude";
    public static String EXTRA_LONGITUDE = "extra_longitude";
    public static String EXTRA_MS_LAPSE = "extra_time_lapsed";
    public static final String EXTRAS_STATE_TIME = "state_time";
    public static final String EXTRAS_URL_PROFILE_IMAGE = "profile_image";
    public static final float MAX_ZOOM_MAP = 18.0f;

    public static ArrayList<String> phrases(){
        ArrayList<String> phrases = new ArrayList<>();
        phrases.add("“Quien compite con los demás es ganador, pero quien compite consigo mismo es poderoso.”");
        phrases.add("“Correr largo y duro es un antidepresivo ideal, ya que es difícil correr y sentir lástima por ti mismo, al mismo tiempo” ");
        phrases.add("“Correr es un deporte mental… y todos nosotros estamos locos”");
        return phrases;
    }
}
