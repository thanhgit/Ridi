package saveteam.com.ridesharing.utils.activity;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedRefUtils {
    public static final String REF_UID = "uid";
    public static final String REF_EMAIL = "email";
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
        return SharedRefUtils.getInstance(context).getString(REF_EMAIL, "thanh29695@gmail.com");
    }

    public static void saveUid(String uid, Context context) {
        SharedPreferences.Editor editor = SharedRefUtils.getInstance(context).edit();
        editor.putString(REF_UID, uid);
        editor.commit();
    }

    public static String getUid(Context context) {
        return SharedRefUtils.getInstance(context).getString(REF_UID, "0000");
    }

}
