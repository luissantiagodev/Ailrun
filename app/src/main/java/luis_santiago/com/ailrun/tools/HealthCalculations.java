package luis_santiago.com.ailrun.tools;

import android.util.Log;

import luis_santiago.com.ailrun.Constants;

/**
 * Created by Luis Santiago on 10/13/18.
 */
public class HealthCalculations {

    public static Double metersToKilometers(double mts) {
        return mts / 1000;
    }


    public static Double velocity(double distance, long time) {
        return distance / time;
    }

    public static double calculateEnergyExpenditure(Double height, long age, Double weight, int gender, long durationInSeconds, double mts) {

        Log.e("HEALTH" , "HEIGHT" + height);
        Log.e("HEALTH" , "AGE" + age);
        Log.e("HEALTH" , "Weight" + weight);
        Log.e("HEALTH" , "Gender" + gender);
        Log.e("HEALTH" , "Duration Seconds" + durationInSeconds);
        Log.e("HEALTH" , "Mts" + mts);

        float harrisBenedictRmR = convertKilocaloriesToMlKmin(harrisBenedictRmr(gender, weight, age, convertMetresToCentimetre(height)), weight);
        double kmTravelled = metersToKilometers(mts);
        float hours = convertSecondsToHours((int) durationInSeconds);
        double speedInMph = convertKilometersToMiles(kmTravelled) / hours;
        float metValue = getMetForActivity((float) speedInMph);
        float constant = 3.5f;
        float correctedMets = metValue * (constant / harrisBenedictRmR);
        Log.e("HEALTH" , "RESULT: " + correctedMets * hours * weight);
        return correctedMets * hours * weight;
    }

    private static double convertKilometersToMiles(double kmTravelled) {
        return kmTravelled / 1.609;
    }

    private static float convertSecondsToHours(int durationInSeconds) {
        return durationInSeconds / 3600;
    }

    private static float convertMetresToCentimetre(Double height) {
        return (float) (height * 100);
    }


    private static float harrisBenedictRmr(int gender, Double weightKg, float age, float heightCm) {
        if (gender == Constants.WOMEN_OPTION_SELECTED) {
            return (float) (655.0955f + (1.8496f * heightCm) + (9.5634f * weightKg) - (4.6756f * age));
        } else {
            return (float) (66.4730f + (5.0033f * heightCm) + (13.7516f * weightKg) - (6.7550f * age));
        }
    }

    public static float convertKilocaloriesToMlKmin(float kilocalories, Double weightKgs) {
        float kcalMin = kilocalories / 1440;
        kcalMin /= 5;
        return (float) ((kcalMin / (weightKgs)) * 1000);
    }


    private static float getMetForActivity(float speedInMph) {
        if (speedInMph < 2.0) {
            return 2.0f;
        } else if (Float.compare(speedInMph, 2.0f) == 0) {
            return 2.8f;
        } else if (Float.compare(speedInMph, 2.0f) > 0 && Float.compare(speedInMph, 2.7f) <= 0) {
            return 3.0f;
        } else if (Float.compare(speedInMph, 2.8f) > 0 && Float.compare(speedInMph, 3.3f) <= 0) {
            return 3.5f;
        } else if (Float.compare(speedInMph, 3.4f) > 0 && Float.compare(speedInMph, 3.5f) <= 0) {
            return 4.3f;
        } else if (Float.compare(speedInMph, 3.5f) > 0 && Float.compare(speedInMph, 4.0f) <= 0) {
            return 5.0f;
        } else if (Float.compare(speedInMph, 4.0f) > 0 && Float.compare(speedInMph, 4.5f) <= 0) {
            return 7.0f;
        } else if (Float.compare(speedInMph, 4.5f) > 0 && Float.compare(speedInMph, 5.0f) <= 0) {
            return 8.3f;
        } else if (Float.compare(speedInMph, 5.0f) > 0) {
            return 9.8f;
        }
        return 0;
    }
}
