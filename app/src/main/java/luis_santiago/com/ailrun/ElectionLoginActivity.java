package luis_santiago.com.ailrun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.ArrayList;

import luis_santiago.com.ailrun.adapters.IntroTextAdapter;

public class ElectionLoginActivity extends AppCompatActivity {

    private ScalableVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_login);
        init();
        loadVideo();
        loadRecyclerView();
    }

    private void loadRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.list);
        ArrayList<String> phrases = new ArrayList<>();
        phrases.add("Testing");
        phrases.add("Testing 2");
        phrases.add("Testing 3");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this );
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(new IntroTextAdapter(phrases));
    }

    private void init() {
        videoView = findViewById(R.id.video_background);
    }

    private void loadVideo(){
        try {
            videoView.setRawData(R.raw.video);
        } catch (IOException e) {
            Log.e("ELECTION ACTIVITY" , "ERROR ACTIVITY");
            e.printStackTrace();
        }
        try {
            videoView.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        videoView.setLooping(true);
        videoView.start();
    }
}
