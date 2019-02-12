package saveteam.com.ridi.model;

import java.io.Serializable;

import saveteam.com.ridi.utils.google.S2Utils;

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

    public String toStr() {
        return title+"|"+lat+"|"+lng;
    }

    public static Geo toParse(String str) {
        String[] tmp = str.split("\\|");
        Geo geo = new Geo(Double.parseDouble(tmp[1]), Double.parseDouble(tmp[2]),
                S2Utils.getCellId(Double.parseDouble(tmp[1]), Double.parseDouble(tmp[2])).id());
        geo.setTitle(tmp[0]);
        return geo;
    }
}
