package saveteam.com.ridi.firebase.model;


import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

import saveteam.com.ridi.model.Geo;

@IgnoreExtraProperties
public class TrackingFB implements Serializable {
    public static final String DB_IN_FB = "trackingv1";

    @NonNull
    private String uid;

    private List<Geo> paths;

    public TrackingFB() {
    }

    @Ignore
    public TrackingFB(@NonNull String uid, List<Geo> paths) {
        this.uid = uid;
        this.paths = paths;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public List<Geo> getPaths() {
        return paths;
    }

    public void setPaths(List<Geo> paths) {
        this.paths = paths;
    }
}
