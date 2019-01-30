package saveteam.com.ridesharing.utils.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.location.Location;
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

import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.utils.google.S2Utils;

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

    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color)
    {
        Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(),true);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }

    /**
     * Convert from Geo to GeoCoordinate
     */
    public static List<LatLng> convertFrom(List<Geo> geos) {
        List<LatLng> result = new ArrayList<>();
        for (Geo geo : geos) {
            result.add(new LatLng(geo.lat, geo.lng));
        }
        return result;
    }

    public static LatLng convertFrom(Geo geo) {
        return new LatLng(geo.lat, geo.lng);
    }

    /**
     * Convert from GeoCoordinate to Geo
     */
    public static List<Geo> convertToGeo(List<LatLng> geos) {
        List<Geo> result = new ArrayList<>();

        for (LatLng geo : geos) {
            long cellId = S2Utils.getCellId(geo.latitude, geo.longitude).id();
            result.add(new Geo(geo.latitude, geo.longitude, cellId));
        }
        return result;
    }

    public static Geo convertToGeo(LatLng geo) {
        long cellId = S2Utils.getCellId(geo.latitude, geo.longitude).id();
        return new Geo(geo.latitude, geo.longitude, cellId);
    }

    /**
     * Computing distance between 2 geo
     */

    public static float distanceBetween2Geo(Geo geo1, Geo geo2) {
        Location loc1 = new Location("");
        loc1.setLatitude(geo1.lat);
        loc1.setLongitude(geo1.lng);

        Location loc2 = new Location("");
        loc2.setLatitude(geo2.lat);
        loc2.setLongitude(geo2.lng);

        return  loc1.distanceTo(loc2);
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

    /**
     * Get now
     */

    public static String getNow() {
        SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        return myDateFormat.format(new Date());
    }

    /**
     * Date time format
     */

    public static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
    }

    public static SimpleDateFormat getTimeFormat() {
        return new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
    }
}
