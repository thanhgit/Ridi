package saveteam.com.ridesharing.model;

import java.io.Serializable;
import java.util.List;

public class FindTripDTO implements Serializable {
    private Trip tripSearch;
    private List<MatchingDTO> matchingDTOS;

    public FindTripDTO(Trip tripSearch, List<MatchingDTO> matchingDTOS) {
        this.tripSearch = tripSearch;
        this.matchingDTOS = matchingDTOS;
    }

    public Trip getTripSearch() {
        return tripSearch;
    }

    public void setTripSearch(Trip tripSearch) {
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
            if (matchingDTO.getUserName().equals(uid)) {
                return matchingDTO.getPercent();
            }
        }

        return 0;
    }
}
