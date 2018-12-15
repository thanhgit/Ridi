package saveteam.com.ridesharing.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.model.Geo;

public class ActivityUtils {
    public static final String TAG = "thanhuit";

    public static void displayLog(String message) {
        Log.d(TAG, message);
    }

    public static void displayToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

    }

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    public static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE};


    public static void changeActivity(AppCompatActivity app, Class<?> cls) {
        Intent changeActivity = new Intent(app.getApplicationContext(), cls);
        app.startActivity(changeActivity);
    }

    public static Image getMarker(Context context, int res, int color) {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources() ,res);

        Bitmap source = changeBitmapColor(sourceBitmap, color);
        Image marker_img = new Image();
        marker_img.setBitmap(source);
        return marker_img;
    }

    public static Image getMarker(int res) {
        Image image = new Image();
        try {
            image.setImageResource(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Image getMarker() {
        Image image = new Image();

        try {
            image.setImageResource(R.drawable.marker);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
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
    public static List<GeoCoordinate> convertFrom(List<Geo> geos) {
        List<GeoCoordinate> result = new ArrayList<>();
        for (Geo geo : geos) {
            result.add(new GeoCoordinate(geo.lat, geo.lng));
        }
        return result;
    }

    public static GeoCoordinate convertFrom(Geo geo) {
        return new GeoCoordinate(geo.lat, geo.lng);
    }

    /**
     * Convert from GeoCoordinate to Geo
     */
    public static List<Geo> convertToGeo(List<GeoCoordinate> geos) {
        List<Geo> result = new ArrayList<>();

        for (GeoCoordinate geo : geos) {
            long cellId = S2Utils.getCellId(geo.getLatitude(), geo.getLongitude()).id();
            result.add(new Geo(geo.getLatitude(), geo.getLongitude(), cellId));
        }
        return result;
    }

    public static Geo convertToGeo(GeoCoordinate geo) {
        long cellId = S2Utils.getCellId(geo.getLatitude(), geo.getLongitude()).id();
        return new Geo(geo.getLatitude(), geo.getLongitude(), cellId);
    }

    public static int randomColorArgb() {
        int R = (int)(Math.random()*256);
        int G = (int)(Math.random()*256);
        int B= (int)(Math.random()*256);
        return Color.argb(100,R,G,B);
    }
}
