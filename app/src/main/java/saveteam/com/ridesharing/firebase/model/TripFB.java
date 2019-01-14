package saveteam.com.ridesharing.firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

import saveteam.com.ridesharing.model.Geo;

@IgnoreExtraProperties
public class TripFB implements Serializable {
    public static final String DB_IN_FB = "offertrips";

    private String uid;
    private int size;
    private Geo geoStart;
    private Geo geoEnd;
    private List<Geo> paths;

    public TripFB() {
    }

    public TripFB(String uid, int size, Geo geoStart, Geo geoEnd, List<Geo> paths) {
        this.uid = uid;
        this.size = size;
        this.geoStart = geoStart;
        this.geoEnd = geoEnd;
        this.paths = paths;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Geo getGeoStart() {
        return geoStart;
    }

    public void setGeoStart(Geo geoStart) {
        this.geoStart = geoStart;
    }

    public Geo getGeoEnd() {
        return geoEnd;
    }

    public void setGeoEnd(Geo geoEnd) {
        this.geoEnd = geoEnd;
    }

    public List<Geo> getPaths() {
        return paths;
    }

    public void setPaths(List<Geo> paths) {
        this.paths = paths;
    }
}
