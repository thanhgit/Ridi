package saveteam.com.ridi.database.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "searchplacehistories")
public class SearchPlaceHistory implements Serializable {
    @PrimaryKey
    @NonNull
    String title;

    double lat;
    double lng;
    long cellId;


    public SearchPlaceHistory() {
    }

    @Ignore
    public SearchPlaceHistory(double lat, double lng, long cellId, String title) {
        this.lat = lat;
        this.lng = lng;
        this.cellId = cellId;
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getCellId() {
        return cellId;
    }

    public void setCellId(long cellId) {
        this.cellId = cellId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
