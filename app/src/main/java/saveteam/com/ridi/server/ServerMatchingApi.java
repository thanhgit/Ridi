package saveteam.com.ridi.server;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import saveteam.com.ridi.server.model.MatchingForSearchResponse;
import saveteam.com.ridi.server.model.QueryRequest;
import saveteam.com.ridi.server.model.matching.MatchingResponse;

public interface ServerMatchingApi {

    @GET("v1/ridesharing/matching/schedule/{threshold}")
    Call<MatchingResponse> matchingForSchedule(@Path("threshold")String threshold);

    @POST("/v1/ridesharing/matching/search")
    Call<MatchingForSearchResponse> matchingForSearch(@Body QueryRequest query);
}
