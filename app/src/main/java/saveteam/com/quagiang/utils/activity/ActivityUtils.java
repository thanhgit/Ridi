package saveteam.com.quagiang.utils.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class ActivityUtils {
    public static final String TAG = "thanhuit";

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    public static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA
    };

    public static final String[] LOGIN_REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.INTERNET
    };

    public static final String[] MAIN_REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
    };

    public static final String[] PROFILE_REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final String[] SPLASH_REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.WRITE_SETTINGS,
    };

    /**
     * Display log
     */
    public static void displayLog(String message) {
        Log.d(TAG, message);
    }

    public static void displayToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }


    /**
     * Change activity
     */
    public static void changeActivity(Activity app, Class<?> cls) {
        Intent changeActivity = new Intent(app.getApplicationContext(), cls);
        app.startActivity(changeActivity);
    }

    public static void changeActivityTop(Activity app, Class<?> cls) {
        Intent changeActivity = new Intent(app.getApplicationContext(), cls);
        changeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        app.startActivity(changeActivity);
    }

    /**
     * Random utils
     */
    public static int randomColorArgb() {
        int R = (int)(Math.random()*256);
        int G = (int)(Math.random()*256);
        int B= (int)(Math.random()*256);
        return Color.argb(100,R,G,B);
    }

    /**
     * Network utils
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else if(Build.VERSION.SDK_INT >= 21){
            Network[] info = connectivity.getAllNetworks();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i] != null && connectivity.getNetworkInfo(info[i]).isConnected()) {
                        return true;
                    }
                }
            }
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
            final NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
            }
        }

        return false;
    }

    public static void openNetWork(final Activity context) {
        if (!ActivityUtils.checkInternetConnection(context)) {
            Snackbar snackbar = Snackbar.make(context.findViewById(android.R.id.content), "No internet", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Open", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
            snackbar.show();
        }
    }


    public interface OnOkClickListener {
        void onClick(DialogInterface dialog, int which);
    }

    public interface OnCancelClickListener {
        void onClick(DialogInterface dialog, int which);
    }

    public static void displayAlert(@NonNull final String title,@NonNull final String message,@NonNull final Activity context, final OnOkClickListener listenerOk, final OnCancelClickListener listenerCancel) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listenerOk.onClick(dialog, which);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listenerCancel.onClick(dialog, which);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    public static void displaySnackbar(final Activity context,View view, String message, String action) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });
        snackbar.show();
    }

    /**
     * keyboard
     */

    public static void showKeyboard(Activity activity, EditText txt) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txt, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboard(Activity activity, EditText txt) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
    }

    /**
     * Background processing
     */

    public static void backgroundProcess(Runnable runnable, int time) {
        Handler handler = new Handler();
        handler.postDelayed(runnable, time);
    }
}
