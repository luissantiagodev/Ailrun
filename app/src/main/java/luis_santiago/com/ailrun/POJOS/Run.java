package luis_santiago.com.ailrun.POJOS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Luis Santiago on 10/24/18.
 */
public class Run {

    private ArrayList<CustomLocation> points;
    private double kmRan;
    private String timeElapsedMs;
    private double velocity;
    private double kcaBurned;
    private boolean isPublishedToGlobal;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getKcaBurned() {
        return kcaBurned;
    }

    public void setKcaBurned(double kcaBurned) {
        this.kcaBurned = kcaBurned;
    }

    public boolean isPublishedToGlobal() {
        return isPublishedToGlobal;
    }

    public void setPublishedToGlobal(boolean publishedToGlobal) {
        isPublishedToGlobal = publishedToGlobal;
    }

    public ArrayList<CustomLocation> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<CustomLocation> points) {
        this.points = points;
    }

    public double getKmRan() {
        return kmRan;
    }

    public void setKmRan(double kmRan) {
        this.kmRan = kmRan;
    }

    public String getTimeElapsedMs() {
        return timeElapsedMs;
    }

    public void setTimeElapsedMs(String timeElapsedMs) {
        this.timeElapsedMs = timeElapsedMs;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public HashMap<String, Object> toHash() {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        ArrayList<HashMap<String, Object>> pointsHash = new ArrayList<>();
        for (CustomLocation customLocation : points) {
            pointsHash.add(customLocation.toHash());
        }

        objectHashMap.put("points", pointsHash);
        objectHashMap.put("kmRan" , String.valueOf(kmRan));
        objectHashMap.put("timeElapsed" , String.valueOf(timeElapsedMs));
        objectHashMap.put("velocity" , String.valueOf(velocity));
        objectHashMap.put("kcaBurned" , String.valueOf(kcaBurned));
        objectHashMap.put("date" , new Date().toString());

        return objectHashMap;
    }
}
