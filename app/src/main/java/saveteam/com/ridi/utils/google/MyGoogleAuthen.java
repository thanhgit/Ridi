package saveteam.com.ridi.utils.google;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MyGoogleAuthen {
    public static int RC_SIGN_IN = 9001;
    private Activity mActivity;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CheckSignInListener checkSignInListener;

    public interface CheckSignInListener {
        /**
         * if exist account
         */
        void success(FirebaseUser user);

        /**
         * if not exist account
         */
        void fail();
    }

    public MyGoogleAuthen(Activity activity, CheckSignInListener checkSignInListener) {
        this.mActivity = activity;
        this.checkSignInListener = checkSignInListener;
    }

    public void init() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("776097512960-gs1h0cvjd2e89maftdd3nl3rh71gvaj8.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this.mActivity, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        return user;
    }

    public void checkSignIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            checkSignInListener.fail();
        } else {
            checkSignInListener.success(currentUser);
        }
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void logout(final LogoutCompleteListener logoutCompleteListener) {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(mActivity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        logoutCompleteListener.done();
                    }
                });
    }

    public void getResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    checkSignInListener.success(user);
                                } else {
                                    checkSignInListener.fail();
                                }
                            }
                        });
            } catch (ApiException e) {
                checkSignInListener.fail();
            }
        }
    }

    public interface LogoutCompleteListener {
        void done();
    }

    public static void signOut(Activity activity, final LogoutCompleteListener listener ) {
        MyGoogleAuthen authen = new MyGoogleAuthen(activity, null);
        authen.init();
        authen.logout(listener);
    }

    public static FirebaseUser getCurrentUser(Activity activity) {
        MyGoogleAuthen authen = new MyGoogleAuthen(activity, null);
        authen.init();

        return authen.getCurrentUser();
    }
}
