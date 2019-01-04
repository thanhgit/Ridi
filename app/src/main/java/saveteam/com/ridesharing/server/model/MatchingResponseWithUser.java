package saveteam.com.ridesharing.server.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchingResponseWithUser implements Serializable {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("users")
    @Expose
    private List<String> users = null;

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

}