package saveteam.com.ridi.server.model.matching;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import saveteam.com.ridi.model.Trip;

public class MatchingResponse {

    @SerializedName("keyQuery")
    @Expose
    private String keyQuery;
    @SerializedName("similarSet")
    @Expose
    private List<SimilarSet> similarSet = null;

    public String getKeyQuery() {
        return keyQuery;
    }

    public void setKeyQuery(String keyQuery) {
        this.keyQuery = keyQuery;
    }

    public List<SimilarSet> getSimilarSet() {
        return similarSet;
    }

    public void setSimilarSet(List<SimilarSet> similarSet) {
        this.similarSet = similarSet;
    }

    public List<Trip> getTrips() {
        List<Trip> trips = new ArrayList<>();
        for (SimilarSet item : this.similarSet) {
            trips.add(item.toTrip());
        }
        return trips;
    }

}


