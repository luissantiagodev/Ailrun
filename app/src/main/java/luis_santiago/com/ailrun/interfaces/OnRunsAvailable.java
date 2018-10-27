package luis_santiago.com.ailrun.interfaces;

import java.util.ArrayList;

import luis_santiago.com.ailrun.POJOS.Run;

/**
 * Created by Luis Santiago on 10/26/18.
 */
public interface OnRunsAvailable {
    void onDataLoaded(ArrayList <Run> list);
}
