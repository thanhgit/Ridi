package saveteam.com.ridesharing.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchingForSearchResponse implements Serializable {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("users")
    @Expose
    private List<String> users = null;
    @SerializedName("percents")
    @Expose
    private List<Double> percents = null;

    public MatchingForSearchResponse() {
        users = new ArrayList<>();
        percents = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<Double> getPercents() {
        return percents;
    }

    public void setPercents(List<Double> percents) {
        this.percents = percents;
    }

}