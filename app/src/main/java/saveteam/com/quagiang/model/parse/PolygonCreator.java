package saveteam.com.quagiang.model.parse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PolygonCreator {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("geometries")
    @Expose
    private List<Geometry> geometries = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Geometry> getGeometries() {
        return geometries;
    }

    public void setGeometries(List<Geometry> geometries) {
        this.geometries = geometries;
    }

}