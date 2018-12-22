package saveteam.com.ridesharing.presentation.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.presentation.LoginActivity;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                SharedRefUtils.saveEmail("", HomeActivity.this);
                SharedRefUtils.saveUid("", HomeActivity.this);

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
