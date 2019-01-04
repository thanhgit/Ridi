package saveteam.com.ridesharing.presentation.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.presentation.LoginActivity;
import saveteam.com.ridesharing.presentation.home.MainActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;

public class RegisterProfileActivity extends AppCompatActivity {
    @BindView(R.id.btn_activate_account_where_register_profile)
    AppCompatButton btn_activate_account;
    @BindView(R.id.txt_phone_where_ui_profile_user)
    EditText txt_phone;
    @BindView(R.id.txt_first_name_where_ui_profile_user)
    EditText txt_first_name;
    @BindView(R.id.txt_last_name_where_ui_profile_user)
    EditText txt_last_name;
    @BindView(R.id.rbg_gender_where_ui_profile_user)
    RadioGroup rbg_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);

        ButterKnife.bind(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedRefUtils.signout(this);
    }


    @OnClick(R.id.btn_activate_account_where_register_profile)
    public void clickActivateAccount(View view) {
        if (validateEntering()) {
            Intent intent  = new Intent(this, ActivateCodeActivity.class);
            String uid = SharedRefUtils.getUid(this);
            boolean gender = rbg_gender.getCheckedRadioButtonId() == R.id.rb_male ? true : false;

            ProfileFB profile = new ProfileFB(uid,
                    txt_first_name.getText().toString(),
                    txt_last_name.getText().toString(),
                    "",
                    txt_phone.getText().toString(),
                    gender);
            intent.putExtra("profile", profile);
            startActivity(intent);
        }
    }

    private boolean validateEntering() {
        boolean isPhone = txt_phone.getText().toString().length() >=8 ;
        boolean isName = txt_first_name.getText().length() > 0 || txt_last_name.getText().length() >0;

        return isPhone && isName;
    }

}
