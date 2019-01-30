package saveteam.com.ridesharing.model;

import java.io.Serializable;
import java.util.List;

import saveteam.com.ridesharing.firebase.model.TripFB;

public class FindTripDTO implements Serializable {
    private TripFB tripSearch;
    private List<MatchingDTO> matchingDTOS;

    public FindTripDTO(TripFB tripSearch, List<MatchingDTO> matchingDTOS) {
        this.tripSearch = tripSearch;
        this.matchingDTOS = matchingDTOS;
    }

    public TripFB getTripSearch() {
        return tripSearch;
    }

    public void setTripSearch(TripFB tripSearch) {
        this.tripSearch = tripSearch;
    }

    public List<MatchingDTO> getMatchingDTOS() {
        return matchingDTOS;
    }

    public void setMatchingDTOS(List<MatchingDTO> matchingDTOS) {
        this.matchingDTOS = matchingDTOS;
    }

    public double getPercentByUid(String uid) {
        for (MatchingDTO matchingDTO : matchingDTOS) {
            if (matchingDTO.getUserId().equals(uid)) {
                return matchingDTO.getPercent();
            }
        }

        return 0;
    }
}
