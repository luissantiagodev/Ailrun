package luis_santiago.com.ailrun.POJOS;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Luis Santiago on 10/16/18.
 */
public class CustomLocation implements Parcelable {

    double Latng;
    double Longt;

    public CustomLocation(double latng, double longt) {
        Latng = latng;
        Longt = longt;
    }


    protected CustomLocation(Parcel in) {
        Latng = in.readDouble();
        Longt = in.readDouble();
    }

    public static final Creator<CustomLocation> CREATOR = new Creator<CustomLocation>() {
        @Override
        public CustomLocation createFromParcel(Parcel in) {
            return new CustomLocation(in);
        }

        @Override
        public CustomLocation[] newArray(int size) {
            return new CustomLocation[size];
        }
    };

    public double getLatng() {
        return Latng;
    }

    public void setLatng(double latng) {
        Latng = latng;
    }

    public double getLongt() {
        return Longt;
    }

    public void setLongt(double longt) {
        Longt = longt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(Latng);
        parcel.writeDouble(Longt);
    }

    @Override
    public String toString() {
        return "CustomLocation{" +
                "Latng=" + Latng +
                ", Longt=" + Longt +
                '}';
    }
}
