package saveteam.com.ridesharing.utils.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;

import java.io.File;

public abstract class BasicMapActivity extends AppCompatActivity {

    public Map map = null;
    public MapFragment mapFragment = null;
    private GeoCoordinate center;

    public abstract void addInteraction();
    public abstract void addView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addView();
        initialize();
    }

    private void initialize() {
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "saveteam.com.ridesharing.MapService");

        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        map = mapFragment.getMap();
                        addInteraction();
                        map.setCenter(center != null ? center : (new GeoCoordinate(10.7859988, 106.7340538, 0.0)),
                                Map.Animation.NONE);
                        map.setZoomLevel(17);
                    } else {
                        System.out.println("ERROR: Cannot initialize Map Fragment");
                    }
                }
            });
        }
    }

    public void setCenter(GeoCoordinate center) {
        this.center = center;
    }
}