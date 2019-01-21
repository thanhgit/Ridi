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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.database.DBUtils;
import saveteam.com.ridesharing.database.model.Profile;
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.firebase.FirebaseUtils;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class ProfileActivity extends AppCompatActivity {
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

    // profile
    @BindView(R.id.tv_user_name_where_profile)
    TextView tv_user_name;
    @BindView(R.id.tv_phone_where_profile)
    TextView tv_phone;
    @BindView(R.id.tv_email_where_profile)
    TextView tv_email;
    @BindView(R.id.tv_gender_where_profile)
    TextView tv_gender;

    Profile mProfile;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading profile ...");
        dialog.show();

        layout_update_profile.setVisibility(View.INVISIBLE);
        layout_profile.setVisibility(View.VISIBLE);

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
        FirebaseUtils.uploadImageFile(uid, iv_user_profile_photo, new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        updateProfile();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ActivityUtils.displayToast(ProfileActivity.this, "Not update");
                    }
                });
    }

    private void updateProfile() {
        String uid = SharedRefUtils.getUid(this);
        boolean gender = rbg_gender.getCheckedRadioButtonId() == R.id.rb_male ? true : false;

        ProfileFB profile = new ProfileFB(uid,
                txt_first_name.getText().toString(),
                txt_last_name.getText().toString(),
                "",
                txt_phone.getText().toString(),
                gender);

        DatabaseReference db = FirebaseDB.getInstance().child(ProfileFB.DB_IN_FB);
        Map<String, Object> obj = new HashMap<>();
        obj.put(uid, profile);
        db.updateChildren(obj, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    ActivityUtils.displayLog("add profile success");
                } else {
                    ActivityUtils.displayLog("add profile false");
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
        DBUtils.GetProfileByIdTask task = new DBUtils.GetProfileByIdTask(this, uid, new DBUtils.GetProfileByIdTask.GetProfileListener() {
            @Override
            public void done(Profile profile) {
                mProfile = profile;

                if (mProfile != null) {
                    tv_user_name.setText(mProfile.getFirstName() +" " + mProfile.getLastName());
                    tv_email.setText(SharedRefUtils.getEmail(ProfileActivity.this));
                    tv_phone.setText(mProfile.getPhone());
                    tv_gender.setText(mProfile.getGenderString());
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        });
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2000) {
            Uri photo = data.getData();

            Glide.with(this).load(photo)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.5f)
                    .into(iv_user_profile_photo);
        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
