package luis_santiago.com.ailrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import luis_santiago.com.ailrun.adapters.IntroTextAdapter;
import luis_santiago.com.ailrun.ui.LoginActivity;

public class ElectionLoginActivity extends AppCompatActivity implements View.OnClickListener, FacebookCallback<LoginResult> {

    private ScalableVideoView videoView;
    private Button loginEmail;
    private LoginButton facebuttonLogin;
    private Button googleButton;
    private Button mainContainerFaceButton;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_login);
        init();
        loadVideo();
        loadRecyclerView();
        loadFacebookStats();
        setUpClients();
    }

    private void setUpClients() {
        setUpGoogleClient();
        setUpFacebookClient();
        initListenersForButtons();
    }

    private void initListenersForButtons() {
        loginEmail.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        mainContainerFaceButton.setOnClickListener(this);
        facebuttonLogin.setOnClickListener(this);
    }

    private void setUpFacebookClient() {
        mCallbackManager = CallbackManager.Factory.create();
        facebuttonLogin.setReadPermissions("email", "public_profile");
        facebuttonLogin.registerCallback(mCallbackManager, this);
    }

    private void setUpGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loadFacebookStats() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    private void loadRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(new IntroTextAdapter(Constants.phrases()));
    }

    private void init() {
        videoView = findViewById(R.id.video_background);
        loginEmail = findViewById(R.id.button_email);
        googleButton = findViewById(R.id.button);
        mainContainerFaceButton = findViewById(R.id.hidden_button);
        facebuttonLogin = findViewById(R.id.login_button_facebook);
        mProgressbar = findViewById(R.id.progress_bar_loading);
        loginEmail = findViewById(R.id.login);
    }

    private void loadVideo() {
        try {
            videoView.setRawData(R.raw.video);
            videoView.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoView.setLooping(true);
        videoView.start();
    }

    @Override
    public void onClick(View view) {
        int currentId = view.getId();
        loadProgressBar();
        switch (currentId) {
            case R.id.button_email: {
//                startActivity(new Intent(getBaseContext(), CreateAccountActivity.class));
                stopLoading();
                break;
            }

            case R.id.hidden_button: {
                facebuttonLogin.performClick();
                loadProgressBar();
                break;
            }

            case R.id.button: {
//                signInWithGoogle();
                loadProgressBar();
                break;
            }

            case R.id.login: {
//                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                stopLoading();
            }
        }

        startActivity(new Intent(this , MapsActivity.class));
    }

    @Override
    public void onSuccess(LoginResult loginResult) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    private void loadProgressBar() {
        mProgressbar.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        mProgressbar.setVisibility(View.INVISIBLE);
    }

}
