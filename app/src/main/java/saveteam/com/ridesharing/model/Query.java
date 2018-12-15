package saveteam.com.ridesharing.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Query {
    public String key;
    public Trip trip;

    public Query() {
    }

    public Query(String key, Trip trip) {
        this.key = key;
        this.trip = trip;
    }
}
