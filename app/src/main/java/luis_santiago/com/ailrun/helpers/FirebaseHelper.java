package luis_santiago.com.ailrun.helpers;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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


}
