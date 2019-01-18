package saveteam.com.ridesharing.model;

import java.io.Serializable;
import java.util.List;

public class MatchingDTO implements Serializable {
    private String userName;
    private double percent;

    public MatchingDTO() {
    }

    public MatchingDTO(String userName, double percent) {
        this.userName = userName;
        this.percent = percent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
