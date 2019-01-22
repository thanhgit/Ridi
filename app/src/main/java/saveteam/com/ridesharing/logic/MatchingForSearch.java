package saveteam.com.ridesharing.logic;

import retrofit2.Call;
import retrofit2.Callback;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.model.MatchingForSearchResponse;
import saveteam.com.ridesharing.server.model.QueryRequest;

public class MatchingForSearch {
    private double threshold;
    private Query query;

    public MatchingForSearch(double threshold, Query query) {
        this.threshold = threshold;
        this.query = query;
    }

    public void matching(Callback<MatchingForSearchResponse> listener) {
        QueryRequest queryRequest = new QueryRequest(threshold, query);

        Call<MatchingForSearchResponse> matchingResponseCall = ApiUtils.getUserClient().matchingForSearch(queryRequest);
        matchingResponseCall.enqueue(listener);
    }
}
