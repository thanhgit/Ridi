package saveteam.com.ridesharing.presentation.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;

public class HomeDriveActivity extends HomeActivity {
    @BindView(R.id.btn_ride_where_home_drive)
    AppCompatButton btn_ride;
    @BindView(R.id.btn_post_as_drive_where_home)
    AppCompatButton btn_post_as_drive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drive);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_ride_where_home_drive)
    public void clickRide(View view) {
        ActivityUtils.changeActivity(this, HomeRideActivity.class);
    }

    @OnClick(R.id.btn_post_as_drive_where_home)
    public void clickPostAsDrive(View view) {

    }
}
