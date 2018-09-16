package luis_santiago.com.ailrun;

import java.util.ArrayList;

/**
 * Created by Luis Santiago on 9/9/18.
 */
public class Constants {

    public static final long LOCATION_INTERVAL = 1000;
    public static final long LOCATION_FASTEST_INTERVAL = 1000;

    public static ArrayList<String> phrases(){
        ArrayList<String> phrases = new ArrayList<>();
        phrases.add("“Quien compite con los demás es ganador, pero quien compite consigo mismo es poderoso.”");
        phrases.add("“Correr largo y duro es un antidepresivo ideal, ya que es difícil correr y sentir lástima por ti mismo, al mismo tiempo” ");
        phrases.add("“Correr es un deporte mental… y todos nosotros estamos locos”");
        return phrases;
    }


    public final int RC_SIGN_IN = 4;
    public static String EXTRA_LATITUDE = "extra_longitud";
    public static String EXTRA_LONGITUDE = "extra_longitude";

}
