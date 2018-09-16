package luis_santiago.com.ailrun.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;

/**
 * Created by Luis Santiago on 9/15/18.
 */
public class SplashActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private Boolean runOnce = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        handleDestination();
    }

    private void routeToAppropriatePage(FirebaseUser user){
        if(user == null){
            startActivity(new Intent(this , ElectionLoginActivity.class));
        }else{
            startActivity(new Intent(SplashActivity.this  , HomeActivity.class));
        }
    }

    private void handleDestination(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        routeToAppropriatePage(user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(runOnce){
            handleDestination();
        }
        runOnce = true;
    }
}
