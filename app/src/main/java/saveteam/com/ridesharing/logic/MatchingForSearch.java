package saveteam.com.ridesharing.logic;

import android.content.Intent;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.model.MatchingResponseWithUser;
import saveteam.com.ridesharing.server.model.QueryRequest;

public class MatchingForSearch {
    private double threshold;
    private Query query;

    public MatchingForSearch(double threshold, Query query) {
        this.threshold = threshold;
        this.query = query;
    }

    public void matching(Callback<MatchingResponseWithUser> listener) {
        QueryRequest queryRequest = new QueryRequest(threshold, query);

        Call<MatchingResponseWithUser> matchingResponseCall = ApiUtils.getUserClient().getMatchingFromPersonalResultUser(queryRequest);
        matchingResponseCall.enqueue(listener);
    }
}
