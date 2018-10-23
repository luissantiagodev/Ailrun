package luis_santiago.com.ailrun;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import luis_santiago.com.ailrun.interfaces.OnAcceptListener;

/**
 * Created by Luis Santiago on 10/7/18.
 */
public class Tools {

    public static void showDialogue(Context context , final OnAcceptListener listener){
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
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setTitle("Cancelar carrera")
                .create();
        dialog.show();
    }

    public static GoogleApiClient generateClient (Context context){
        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) context)
                .build();
    }
}
