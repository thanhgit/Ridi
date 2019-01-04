package saveteam.com.ridesharing.presentation.splashscreen;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.presentation.LoginActivity;
import saveteam.com.ridesharing.presentation.home.MainActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.layout_where_splash)
    RelativeLayout layout;
    @BindView(R.id.circle_loading_view_where_splash)
    AnimatedCircleLoadingView progress_loading;

    String email = "";
    String uid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uid = SharedRefUtils.getUid(this);
        email = SharedRefUtils.getEmail(this);

        if (ActivityUtils.checkInternetConnection(this)) {
            StartTask startTask = new StartTask(this,progress_loading, layout, email, uid);
            startTask.execute();
        } else {
            ActivityUtils.openNetWork(this);
        }

    }

    private static class StartTask extends AsyncTask<Void, Integer, Void> {
        Activity context;
        AnimatedCircleLoadingView progress;
        RelativeLayout layout;
        String email;
        String uid;

        public StartTask(Activity context, AnimatedCircleLoadingView progress, RelativeLayout layout, String email, String uid) {
            this.context = context;
            this.progress = progress;
            this.layout = layout;
            this.email = email;
            this.uid = uid;
        }

        @Override
        protected void onPreExecute() {
            this.progress.startDeterminate();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            this.progress.stopOk();
            ActivityUtils.displayToast(context, "Done" );
            if (ActivityUtils.checkInternetConnection(context)) {
                this.progress.stopOk();

                Handler handler = new Handler();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(!email.equals("") && !uid.equals("")) {
                            ActivityUtils.changeActivity(context, MainActivity.class);
                        } else {
                            ActivityUtils.changeActivity(context, LoginActivity.class);
                        }
                    }
                };

                handler.postDelayed(runnable, 3500);


            } else {
                this.progress.stopFailure();
                ActivityUtils.openNetWork(context);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int value = values[0];
            progress.setPercent(value);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int index = 0 ; index <= 100; index+=5) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(index);
            }

            return null;
        }
    }
}
