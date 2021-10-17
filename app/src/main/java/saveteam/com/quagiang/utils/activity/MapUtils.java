package saveteam.com.quagiang.utils.activity;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import saveteam.com.quagiang.model.Geo;
import saveteam.com.quagiang.utils.google.S2Utils;

public class MapUtils {
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
}
