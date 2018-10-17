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

import java.net.Inet4Address;

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

    private FirebaseHelper(){
        mDatabaseReference.setPersistenceEnabled(true);
    }

    public static FirebaseHelper getInstance() {
        if (firebaseHelper == null) {
            firebaseHelper = new FirebaseHelper();
            return firebaseHelper;
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

                user.setEmail(mAuth.getCurrentUser().getEmail());
                //Search if he has the
                mDatabaseReference.getReference("users")
                        .child(mAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String weight = (String) dataSnapshot.child("weight").getValue();
                                    String height = (String) dataSnapshot.child("height").getValue();
                                    String age = (String) dataSnapshot.child("age").getValue();
                                    String sexOption = (String) dataSnapshot.child("sexOption").getValue();
                                    if(weight != null && height!= null){
                                        user.setHeight(Double.valueOf(height));
                                        user.setWeight(Double.valueOf(weight));
                                    }

                                    if(age != null){
                                        user.setAge(Integer.valueOf(age));
                                    }

                                    if(age != null){
                                        user.setAge(Integer.valueOf(age));
                                    }
                                    if(sexOption != null){
                                        user.setSexOption(Integer.valueOf(sexOption));
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
