package luis_santiago.com.ailrun.ui;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import luis_santiago.com.ailrun.Constants;
import luis_santiago.com.ailrun.R;
import luis_santiago.com.ailrun.helpers.GlideApp;

public class RequestInfoActivity extends AppCompatActivity {

    private CircleImageView profilePicture;
    private TextInputEditText heightEditText;
    private TextInputEditText weigtEditText;
    private TextView name_text;
    private Button continueButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_info);
        init();
        if (getIntent().getExtras() != null) {
            String urlImage = getIntent().getExtras().getString(Constants.EXTRAS_URL_PROFILE_IMAGE);
            String name = getIntent().getExtras().getString(Constants.EXTRAS_PROFILE_NAME);
            String uid = getIntent().getExtras().getString(Constants.EXTRAS_PROFILE_UID);
            Log.e("REQUEST ACTIVITY" , "THE NAME IS" + name);
            Log.e("REQUEST ACTIVITY" , "THE UID IS" + uid);
            Log.e("REQUEST ACTIVITY" , "THE URL IMAGE IS" + uid);
            //name_text.setText(name + ", Cuentanos mas sobre ti");
            setProfilePicture(urlImage);
        }

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String height = heightEditText.getText().toString();
                String weight = weigtEditText.getText().toString();

                if(!TextUtils.isEmpty(height)){
                    if(!TextUtils.isEmpty(weight)){
                        //We have the data
                    }else{
                        weigtEditText.setError("Se necesita un peso");
                    }
                }else{
                    heightEditText.setError("Se necesita una altura");
                }
            }
        });
    }

    private void init() {
        profilePicture = findViewById(R.id.profilePicture);
        heightEditText = findViewById(R.id.edit_height);
        weigtEditText = findViewById(R.id.edit_weight);
        continueButton = findViewById(R.id.continueButton);
        name_text = findViewById(R.id.name_text);
    }


    private void setProfilePicture(String url) {
        GlideApp
                .with(this)
                .load(url)
                .placeholder(R.drawable.logo_ailrun)
                .into(profilePicture);
    }

    @Override
    public void onBackPressed() {

    }
}
