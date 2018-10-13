package luis_santiago.com.ailrun.tools;

import luis_santiago.com.ailrun.Constants;

/**
 * Created by Luis Santiago on 10/13/18.
 */
public class HealthCalculations {

    public static Double metersToKilometers(double mts) {
        return mts / 1000;
    }


    private static Long milisecondsToSeconds(long miliseconds) {
        return miliseconds / 1000;
    }


    public static Double velocity(double distance, long time) {
        return distance / time;
    }

}
