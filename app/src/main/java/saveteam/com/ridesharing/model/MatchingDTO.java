package saveteam.com.ridesharing.model;

import java.io.Serializable;

public class MatchingDTO implements Serializable {
    private String userId;
    private double percent;

    public MatchingDTO() {
    }

    public MatchingDTO(String userId, double percent) {
        this.userId = userId;
        this.percent = percent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
