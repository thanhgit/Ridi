package saveteam.com.quagiang.utils.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import saveteam.com.quagiang.model.Geo;

public class LocationUtils {
    private Activity activity;
    private LocationManager locationManager;
    private GpsListener listener;

    public interface GpsListener{
        void onLocationChanged(Location loc);
        void onFail();
    }

    public LocationUtils(Activity activity, GpsListener listener) {
        this.activity = activity;
        this.listener = listener;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    public void Gps() {
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (listener != null) {
                listener.onFail();
            }
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 20, locationListener);

    }

    private class MyLocationListener implements LocationListener {
        private Location lastLocation;
        @Override
        public void onLocationChanged(Location loc) {
            if (lastLocation == null) {
                lastLocation = loc;
            }

            if (listener != null && MapUtils.distanceBetween2Geo(new Geo(lastLocation.getLatitude(), lastLocation.getLongitude(), 0),
                    new Geo(loc.getLatitude(), loc.getLongitude(), 0)) > 20) {
                lastLocation = loc;
                listener.onLocationChanged(loc);
                ActivityUtils.displayLog(loc.getLatitude()+" , "+loc.getLongitude());
            }

//            Toast.makeText(
//                    activity,
//                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
//                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();

            /*------- To get city name from coordinates -------- */
//            String cityName = null;
//            Geocoder gcd = new Geocoder(activity, Locale.getDefault());
//            List<Address> addresses;
//            try {
//                addresses = gcd.getFromLocation(loc.getLatitude(),
//                        loc.getLongitude(), 1);
//                if (addresses.size() > 0) {
//                    System.out.println(addresses.get(0).getLocality());
//                    cityName = addresses.get(0).getLocality();
//                    Toast.makeText(
//                            activity, cityName, Toast.LENGTH_SHORT).show();
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
