package luis_santiago.com.ailrun.helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
                    isWithSocialMedia = true;
                }
            }
            if (isWithSocialMedia) {
                final User user = new User(
                        mAuth.getCurrentUser().getDisplayName(),
                        mAuth.getCurrentUser().getPhotoUrl().toString(),
                        mAuth.getUid()
                );

                //Search if he has the
                mDatabaseReference.getReference("users")
                        .child(mAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String weight = (String) dataSnapshot.child("weight").getValue();
                                    String height = (String) dataSnapshot.child("height").getValue();
                                    Log.e("HELPER" , "WEIGTH " + weight);
                                    Log.e("HELPER" , "HEIGHT " + height);
                                    if(weight != null && height!= null){
                                        user.setHeight(Double.valueOf(height));
                                        user.setWeight(Double.valueOf(weight));
                                    }
                                }
                                events.onUserLoaded(user);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    public void updateUserInfo(User user , OnCompleteListener onCompleteListener){
        mDatabaseReference.getReference("users")
                .child(mAuth.getCurrentUser().getUid())
                .updateChildren(user.toHash())
                .addOnCompleteListener(onCompleteListener);
    }


}
