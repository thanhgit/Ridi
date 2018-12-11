package saveteam.com.ridesharing;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ui.SupportVectorDrawablesButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.gofynd.gravityview.GravityView;
import saveteam.com.ridesharing.utils.ActivityUtils;
import saveteam.com.ridesharing.utils.MyGoogleAuthen;

import static saveteam.com.ridesharing.utils.MyGoogleAuthen.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.imgView_where_login)
    ImageView imgView;
    @BindView(R.id.btn_signin_where_login)
    SupportVectorDrawablesButton btnSignin;

    GravityView gravityView;

    MyGoogleAuthen authen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initGravityView();

        authen = new MyGoogleAuthen(this, new MyGoogleAuthen.CheckSignInListener() {
            @Override
            public void success(FirebaseUser user) {
                ActivityUtils.changeActivity(LoginActivity.this, HomeActivity.class);
            }

            @Override
            public void fail() {
                ActivityUtils.displayToast(LoginActivity.this, "Fail to authentication");
            }
        });

        authen.init();
    }

    private void initGravityView() {
        gravityView = GravityView.getInstance(this)
                .setImage(imgView, R.drawable.bg_where_login)
                .center();
        if (!gravityView.deviceSupported()) {
            ActivityUtils.displayToast(this, "Gyroscope sensor not available in your device");
        }
    }

    @OnClick(R.id.btn_signin_where_login)
    public void clickButtonSignin(View view) {
        authen.signIn();
    }


    @Override
    protected void onStart() {
        super.onStart();
//        authen.checkSignIn();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authen.getResult(requestCode, resultCode, data);
    }

}
