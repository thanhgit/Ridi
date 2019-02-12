package saveteam.com.ridi.model.parse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private List<List<List<List<Float>>>> coordinates = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<List<List<Float>>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<List<Float>>>> coordinates) {
        this.coordinates = coordinates;
    }

}