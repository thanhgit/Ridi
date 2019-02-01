package saveteam.com.ridesharing.presentation.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.database.DBUtils;
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.firebase.FirebaseUtils;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.presentation.SearchPlaceActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.DataManager;
import saveteam.com.ridesharing.utils.activity.DateTimePickerUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class ProfileActivity extends AppCompatActivity {
    private static final int HOME_PLACE = 1000;
    private static final int OFFICE_PLACE = 1100;

    private boolean update = false;

    @BindView(R.id.btn_edit_profile_where_profile)
    ImageView btn_edit_profile;
    @BindView(R.id.btn_user_profile_photo_where_profile)
    ImageView iv_user_profile_photo;

    @BindView(R.id.layout_update_profile_where_profile)
    LinearLayout layout_update_profile;
    @BindView(R.id.layout_profile_where_profile)
    LinearLayout layout_profile;

    // update profile
    @BindView(R.id.txt_first_name_where_ui_profile_user)
    EditText txt_first_name;
    @BindView(R.id.txt_last_name_where_ui_profile_user)
    EditText txt_last_name;
    @BindView(R.id.rbg_gender_where_ui_profile_user)
    RadioGroup rbg_gender;
    @BindView(R.id.txt_phone_where_ui_profile_user)
    EditText txt_phone;
    @BindView(R.id.btn_update_profile_where_profile)
    AppCompatButton btn_update_profile;
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


    // profile
    @BindView(R.id.tv_user_name_where_profile)
    TextView tv_user_name;
    @BindView(R.id.btn_phone_where_profile)
    AppCompatButton phone;
    @BindView(R.id.btn_email_where_profile)
    AppCompatButton email;
    @BindView(R.id.btn_mode_where_profile)
    AppCompatButton mode;
    @BindView(R.id.btn_gender_where_profile)
    AppCompatButton gender;
    @BindView(R.id.btn_birth_day_where_profile)
    AppCompatButton birth_day;
    @BindView(R.id.btn_home_place_where_profile)
    AppCompatButton home_place;
    @BindView(R.id.btn_office_place_where_profile)
    AppCompatButton office_place;
    @BindView(R.id.btn_start_time_where_profile)
    AppCompatButton start_time;
    @BindView(R.id.btn_leave_office_time_where_profile)
    AppCompatButton leave_office_time;


    ProfileFB mProfile;

    ProgressDialog dialog;
    boolean isUpdateAvatar = false;

    private DateTimePickerUtils birthday;
    private DateTimePickerUtils startTime;
    private DateTimePickerUtils leaveOfficeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mProfile = new ProfileFB();

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading profile ...");

        layout_update_profile.setVisibility(View.INVISIBLE);
        layout_profile.setVisibility(View.VISIBLE);

        birthday = new DateTimePickerUtils(this);
        startTime = new DateTimePickerUtils(this);
        leaveOfficeTime = new DateTimePickerUtils(this);

        birthday.initDateTimePicker("birthday", new DateTimePickerUtils.DateTimeListener() {
            @Override
            public void onPositiveButtonClick(Date date, String tagFragment) {
                String sBirthday = ActivityUtils.getDateFormat().format(date);
                btn_birth_day.setHint(sBirthday);
                mProfile.setBirthday(sBirthday);
            }

            @Override
            public void onNegativeButtonClick(Date date, String tagFragment) {
                btn_birth_day.setHint(getResources().getString(R.string.birth_day_where_profile));
                mProfile.setBirthday("");
            }

            @Override
            public void onNeutralButtonClick(Date date, String tagFragment) {
                btn_birth_day.setHint(getResources().getString(R.string.birth_day_where_profile));
                mProfile.setBirthday("");
            }
        });

        startTime.initDateTimePicker("start_time", new DateTimePickerUtils.DateTimeListener() {
            @Override
            public void onPositiveButtonClick(Date date, String tagFragment) {
                String sStartTime = ActivityUtils.getTimeFormat().format(date);
                btn_start_time.setHint(sStartTime);
                mProfile.setStartTime(sStartTime);
            }

            @Override
            public void onNegativeButtonClick(Date date, String tagFragment) {
                btn_start_time.setHint(getResources().getString(R.string.start_time_where_profile));
                mProfile.setStartTime("");
            }

            @Override
            public void onNeutralButtonClick(Date date, String tagFragment) {
                btn_start_time.setHint(getResources().getString(R.string.start_time_where_profile));
                mProfile.setStartTime("");
            }
        });

        leaveOfficeTime.initDateTimePicker("leave_office_time", new DateTimePickerUtils.DateTimeListener() {
            @Override
            public void onPositiveButtonClick(Date date, String tagFragment) {
                String sLeaveOfficeTime = ActivityUtils.getTimeFormat().format(date);
                btn_leave_office_time.setHint(sLeaveOfficeTime);
                mProfile.setLeaveOfficeTime(sLeaveOfficeTime);
            }

            @Override
            public void onNegativeButtonClick(Date date, String tagFragment) {
                btn_leave_office_time.setHint(getResources().getString(R.string.leave_office_where_profile));
                mProfile.setLeaveOfficeTime("");
            }

            @Override
            public void onNeutralButtonClick(Date date, String tagFragment) {
                btn_leave_office_time.setHint(getResources().getString(R.string.leave_office_where_profile));
                mProfile.setLeaveOfficeTime("");
            }
        });

        loadCurrentProfile();
    }

    @OnClick(R.id.btn_edit_profile_where_profile)
    public void clickEditProfile(View view) {
        update = true;
        layout_update_profile.setVisibility(View.VISIBLE);
        layout_profile.setVisibility(View.INVISIBLE);

        if (mProfile != null) {
            txt_phone.setText(mProfile.getPhone());
            txt_first_name.setText(mProfile.getFirstName());
            txt_last_name.setText(mProfile.getLastName());
            if (mProfile.isGender()) {
                rbg_gender.check(R.id.rb_male);
            } else {
                rbg_gender.check(R.id.rb_female);
            }
        }

    }

    @OnClick(R.id.btn_update_profile_where_profile)
    public void clickUpdateProfile(View view) {
        update = false;
        layout_update_profile.setVisibility(View.INVISIBLE);
        layout_profile.setVisibility(View.VISIBLE);
        String uid = SharedRefUtils.getUid(this);
        mProfile.setFirstName(txt_first_name.getText().toString().trim());
        mProfile.setLastName(txt_last_name.getText().toString().trim());
        mProfile.setPhone(txt_phone.getText().toString().trim());
        mProfile.setGender(rbg_gender.getCheckedRadioButtonId() == R.id.rb_male ? true : false);

        updateProfile();
    }

    private void updateProfile() {
        if (isUpdateAvatar) {
            FirebaseUtils.uploadImageFile(mProfile.getUid(), iv_user_profile_photo, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        DatabaseReference db = FirebaseDB.getInstance().child(ProfileFB.DB_IN_FB);
        db.child(mProfile.getUid()).setValue(mProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ActivityUtils.displayLog("add profile success");
                    DataManager.getInstance().setProfile(mProfile);
                    loadCurrentProfile();
                } else {
                    ActivityUtils.displayLog("add profile fail");
                }
            }
        });
    }

    @OnClick(R.id.btn_user_profile_photo_where_profile)
    public void click_user_photo(View view) {
        if (update) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 2000);
        }
    }

    public void loadCurrentProfile() {
        String uid = SharedRefUtils.getUid(this);
        FirebaseUtils.downloadImageFile(uid, new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Glide.with(ProfileActivity.this).load(bitmap)
                                .apply(RequestOptions.circleCropTransform())
                                .thumbnail(0.5f)
                                .into(iv_user_profile_photo);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        mProfile = DataManager.getInstance().getProfile();

        if (mProfile != null) {
            tv_user_name.setText(mProfile.getFirstName() +" " + mProfile.getLastName());
            email.setHint(SharedRefUtils.getEmail(ProfileActivity.this));
            phone.setHint(mProfile.getPhone());
            gender.setHint(mProfile.isGender() ? "male" : "female");
            mode.setHint(mProfile.getMode().equals("find_ride") ? getResources().getString(R.string.find_ride_where_profile) : getResources().getString(R.string.offer_ride_where_profile));
            mode.setCompoundDrawablesWithIntrinsicBounds(mProfile.getMode().equals("find_ride") ? getResources().getDrawable(R.drawable.findride) : getResources().getDrawable(R.drawable.offerride), null, null, null);
            birth_day.setHint(mProfile.getBirthday());

            String[] homes = mProfile.getHomePlace().split("\\|");
            home_place.setHint(homes.length > 0 ? homes[0] : "");

            String[] offices = mProfile.getOfficePlace().split("\\|");
            office_place.setHint(offices.length > 0 ? offices[0] : "");
            start_time.setHint("Start time: " + mProfile.getStartTime());
            leave_office_time.setHint("Leave office time: " + mProfile.getLeaveOfficeTime());

            if (mProfile.getBirthday().equals("")) {
                birth_day.setVisibility(View.GONE);
            } else {
                btn_birth_day.setHint(mProfile.getBirthday());
            }

            if (mProfile.getHomePlace().equals("")) {
                home_place.setVisibility(View.GONE);
            } else {
                btn_home_place.setHint(homes.length > 0 ? homes[0] : "");
            }

            if (mProfile.getOfficePlace().equals("")) {
                office_place.setVisibility(View.GONE);
            } else {
                btn_office_place.setHint(offices.length > 0 ? offices[0] : "");
            }

            if (mProfile.getStartTime().equals("")) {
                start_time.setVisibility(View.GONE);
            } else {
                btn_start_time.setHint("Start time: "+mProfile.getStartTime());
            }

            if (mProfile.getLeaveOfficeTime().equals("")) {
                leave_office_time.setVisibility(View.GONE);
            } else {
                btn_leave_office_time.setHint("Leave office time: " + mProfile.getLeaveOfficeTime());
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2000) {
            isUpdateAvatar = true;
            Uri photo = data.getData();

            Glide.with(this).load(photo)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.5f)
                    .into(iv_user_profile_photo);
        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

        if (requestCode == HOME_PLACE && resultCode == RESULT_OK) {
            Geo start = (Geo) data.getSerializableExtra("data");
            String title = data.getStringExtra("title");
            String placeId = data.getStringExtra("placeId");

            if (start != null) {
                btn_home_place.setHint(title);
                mProfile.setHomePlace(title+"|"+start.lat+"|"+start.lng);
            }
        }

        if (requestCode == OFFICE_PLACE && resultCode == RESULT_OK ) {
            Geo end = (Geo) data.getSerializableExtra("data");
            String title = data.getStringExtra("title");
            String placeId = data.getStringExtra("placeId");

            if (end != null) {
                btn_office_place.setHint(title);
                mProfile.setOfficePlace(title+"|"+end.lat+"|"+end.lng);
            }
        }
    }

    @OnClick(R.id.btn_find_ride_where_ui_profile_user)
    public void clickFindRide(View view) {
        btn_find_ride.setBackgroundColor(getResources().getColor(R.color.no_transparent));
        btn_offer_ride.setBackgroundColor(getResources().getColor(R.color.transparent));
        mProfile.setMode("find_ride");
    }

    @OnClick(R.id.btn_offer_ride_where_ui_profile_user)
    public void clickOfferRide(View view) {
        btn_find_ride.setBackgroundColor(getResources().getColor(R.color.transparent));
        btn_offer_ride.setBackgroundColor(getResources().getColor(R.color.no_transparent));
        mProfile.setMode("offer_ride");
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
}
