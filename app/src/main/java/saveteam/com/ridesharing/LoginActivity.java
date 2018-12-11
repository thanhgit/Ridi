package saveteam.com.ridesharing;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.gofynd.gravityview.GravityView;
import saveteam.com.ridesharing.utils.ActivityUtils;
import saveteam.com.ridesharing.utils.MyGoogleAuthen;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.btn_signin_where_login)
    SignInButton btnSignIn;
    @BindView(R.id.imgView_where_login)
    ImageView imgView;

    MyGoogleAuthen authen;
    GravityView gravityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initGravityView();
        initGoogleAuthen();

    }

    private void initGravityView() {
        gravityView = GravityView.getInstance(this)
                .setImage(imgView, R.drawable.bg_where_login)
                .center();
        if (!gravityView.deviceSupported()) {
            ActivityUtils.displayToast(this, "Gyroscope sensor not available in your device");
        }
    }

    private void initGoogleAuthen(){
        authen = new MyGoogleAuthen(this);
        authen.init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        authen.checkSignIn(new MyGoogleAuthen.CheckSignInListener() {
            @Override
            public void success(GoogleSignInAccount account) {
                loginSuccess();
            }

            @Override
            public void fail() {
                loginFail();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gravityView.registerListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gravityView.unRegisterListener();
    }

    @OnClick(R.id.btn_signin_where_login)
    public void clickBtnSignin(View view) {
        authen.signIn();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        authen.getResult(requestCode, resultCode, data, new MyGoogleAuthen.CheckSignInListener() {
            @Override
            public void success(GoogleSignInAccount account) {
                loginSuccess();
            }

            @Override
            public void fail() {
                loginFail();
            }
        });
    }

    private void loginSuccess() {
        ActivityUtils.changeActivity(this, HomeActivity.class);
    }

    private void loginFail() {

    }
}
