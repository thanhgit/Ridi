package saveteam.com.ridi.server.model.matching;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import saveteam.com.ridi.model.Geo;

public class StartGeo {

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("cellId")
    @Expose
    private Long cellId;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Long getCellId() {
        return cellId;
    }

    public void setCellId(Long cellId) {
        this.cellId = cellId;
    }

    public Geo toGeo() {
        return new Geo(this.lat, this.lng, this.cellId);
    }
}