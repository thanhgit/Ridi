package saveteam.com.ridesharing.model;

import java.io.Serializable;
import java.util.List;

public class MatchingDTO implements Serializable {
    List<Trip> trips;

    public MatchingDTO(List<Trip> trips) {
        this.trips = trips;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
}
