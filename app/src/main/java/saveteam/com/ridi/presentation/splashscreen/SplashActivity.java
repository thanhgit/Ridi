package saveteam.com.ridi.presentation.splashscreen;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridi.R;
import saveteam.com.ridi.firebase.model.ProfileFB;
import saveteam.com.ridi.presentation.home.MainActivity;
import saveteam.com.ridi.utils.MyPermission;
import saveteam.com.ridi.utils.activity.ActivityUtils;
import saveteam.com.ridi.utils.activity.DataManager;
import saveteam.com.ridi.utils.activity.SharedRefUtils;

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.layout_where_splash)
    RelativeLayout layout;

    String uid = "";

    private Merlin network;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        MyPermission.getInstance(this).requestPermission(ActivityUtils.SPLASH_REQUIRED_PERMISSIONS);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        initNetwork();

        action();
    }

    private void action() {
        if (ActivityUtils.checkInternetConnection(this)) {
            uid = SharedRefUtils.getUid(this);

            StartTask startTask = new StartTask(this, uid, new StartTask.ProfileListener() {
                @Override
                public void success(final ProfileFB profile) {
                    if (uid.trim().equals("")) {
                        ActivityUtils.changeActivityTop(SplashActivity.this, LoginActivity.class);
                    } else {
                        if (ActivityUtils.checkInternetConnection(SplashActivity.this)) {

                            DataManager.getInstance().setProfile(profile);
                            Handler handler = new Handler();

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (profile != null && profile.getUid() != null && !SharedRefUtils.isOnboarding(SplashActivity.this)) {
                                        ActivityUtils.changeActivityTop(SplashActivity.this, MainActivity.class);
                                    } else if(SharedRefUtils.isOnboarding(SplashActivity.this)) {
                                        ActivityUtils.changeActivityTop(SplashActivity.this, OnBoardingActivity.class);
                                    } else  {
                                        ActivityUtils.changeActivityTop(SplashActivity.this, LoginActivity.class);
                                    }
                                }
                            };

                            handler.postDelayed(runnable, 3500);
                        } else {
                            ActivityUtils.openNetWork(SplashActivity.this);
                        }
                    }


                }

                @Override
                public void fail() {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            ActivityUtils.changeActivityTop(SplashActivity.this, LoginActivity.class);
                        }
                    };
                    handler.postDelayed(runnable, 3500);
                }
            });
            startTask.execute();
        } else {
            ActivityUtils.openNetWork(this);
        }
    }


    private void initNetwork() {
        network = new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .build(this);

        network.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                onResume();
            }
        });

        network.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                ActivityUtils.openNetWork(SplashActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        action();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyPermission.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static class StartTask extends AsyncTask<Void, Integer, Void> {
        Activity context;
        String uid;
        ProfileListener listener;

        public interface ProfileListener {
            void success(ProfileFB profile);
            void fail();
        }

        public StartTask(Activity context,  String uid,ProfileListener listener) {
            this.context = context;
            this.uid = uid;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(ProfileFB.DB_IN_FB);
            dbref.child(this.uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProfileFB profile = dataSnapshot.getValue(ProfileFB.class);
                    if (profile != null) {
                        listener.success(profile);
                    } else {
                        listener.fail();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.fail();
                    ActivityUtils.displayToast(context, "Error firebase");

                }
            });

            return null;
        }
    }
}
