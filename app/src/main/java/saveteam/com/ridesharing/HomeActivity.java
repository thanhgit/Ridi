package saveteam.com.ridesharing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.utils.ActivityUtils;
import saveteam.com.ridesharing.utils.MyGoogleAuthen;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.btn_start_where_home)
    AppCompatButton btn_start;
    @BindView(R.id.btn_end_where_home)
    AppCompatButton btn_end;
    @BindView(R.id.btn_schedule_where_home)
    AppCompatButton btn_schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_start_where_home)
    public void clickButtonStart(View view) {
        ActivityUtils.changeActivity(this, SearchPlaceActivity.class);
    }

    @OnClick(R.id.btn_end_where_home)
    public void clickButtonEnd(View view){
        ActivityUtils.changeActivity(this, SearchPlaceActivity.class);

    }

    @OnClick(R.id.btn_schedule_where_home)
    public void clickButtonSchedule(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_signout_where_home_menu:
                signout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signout() {
        MyGoogleAuthen.signOut(this, new MyGoogleAuthen.LogoutCompleteListener() {
            @Override
            public void done() {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

}
