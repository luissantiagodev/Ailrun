package luis_santiago.com.ailrun.helpers;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    private FirebaseStorage storage = FirebaseStorage.getInstance();

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

    public void uploadImageToFirebase(final String key, Bitmap bitmap, final S3UploadReady dataListener, final OnProgressListener onProgress) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://react-test-5ae29.appspot.com");

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child(key + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnProgressListener(onProgress);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(uri -> {
                    String photoStringLink = uri.toString();
                    dataListener.onImageUpload(photoStringLink);
                });
            }
        });
    }
}
