package saveteam.com.ridesharing.utils.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import saveteam.com.ridesharing.presentation.LoginActivity;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;

public class SharedRefUtils {
    public static final String REF_UID = "uid";
    public static final String REF_EMAIL = "email";
    public static final String REF_ONBOARDING = "onboarding";
    public static final String REF_APPS = "refapps";

    private static SharedPreferences sharedpreferences;

    public static SharedPreferences getInstance(Context context) {
        if (sharedpreferences == null) {
            sharedpreferences = context.getSharedPreferences(REF_APPS,
                    Context.MODE_PRIVATE);
        }

        return sharedpreferences;
    }

    public static void saveEmail(String email, Context context) {
        SharedPreferences.Editor editor = SharedRefUtils.getInstance(context).edit();
        editor.putString(REF_EMAIL, email);
        editor.commit();
    }

    public static String getEmail(Context context) {
        return SharedRefUtils.getInstance(context).getString(REF_EMAIL, "");
    }

    public static void saveUid(String uid, Context context) {
        SharedPreferences.Editor editor = SharedRefUtils.getInstance(context).edit();
        editor.putString(REF_UID, uid);
        editor.commit();
    }

    public static String getUid(Context context) {
        return SharedRefUtils.getInstance(context).getString(REF_UID, "");
    }

    public static void saveOnboarding(Context context) {
        SharedPreferences.Editor editor = SharedRefUtils.getInstance(context).edit();
        editor.putBoolean(REF_ONBOARDING, false);
        editor.commit();
    }

    public static boolean isOnboarding(Context context) {
        return SharedRefUtils.getInstance(context).getBoolean(REF_ONBOARDING, true);
    }

    public static void signout(final Activity activity) {
        MyGoogleAuthen.signOut(activity, new MyGoogleAuthen.LogoutCompleteListener() {
            @Override
            public void done() {
                SharedRefUtils.saveEmail("", activity.getApplicationContext());
                SharedRefUtils.saveUid("", activity.getApplicationContext());

                Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            }
        });
    }

}
