package luis_santiago.com.ailrun.helpers;

import android.content.Context;
import android.os.SystemClock;
import android.widget.Chronometer;

/**
 * Created by Luis Santiago on 9/19/18.
 */
public class ChronometerRunning extends Chronometer{

    public int msElapsed;
    public boolean isRunning = false;

    public ChronometerRunning(Context context) {
        super(context);
    }

    public int getMsElapsed() {
        return msElapsed;
    }

    public void setMsElapsed(int ms) {
        setBase(getBase() - ms);
        msElapsed  = ms;
    }

    @Override
    public void start() {
        super.start();
        setBase(SystemClock.elapsedRealtime() - msElapsed);
        isRunning = true;
    }

    @Override
    public void stop() {
        super.stop();
        if(isRunning) {
            msElapsed = (int)(SystemClock.elapsedRealtime() - this.getBase());
        }
        isRunning = false;
    }

}
