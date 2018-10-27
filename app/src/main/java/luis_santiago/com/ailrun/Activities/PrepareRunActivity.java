package luis_santiago.com.ailrun.Activities;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.GlideApp;

public class PrepareRunActivity extends AppCompatActivity {

    private ImageView gifTemplate;
    private CountDownTimer mCounterTimer;
    private TextView seconds_left;
    private int currentTime = 0;
    private Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_run);
        init();
        GlideApp.with(this)
                .load(R.raw.running)
                .into(gifTemplate);
        setUpTimer(11000);
        gifTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCounterTimer.onFinish();
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCounterTimer.cancel();
                currentTime = currentTime + 10000;
                setUpTimer(currentTime);
            }
        });
    }


    private void init(){
        gifTemplate = findViewById(R.id.gif_template);
        seconds_left = findViewById(R.id.seconds_left);
        add_button = findViewById(R.id.add_button);
    }

    private void setUpTimer(int resOfMinutes) {
        Log.e("TIMER" , "SETTING TIMER WITH"+ resOfMinutes);
        mCounterTimer = new CountDownTimer(resOfMinutes, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int processMade = (int) (millisUntilFinished / 1000);
                long minutes = processMade / 60;
                int seconds = processMade % 60;
                String template = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
                seconds_left.setText(template);
            }
            @Override
            public void onFinish() {
                close();
            }
        }.start();
    }

    private void close() {
        setResult(Constants.CODE_START_RACE);
        finish();
    }
}
