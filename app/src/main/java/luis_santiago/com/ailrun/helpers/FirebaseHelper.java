package luis_santiago.com.ailrun.helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import luis_santiago.com.ailrun.POJOS.User;
import luis_santiago.com.ailrun.interfaces.IUser;

/**
 * Created by Luis Santiago on 9/15/18.
 */
public class FirebaseHelper {

    private static FirebaseHelper firebaseHelper = null;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabaseReference = FirebaseDatabase.getInstance();
    private String TAG = FirebaseHelper.class.getSimpleName();

    private FirebaseHelper() {
    }

    public static FirebaseHelper getInstance() {
        if (firebaseHelper == null) {
            return new FirebaseHelper();
        } else {
            return firebaseHelper;
        }
    }


    public void signInWithCredential(AuthCredential credential, OnCompleteListener<AuthResult> onSuccess) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(onSuccess);
    }

    public void getUserInfo(final IUser events) {
        if (mAuth.getCurrentUser() != null) {
            boolean isWithSocialMedia = false;
            for (UserInfo user : mAuth.getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com") || user.getProviderId().equals("google.com")) {
                    System.out.println("User is signed in with Facebook or google");
                    isWithSocialMedia = true;
                }
            }

            if (isWithSocialMedia) {
                Log.e("FIREBASE", "PROVIDER : " + mAuth.getCurrentUser().getProviderId());
                Log.e("FIREBASE", mAuth.getCurrentUser().getDisplayName());
                Log.e("FIREBASE", mAuth.getCurrentUser().getPhotoUrl().toString());
                events.onUserLoaded(
                        new User(
                                mAuth.getCurrentUser().getDisplayName(),
                                mAuth.getCurrentUser().getPhotoUrl().toString(),
                                mAuth.getUid()
                        )
                );
            }
        }
    }
}
