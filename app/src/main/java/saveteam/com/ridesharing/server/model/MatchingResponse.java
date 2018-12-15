package saveteam.com.ridesharing.server.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchingResponse {

    @SerializedName("keyQuery")
    @Expose
    private String keyQuery;
    @SerializedName("similarSet")
    @Expose
    private List<Integer> similarSet = null;

    public String getKeyQuery() {
        return keyQuery;
    }

    public void setKeyQuery(String keyQuery) {
        this.keyQuery = keyQuery;
    }

    public List<Integer> getSimilarSet() {
        return similarSet;
    }

    public void setSimilarSet(List<Integer> similarSet) {
        this.similarSet = similarSet;
    }

}