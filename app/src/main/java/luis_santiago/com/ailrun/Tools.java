package luis_santiago.com.ailrun;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import luis_santiago.com.ailrun.POJOS.CustomLocation;
import luis_santiago.com.ailrun.POJOS.Run;
import luis_santiago.com.ailrun.POJOS.User;
import luis_santiago.com.ailrun.interfaces.OnAcceptListener;

/**
 * Created by Luis Santiago on 10/7/18.
 */
public class Tools {

    public static void showDialogue(Context context, final OnAcceptListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage("Estas seguro de cancelar esta carrera?")
                .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onAccept();
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

    public static GoogleApiClient generateClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) context)
                .build();
    }

    public static Run parseRun(DataSnapshot dataSnapshot) {
        Run run = new Run();
        String kmRan = (String) dataSnapshot.child("kmRan").getValue();
        String kcaBurned = (String) dataSnapshot.child("kcaBurned").getValue();
        ArrayList<CustomLocation> points = new ArrayList<>();
        if (dataSnapshot.child("points").getValue() != null) {
            for (DataSnapshot childValue : dataSnapshot.child("points").getChildren()) {
                double latng = (Double) childValue.child("Latng").getValue();
                double lontng = (Double) childValue.child("Longt").getValue();
                CustomLocation customLocation = new CustomLocation(latng, lontng);
                points.add(customLocation);
            }
        }

        run.setPoints(points);
        run.setKmRan(Double.parseDouble(kmRan));
        run.setKcaBurned(Double.parseDouble(kcaBurned));

        Log.e("TOOLS", "DATA:" + dataSnapshot.getValue().toString());
        return run;
    }
}
