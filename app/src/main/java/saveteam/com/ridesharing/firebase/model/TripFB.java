package saveteam.com.ridesharing.firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

import saveteam.com.ridesharing.model.Geo;

@IgnoreExtraProperties
public class TripFB implements Serializable {
    public static final String DB_IN_FB = "offertripsv1";

    private String uid;
    private String userName;
    private int size;
    private Geo geoStart;
    private Geo geoEnd;
    private String startTime;
    private List<Geo> paths;

    public TripFB() {
    }

    public TripFB(String uid, String userName, int size, Geo geoStart, Geo geoEnd, String startTime, List<Geo> paths) {
        this.uid = uid;
        this.userName = userName;
        this.size = size;
        this.geoStart = geoStart;
        this.geoEnd = geoEnd;
        this.startTime = startTime;
        this.paths = paths;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
