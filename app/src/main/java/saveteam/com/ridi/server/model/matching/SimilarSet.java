package saveteam.com.ridi.server.model.matching;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import saveteam.com.ridi.model.Trip;

public class SimilarSet {

    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("path")
    @Expose
    private List<Path> path = null;
    @SerializedName("startGeo")
    @Expose
    private StartGeo startGeo;
    @SerializedName("endGeo")
    @Expose
    private EndGeo endGeo;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Path> getPath() {
        return path;
    }

    public void setPath(List<Path> path) {
        this.path = path;
    }

    public StartGeo getStartGeo() {
        return startGeo;
    }

    public void setStartGeo(StartGeo startGeo) {
        this.startGeo = startGeo;
    }

    public EndGeo getEndGeo() {
        return endGeo;
    }

    public void setEndGeo(EndGeo endGeo) {
        this.endGeo = endGeo;
    }

    public Trip toTrip() {
        Trip trip = new Trip();
        trip.userName = this.userName;
        trip.startGeo = this.startGeo.toGeo();
        trip.endGeo = this.endGeo.toGeo();
        trip.path = new ArrayList<>();
        for (Path geo : this.path){
            trip.path.add(geo.toGeo());
        }
        return trip;
    }

}

