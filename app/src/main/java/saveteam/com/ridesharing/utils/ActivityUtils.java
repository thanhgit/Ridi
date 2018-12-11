package saveteam.com.ridesharing.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class ActivityUtils {
    public static void changeActivity(Activity activityFrom, Class activityTo) {
        Intent intent = new Intent(activityFrom, activityTo);
        activityFrom.startActivity(intent);
    }

    public static void displayToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

    }
}
