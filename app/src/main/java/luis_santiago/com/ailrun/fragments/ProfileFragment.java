package luis_santiago.com.ailrun.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.POJOS.User;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;
import luis_santiago.com.ailrun.helpers.GlideApp;
import luis_santiago.com.ailrun.interfaces.IUser;
import luis_santiago.com.ailrun.interfaces.OnUploadReady;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private CircleImageView profilePicture;
    private static final int RESULT_LOAD_IMG = 68;
    private TextInputEditText edit_name;
    private TextInputEditText edit_height;
    private TextInputEditText edit_weight;
    private TextInputEditText edit_age;
    private TextView change_photo;
    private TextView email;
    private Spinner spinner;
    private Bitmap selectedImage;
    private Button update_button;
    private boolean isPhotoChanged = false;
    private User mUser;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        setUpSpinner();
        FirebaseHelper.getInstance().getUserInfo(new IUser() {
            @Override
            public void onUserLoaded(User user) {
                loadProfilePicture(user.getUrlImage());
                edit_name.setText(user.getName());
                edit_height.setText(String.valueOf(user.getHeight()));
                edit_weight.setText(String.valueOf(user.getWeight()));
                edit_age.setText(String.valueOf(user.getAge()));
                email.setText(user.getEmail());
                if (user.getSexOption() == Constants.MEN_OPTION_SELECTED) {
                    spinner.setSelection(0);
                } else {
                    spinner.setSelection(1);
                }

                mUser = user;
            }
        });

        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser != null) {
                    mUser.setName(edit_name.getText().toString());
                    mUser.setHeight(Double.valueOf(edit_height.getText().toString()));
                    mUser.setWeight(Double.valueOf(edit_weight.getText().toString()));
                    mUser.setAge(Integer.valueOf(edit_age.getText().toString()));
                    int position = spinner.getSelectedItemPosition();
                    if (position == 0) {
                        mUser.setSexOption(Constants.MEN_OPTION_SELECTED);
                    } else {
                        mUser.setSexOption(Constants.WOMEN_OPTION_SELECTED);
                    }

                    if(isPhotoChanged){
                        FirebaseHelper.getInstance().uploadImageToFirebase(mUser.getUid(), selectedImage, new OnUploadReady() {
                            @Override
                            public void onImageUpload(String url) {
                                mUser.setUrlImage(url);
                                FirebaseHelper.getInstance().updateUserInfo(mUser, new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        Toast.makeText(getActivity().getApplicationContext() , "Datos actualizados" , Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }, new OnProgressListener() {
                            @Override
                            public void onProgress(Object o) {

                            }
                        });
                    }else{
                        FirebaseHelper.getInstance().updateUserInfo(mUser, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Toast.makeText(getActivity().getApplicationContext() , "Datos actualizados" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
        return view;
    }

    private void setUpSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Hombre");
        list.add("Mujer");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void loadProfilePicture(String urlImage) {
        GlideApp
                .with(getActivity().getApplicationContext())
                .load(urlImage)
                .placeholder(R.drawable.oficial_logo)
                .into(profilePicture);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                if (data.getData() != null) {
                    isPhotoChanged = true;
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    profilePicture.setImageURI(imageUri);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void init(View view) {
        profilePicture = view.findViewById(R.id.profilePicture);
        edit_name = view.findViewById(R.id.edit_name);
        edit_height = view.findViewById(R.id.edit_height);
        edit_weight = view.findViewById(R.id.edit_weight);
        edit_age = view.findViewById(R.id.edit_age);
        email = view.findViewById(R.id.email);
        spinner = view.findViewById(R.id.spinner);
        change_photo = view.findViewById(R.id.change_photo);
        update_button = view.findViewById(R.id.update_button);
    }
}
