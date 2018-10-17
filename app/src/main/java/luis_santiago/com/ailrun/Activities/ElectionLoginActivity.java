package luis_santiago.com.ailrun.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.adapters.IntroTextAdapter;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;

public class ElectionLoginActivity extends AppCompatActivity implements View.OnClickListener, FacebookCallback<LoginResult> {

    private ScalableVideoView videoView;
//    private Button loginEmail;
    private LoginButton facebuttonLogin;
    private Button googleButton;
    private Button mainContainerFaceButton;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
    private ProgressBar mProgressbar;
    private final int RC_SIGN_IN = 4;

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
//        loginEmail.setOnClickListener(this);
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
//        loginEmail = findViewById(R.id.button_email);
        googleButton = findViewById(R.id.button);
        mainContainerFaceButton = findViewById(R.id.hidden_button);
        facebuttonLogin = findViewById(R.id.login_button_facebook);
        mProgressbar = findViewById(R.id.progress_bar_loading);
//        loginEmail = findViewById(R.id.login);
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
//            case R.id.button_email: {
////                startActivity(new Intent(getBaseContext(), CreateAccountActivity.class));
//                stopLoading();
//                break;
//            }

            case R.id.hidden_button: {
                facebuttonLogin.performClick();
                loadProgressBar();
                break;
            }

            case R.id.button: {
                signInWithGoogle();
                loadProgressBar();
                break;
            }

//            case R.id.login: {
////                startActivity(new Intent(getBaseContext(), LoginActivity.class));
//                stopLoading();
//            }
        }


    }

    private void signInWithGoogle() {
        if(mGoogleSignInClient != null){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
        firebaseHelper.signInWithCredential(credential, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginSuccessful();
                } else {
                    //showError();
                    Log.e("TAG", "signInWithCredential:failure", task.getException());
                    stopLoading();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (Throwable e) {
                Log.e("Home Activity", "Google sign in failed", e);
                Log.e("Home Activity", "Google sign in failed", e.fillInStackTrace());
                e.printStackTrace();
                //showError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseHelper.signInWithCredential(credential, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginSuccessful();
                } else {
                    //showError();
                    Log.e("TAG", "signInWithCredential:failure", task.getException());
                    Log.e("TAG", "ERROR GOOGLE", task.getException());
                }
            }
        });
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

    private void loginSuccessful() {
        startActivity(new Intent(this, HomeActivity.class));
        stopLoading();
    }


}
