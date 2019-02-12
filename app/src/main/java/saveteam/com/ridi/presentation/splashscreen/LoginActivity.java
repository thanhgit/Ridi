package saveteam.com.ridi.presentation.splashscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.firebase.ui.auth.util.ui.SupportVectorDrawablesButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridi.R;
import saveteam.com.ridi.database.DBUtils;
import saveteam.com.ridi.database.model.User;
import saveteam.com.ridi.firebase.model.ProfileFB;
import saveteam.com.ridi.presentation.home.MainActivity;
import saveteam.com.ridi.presentation.splashscreen.onboarder.OnBoardAdapter;
import saveteam.com.ridi.presentation.splashscreen.onboarder.OnBoardItem;
import saveteam.com.ridi.utils.activity.ActivityUtils;
import saveteam.com.ridi.utils.activity.DataManager;
import saveteam.com.ridi.utils.google.MyGoogleAuthen;
import saveteam.com.ridi.utils.MyPermission;
import saveteam.com.ridi.utils.activity.SharedRefUtils;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btn_signin_where_login)
    SupportVectorDrawablesButton btnSignin;
    @BindView(R.id.vp_introduction_where_login)
    ViewPager vp_introduction;

    MyGoogleAuthen authen;

    /**
     * on board
     */
    @BindView(R.id.layout_dots_where_login)
    LinearLayout pager_indicator;

    private int dotsCount;
    private ImageView[] dots;

    private OnBoardAdapter mAdapter;

    int previous_pos=0;


    ArrayList<OnBoardItem> onBoardItems=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loadData();

        mAdapter = new OnBoardAdapter(this,onBoardItems);
        vp_introduction.setAdapter(mAdapter);
        vp_introduction.setCurrentItem(0);
        vp_introduction.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(LoginActivity.this, R.drawable.non_selected_item_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(LoginActivity.this, R.drawable.selected_item_dot));
                int pos=position+1;
                previous_pos=pos;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setUiPageViewController();

        MyPermission.getInstance(this).requestPermission(ActivityUtils.LOGIN_REQUIRED_PERMISSIONS);

        authen = new MyGoogleAuthen(this, new MyGoogleAuthen.CheckSignInListener() {
            @Override
            public void success(FirebaseUser user) {
                User requestUser = new User(user.getUid(),
                        user.getDisplayName(),
                        user.getEmail(),
                        user.getPhoneNumber() == null ? "" : user.getPhoneNumber(),
                        user.getPhotoUrl() == null ? "" : user.getPhotoUrl().toString(),
                        new Date(user.getMetadata().getCreationTimestamp()),
                        new Date(user.getMetadata().getLastSignInTimestamp()));

                SharedRefUtils.saveEmail(user.getEmail(), LoginActivity.this);
                SharedRefUtils.saveUid(user.getUid(), LoginActivity.this);

                InsertUserTask insertUserTask = new InsertUserTask(requestUser);
                insertUserTask.execute();
            }

            @Override
            public void fail() {
                ActivityUtils.openNetWork(LoginActivity.this);
            }
        });

        authen.init();
    }


    @Override
    public void onBackPressed() {
        return;
    }


    @OnClick(R.id.btn_signin_where_login)
    public void clickButtonSignin(View view) {
        authen.signIn();
    }


    @Override
    protected void onStart() {
        super.onStart();
        authen.checkSignIn();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authen.getResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyPermission.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class InsertUserTask extends AsyncTask<Void, Void, Void> {

        private User user;

        public InsertUserTask(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // RidesharingDB.getInstance(getApplicationContext()).getUserDao().insertUsers(user);
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(ProfileFB.DB_IN_FB);
            dbref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProfileFB profile = dataSnapshot.getValue(ProfileFB.class);
                    if (profile != null && profile.getUid() != null) {
                        DataManager.getInstance().setProfile(profile);
                        ActivityUtils.changeActivity(LoginActivity.this, MainActivity.class);
                        DBUtils.InsertProfileTask insertProfileTask = new DBUtils.InsertProfileTask(LoginActivity.this, profile);
                        insertProfileTask.execute();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ActivityUtils.displayToast(LoginActivity.this, "Error firebase");

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(ProfileFB.DB_IN_FB);
//            dbref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    ProfileFB profile = dataSnapshot.getValue(ProfileFB.class);
//                    if (profile != null && profile.getUid() != null) {
//                        DataManager.getInstance().setProfile(profile);
//                        ActivityUtils.changeActivity(LoginActivity.this, MainActivity.class);
//                        DBUtils.InsertProfileTask insertProfileTask = new DBUtils.InsertProfileTask(LoginActivity.this, profile);
//                        insertProfileTask.execute();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    ActivityUtils.displayToast(LoginActivity.this, "Error firebase");
//
//                }
//            });
        }
    }

    // Load data into the viewpager
    public void loadData() {

        int[] header = {R.string.onboard_secure_title, R.string.onboard_ux_title, R.string.onboard_money_title };
        int[] desc = {R.string.onboard_secure_description, R.string.onboard_ux_description, R.string.onboard_money_description};
        int[] imageId = {R.drawable.bg_security, R.drawable.bg_easy_to_use, R.drawable.bg_saving_money};

        for(int i=0;i<imageId.length;i++)
        {
            OnBoardItem item=new OnBoardItem();
            item.setImageID(imageId[i]);
            item.setTitle(getResources().getString(header[i]));
            item.setDescription(getResources().getString(desc[i]));

            onBoardItems.add(item);
        }
    }

    // setup the
    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(LoginActivity.this, R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(LoginActivity.this, R.drawable.selected_item_dot));
    }

}
