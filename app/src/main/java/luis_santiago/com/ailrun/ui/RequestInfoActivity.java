package luis_santiago.com.ailrun.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;
import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.POJOS.User;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.FirebaseHelper;
import luis_santiago.com.ailrun.helpers.GlideApp;
import luis_santiago.com.ailrun.interfaces.IUser;

public class RequestInfoActivity extends AppCompatActivity {

    private CircleImageView profilePicture;
    private TextInputEditText heightEditText;
    private TextInputEditText weightEditText;
    private TextInputEditText ageEditText;
    private TextView name_text;
    private FloatingActionButton continueButton;
    private RadioButton men_option;
    private RadioButton women_option;
    private LinearLayout container_men;
    private LinearLayout container_women;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_info);
        init();
        if (getIntent().getExtras() != null) {
            String urlImage = getIntent().getExtras().getString(Constants.EXTRAS_URL_PROFILE_IMAGE);
            String name = getIntent().getExtras().getString(Constants.EXTRAS_PROFILE_NAME);
            String uid = getIntent().getExtras().getString(Constants.EXTRAS_PROFILE_UID);
            Log.e("REQUEST ACTIVITY", "THE NAME IS" + name);
            Log.e("REQUEST ACTIVITY", "THE UID IS" + uid);
            Log.e("REQUEST ACTIVITY", "THE URL IMAGE IS" + uid);
            name_text.setText(name + ", Cuentanos mas sobre ti");
            setProfilePicture(urlImage);
        }

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String height = heightEditText.getText().toString();
                final String weight = weightEditText.getText().toString();
                final String age = ageEditText.getText().toString();
                if (!TextUtils.isEmpty(height)) {
                    if (!TextUtils.isEmpty(weight)) {
                        if (!TextUtils.isEmpty(age)) {
                            FirebaseHelper.getInstance().getUserInfo(new IUser() {
                                @Override
                                public void onUserLoaded(User user) {
                                    int sexSelected = 0;

                                    if(men_option.isChecked()){
                                        sexSelected = Constants.MEN_OPTION_SELECTED;
                                    }else {
                                        sexSelected = Constants.WOMEN_OPTION_SELECTED;
                                    }

                                    user.setWeight(Double.parseDouble(weight));
                                    user.setHeight(Double.parseDouble(height));
                                    user.setAge(Integer.valueOf(age));
                                    user.setSexOption(sexSelected);

                                    FirebaseHelper.getInstance().updateUserInfo(user, new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            finish();
                                        }
                                    });
                                }
                            });
                        }else{
                            ageEditText.setError("Se necesita una edad");
                        }
                    } else {
                        weightEditText.setError("Se necesita un peso");
                    }
                } else {
                    heightEditText.setError("Se necesita una altura");
                }
            }
        });

        men_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                women_option.setChecked(false);
            }
        });

        women_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                men_option.setChecked(false);
            }
        });

        container_men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                men_option.performClick();
            }
        });

        container_women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                women_option.performClick();
            }
        });
    }

    private void init() {
        profilePicture = findViewById(R.id.profilePicture);
        heightEditText = findViewById(R.id.edit_height);
        weightEditText = findViewById(R.id.edit_weight);
        continueButton = findViewById(R.id.continueButton);
        name_text = findViewById(R.id.name_text);
        men_option = findViewById(R.id.men_option);
        women_option = findViewById(R.id.women_option);
        container_men = findViewById(R.id.container_men);
        container_women = findViewById(R.id.container_women);
        ageEditText = findViewById(R.id.edit_age);
    }


    private void setProfilePicture(String url) {
        GlideApp
                .with(this)
                .load(url)
                .placeholder(R.drawable.oficial_logo)
                .into(profilePicture);
    }

    @Override
    public void onBackPressed() {

    }
}
