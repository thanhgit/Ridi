package saveteam.com.ridesharing.model;

import java.io.Serializable;

public class Geo implements Serializable {
    public double lat;
    public double lng;
    public long cellId;
    public String title;
    public String time;

    public Geo() {
    }

    public Geo(double lat, double lng, long cellId) {
        this.lat = lat;
        this.lng = lng;
        this.cellId = cellId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "( "+lat+", "+lng+" )";
    }
}
