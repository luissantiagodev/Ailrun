package luis_santiago.com.ailrun.ui;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.GlideApp;

public class PrepareRunActivity extends AppCompatActivity {

    private ImageView gifTemplate;
    private CountDownTimer mCounterTimer;
    private TextView seconds_left;
    private int currentTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_run);
        init();
        GlideApp.with(this)
                .load(R.raw.running)
                .into(gifTemplate);
        setUpTimer(11000);
    }


    private void init() {
        gifTemplate = findViewById(R.id.gif_template);
        seconds_left = findViewById(R.id.seconds_left);
    }

    private void setUpTimer(int resOfMinutes){
        mCounterTimer = new CountDownTimer(resOfMinutes , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int processMade = (int) (millisUntilFinished / 1000);
                int seconds = processMade % 60;
                seconds_left.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                close();
            }
        }.start();
    }

    private void close(){
        setResult(Constants.CODE_START_RACE);
        finish();
    }
}
