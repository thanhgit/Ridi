package saveteam.com.ridesharing.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class Matching implements Serializable {
    public String keyQuery;
    public List<Integer> similarSet;

    public Matching() {
    }

    public Matching(String keyQuery, List<Integer> similarSet) {
        this.keyQuery = keyQuery;
        this.similarSet = similarSet;
    }
}
