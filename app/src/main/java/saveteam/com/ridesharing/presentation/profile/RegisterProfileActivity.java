package saveteam.com.ridesharing.presentation.profile;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.presentation.SearchPlaceActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.DataManager;
import saveteam.com.ridesharing.utils.activity.DateTimePickerUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class RegisterProfileActivity extends AppCompatActivity {

    private static final int HOME_PLACE = 1000;
    private static final int OFFICE_PLACE = 1100;

    @BindView(R.id.btn_activate_account_where_register_profile)
    AppCompatButton btn_activate_account;
    @BindView(R.id.txt_first_name_where_ui_profile_user)
    EditText txt_first_name;
    @BindView(R.id.txt_last_name_where_ui_profile_user)
    EditText txt_last_name;
    @BindView(R.id.rbg_gender_where_ui_profile_user)
    RadioGroup rbg_gender;
    @BindView(R.id.txt_phone_where_ui_profile_user)
    EditText txt_phone;
    @BindView(R.id.btn_find_ride_where_ui_profile_user)
    AppCompatButton btn_find_ride;
    @BindView(R.id.btn_offer_ride_where_ui_profile_user)
    AppCompatButton btn_offer_ride;
    @BindView(R.id.btn_country_where_ui_profile_user)
    AppCompatButton btn_country;
    @BindView(R.id.btn_birth_day_where_ui_profile_user)
    AppCompatButton btn_birth_day;
    @BindView(R.id.btn_home_place_where_ui_profile_user)
    AppCompatButton btn_home_place;
    @BindView(R.id.btn_office_place_where_ui_profile_user)
    AppCompatButton btn_office_place;
    @BindView(R.id.btn_start_time_where_ui_profile_user)
    AppCompatButton btn_start_time;
    @BindView(R.id.btn_leave_office_time_where_ui_profile_user)
    AppCompatButton btn_leave_office_time;

    private DateTimePickerUtils birthday;
    private DateTimePickerUtils startTime;
    private DateTimePickerUtils leaveOfficeTime;

    ProfileFB profile;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);

        getSupportActionBar().setTitle("Register Profile");

        ButterKnife.bind(this);
        uid = SharedRefUtils.getUid(this);

        profile = new ProfileFB();
        profile.setUid(uid);
        profile.setMode("find_ride");


        birthday = new DateTimePickerUtils(this);
        startTime = new DateTimePickerUtils(this);
        leaveOfficeTime = new DateTimePickerUtils(this);

        birthday.initDateTimePicker("birthday", new DateTimePickerUtils.DateTimeListener() {
            @Override
            public void onPositiveButtonClick(Date date, String tagFragment) {
                String sBirthday = ActivityUtils.getDateFormat().format(date);
                btn_birth_day.setHint(sBirthday);
                profile.setBirthday(sBirthday);
            }

            @Override
            public void onNegativeButtonClick(Date date, String tagFragment) {
                btn_birth_day.setHint(getResources().getString(R.string.birth_day_where_profile));
                profile.setBirthday("");
            }

            @Override
            public void onNeutralButtonClick(Date date, String tagFragment) {
                btn_birth_day.setHint(getResources().getString(R.string.birth_day_where_profile));
                profile.setBirthday("");
            }
        });

        startTime.initDateTimePicker("start_time", new DateTimePickerUtils.DateTimeListener() {
            @Override
            public void onPositiveButtonClick(Date date, String tagFragment) {
                String sStartTime = ActivityUtils.getTimeFormat().format(date);
                btn_start_time.setHint(sStartTime);
                profile.setStartTime(sStartTime);
            }

            @Override
            public void onNegativeButtonClick(Date date, String tagFragment) {
                btn_start_time.setHint(getResources().getString(R.string.start_time_where_profile));
                profile.setStartTime("");
            }

            @Override
            public void onNeutralButtonClick(Date date, String tagFragment) {
                btn_start_time.setHint(getResources().getString(R.string.start_time_where_profile));
                profile.setStartTime("");
            }
        });

        leaveOfficeTime.initDateTimePicker("leave_office_time", new DateTimePickerUtils.DateTimeListener() {
            @Override
            public void onPositiveButtonClick(Date date, String tagFragment) {
                String sLeaveOfficeTime = ActivityUtils.getTimeFormat().format(date);
                btn_leave_office_time.setHint(sLeaveOfficeTime);
                profile.setLeaveOfficeTime(sLeaveOfficeTime);
            }

            @Override
            public void onNegativeButtonClick(Date date, String tagFragment) {
                btn_leave_office_time.setHint(getResources().getString(R.string.leave_office_where_profile));
                profile.setLeaveOfficeTime("");
            }

            @Override
            public void onNeutralButtonClick(Date date, String tagFragment) {
                btn_leave_office_time.setHint(getResources().getString(R.string.leave_office_where_profile));
                profile.setLeaveOfficeTime("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedRefUtils.signout(this);
    }

    @OnClick(R.id.btn_find_ride_where_ui_profile_user)
    public void clickFindRide(View view) {
        btn_find_ride.setBackgroundColor(getResources().getColor(R.color.no_transparent));
        btn_offer_ride.setBackgroundColor(getResources().getColor(R.color.transparent));
        profile.setMode("find_ride");
    }

    @OnClick(R.id.btn_offer_ride_where_ui_profile_user)
    public void clickOfferRide(View view) {
        btn_find_ride.setBackgroundColor(getResources().getColor(R.color.transparent));
        btn_offer_ride.setBackgroundColor(getResources().getColor(R.color.no_transparent));
        profile.setMode("offer_ride");
    }


    @OnClick(R.id.btn_activate_account_where_register_profile)
    public void clickActivateAccount(View view) {
        if (validateEntering()) {
            Intent intent  = new Intent(this, ActivateCodeActivity.class);
            boolean gender = rbg_gender.getCheckedRadioButtonId() == R.id.rb_male ? true : false;

            profile.setGender(gender);
            profile.setPhone(txt_phone.getText().toString());
            profile.setFirstName(txt_first_name.getText().toString());
            profile.setLastName(txt_last_name.getText().toString());
            // intent.putExtra("profile", profile);
            DataManager.getInstance().setProfile(profile);
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_birth_day_where_ui_profile_user)
    public void clickBirthDay(View view) {
        birthday.openDateTimePicker();
    }

    @OnClick(R.id.btn_start_time_where_ui_profile_user)
    public void clickStartTime(View view) {
        startTime.openTimePicker();
    }

    @OnClick(R.id.btn_leave_office_time_where_ui_profile_user)
    public void clickLeaveOfficeTime(View view) {
        leaveOfficeTime.openTimePicker();
    }

    @OnClick(R.id.btn_home_place_where_ui_profile_user)
    public void clickHomePlace(View view) {
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,HOME_PLACE);
    }

    @OnClick(R.id.btn_office_place_where_ui_profile_user)
    public void clickOfficePlace(View view) {
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,OFFICE_PLACE);
    }

    private boolean validateEntering() {
        boolean isPhone = txt_phone.getText().toString().length() >=8 ;
        boolean isName = txt_first_name.getText().length() > 0 || txt_last_name.getText().length() >0;

        return isPhone && isName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == HOME_PLACE && resultCode == RESULT_OK) {
            Geo start = (Geo) data.getSerializableExtra("data");
            String title = data.getStringExtra("title");
            String placeId = data.getStringExtra("placeId");

            if (start != null) {
                btn_home_place.setHint(title);
                profile.setHomePlace(title+"|"+start.lat+"|"+start.lng);
            }
        }

        if (requestCode == OFFICE_PLACE && resultCode == RESULT_OK ) {
            Geo end = (Geo) data.getSerializableExtra("data");
            String title = data.getStringExtra("title");
            String placeId = data.getStringExtra("placeId");

            if (end != null) {
                btn_office_place.setHint(title);
                profile.setOfficePlace(title+"|"+end.lat+"|"+end.lng);
            }
        }
    }

}
