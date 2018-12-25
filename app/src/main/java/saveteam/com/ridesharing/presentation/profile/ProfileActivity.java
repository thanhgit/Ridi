package saveteam.com.ridesharing.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.database.RidesharingDB;
import saveteam.com.ridesharing.database.model.Profile;
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class ProfileActivity extends AppCompatActivity {
    private boolean update = false;

    @BindView(R.id.btn_change_where_profile)
    ImageView btn_change;
    @BindView(R.id.layout_change_where_profile)
    LinearLayout layout_update_profile;
    @BindView(R.id.btn_update_profile_where_profile)
    AppCompatButton btn_update_profile;
    @BindView(R.id.btn_user_profile_photo_where_profile)
    ImageButton btn_user_profile_photo;

    // update profile
    @BindView(R.id.txt_first_name_where_ui_profile_user)
    EditText txt_first_name;
    @BindView(R.id.txt_last_name_where_ui_profile_user)
    EditText txt_last_name;
    @BindView(R.id.rbg_gender_where_ui_profile_user)
    RadioGroup rbg_gender;
    @BindView(R.id.txt_phone_where_ui_profile_user)
    EditText txt_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        if (getCurrentProfile() != null) {
            txt_first_name.setText(getCurrentProfile().getFirstName());
            txt_last_name.setText(getCurrentProfile().getLastName());
            txt_phone.setText(getCurrentProfile().getPhone());
            if (getCurrentProfile().getGenderString().equals("male")) {
                rbg_gender.check(R.id.rb_male);
            } else {
                rbg_gender.check(R.id.rb_female);
            }
        }
    }

    @OnClick(R.id.btn_change_where_profile)
    public void clickChangeProfile(View view) {
        update = true;
        layout_update_profile.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.btn_update_profile_where_profile)
    public void clickUpdateProfile(View view) {
        update = false;
        layout_update_profile.setVisibility(View.INVISIBLE);
        updateProfile();
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

        DatabaseReference db = FirebaseDB.getInstance().child("profiles");
        db.child(uid).setValue(profile);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ActivityUtils.displayLog("add successfully");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ActivityUtils.displayLog("add error");

            }
        });
    }

    @OnClick(R.id.btn_user_profile_photo_where_profile)
    public void click_user_photo(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 2000);
    }

    private Profile getCurrentProfile() {
        final Profile[] result = {null};
        String uid = SharedRefUtils.getUid(this);
        GetProfileByIdTask task = new GetProfileByIdTask(this, uid, new GetProfileByIdTask.GetProfileListener() {
            @Override
            public void done(Profile profile) {
                result[0] = profile;
            }
        });
        task.execute();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2000) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            btn_user_profile_photo.setImageBitmap(getRoundedShape(photo));
        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        // TODO Auto-generated method stub
        int targetWidth = 96;
        int targetHeight = 96;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    private static class GetProfileByIdTask extends AsyncTask<Void, Void, Void> {
        Context context;
        String uid;
        GetProfileListener listener;

        public interface GetProfileListener {
            void done(Profile profile);
        }

        public GetProfileByIdTask(Context context, String uid, GetProfileListener listener ) {
            this.context = context;
            this.uid = uid;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RidesharingDB db = RidesharingDB.getInstance(context);
            Profile[] profiles = db.getProfileDao().loadProfileBy(uid);
            Profile profile = profiles != null && profiles.length > 0 ? profiles[0] : null;
            listener.done(profile);
            return null;
        }
    }

}
