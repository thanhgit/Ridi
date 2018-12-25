package saveteam.com.ridesharing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Trip implements Serializable {
    public String userName;
    public List<Geo> path;
    public Geo startGeo;
    public Geo endGeo;

    public Trip() {
    }

    public Trip(String userName, List<Geo> path, Geo startGeo, Geo endGeo) {
        this.userName = userName;
        this.path = path;
        this.startGeo = startGeo;
        this.endGeo = endGeo;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "userName='" + userName + '\'' +
                ", path=" + path +
                ", startGeo=" + startGeo +
                ", endGeo=" + endGeo +
                '}';
    }
}
