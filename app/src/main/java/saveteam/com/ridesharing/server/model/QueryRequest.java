package saveteam.com.ridesharing.server.model;

import saveteam.com.ridesharing.model.Query;

public class QueryRequest {
    private double threshold;
    private Query query;

    public QueryRequest(double threshold, Query query) {
        this.threshold = threshold;
        this.query = query;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }
}
