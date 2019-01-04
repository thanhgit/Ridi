package saveteam.com.ridesharing.presentation.profile;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.presentation.home.MainActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;

public class ActivateCodeActivity extends AppCompatActivity {
    @BindView(R.id.txt_activate_code_where_activate_code)
    EditText txt_activate_code;
    @BindView(R.id.btn_activate_where_activate_code)
    AppCompatButton btn_activate;
    @BindView(R.id.tv_resend_where_activate_code)
    TextView tv_resend;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    String verificationId = "";
    String phoneNumber = "";

    ProfileFB profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_code);
        ButterKnife.bind(this);

        profile = (ProfileFB) getIntent().getSerializableExtra("profile");

        if (profile != null) {
            phoneNumber = "+84"+profile.getPhone();
        }

        genPhoneCode();
    }

    @OnClick(R.id.btn_activate_where_activate_code)
    public void clickActivate(View view) {
        if (!txt_activate_code.getText().toString().trim().equals("")) {
            if (!txt_activate_code.getText().toString().trim().equals("")) {
                FirebaseAuth.getInstance().signInWithCredential(PhoneAuthProvider.getCredential(verificationId, txt_activate_code.getText().toString()))
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference db = FirebaseDB.getInstance().child("profiles");
                                    db.child(profile.getUid()).setValue(profile);
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
                                    ActivityUtils.changeActivity(ActivateCodeActivity.this, MainActivity.class);
                                } else {
                                    ActivityUtils.displayToast(ActivateCodeActivity.this, "Code is false");
                                }
                            }
                        });
            } else {
                ActivityUtils.displayToast(this, "Code is false");
            }
        }
    }

    @OnClick(R.id.tv_resend_where_activate_code)
    public void clickResend(View view) {
        ActivityUtils.displayToast(this, "Resend code to your mobile phone");
        genPhoneCode();
    }

    public void genPhoneCode() {
        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
        phoneAuthProvider.verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                this, /* activity */
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        ActivityUtils.changeActivity(ActivateCodeActivity.this, MainActivity.class);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        ActivityUtils.displayToast(ActivateCodeActivity.this, "false");
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationId = s;
                        super.onCodeSent(s, forceResendingToken);
                    }
                });

    }

}
